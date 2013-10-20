/********************************************************************************
 * Author:  yucheng
 * Date:    2013/10/7
 * Description: ActiveMQ C#客户端
 ********************************************************************************/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Net;
using System.Net.Sockets;

using Apache.NMS;
using Apache.NMS.Util;

namespace MonitorClient
{
    /// <summary>
    /// 上传处理器，负责向服务器上传数据/事件
    /// </summary>
    class MQClient : Processor
    {
        private static IConnectionFactory connFac;

        private static IConnection connection;
        private static ISession session;
        private static IDestination destination;
        private static IMessageProducer startMsgPublisher, eventMsgPublisher;
        private static IMessageConsumer consumer;

        private static string AMQ_CONN_URI = "tcp://" + Global.ip + ":" + Global.port;

        // 客户端发送初始化消息到START_TOPIC上
        private const string START_TOPIC = "client-start";
        
        // 客户端发送性能数据事件到EVENT_TOPIC上
        private const string EVENT_TOPIC = "event";

        // 客户端订阅CHANGE_PERIOD_TOPIC接受服务端的监控周期改变指令
        private const string CHANGE_PERIOD_TOPIC = "change-period";

        #region 启动项目模块
        /// <summary>
        /// 通知服务器本客户端已经启动，主控方法
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ClientStartNotify(Processor sender, EventArgs e)
        {
            ITextMessage t = session.CreateTextMessage(Global.machineName);
            startMsgPublisher.Send(t);
        }

        #endregion

        /// <summary>
        /// 数据上传的主控方法
        /// </summary>
        /// <param name="sender">发送者</param>
        /// <param name="e">上传数据</param>
        private void PerformanceDataUpload(Processor sender, EventArgs e)
        {
            try
            {
                PerformanceDataUploadEvent pe = (PerformanceDataUploadEvent)e;
                InstanceValues instances = Global.categoryValues[pe.categoryName];
                foreach (string instanceName in instances.instanceNames)
                {
                    IMapMessage data = session.CreateMapMessage();
                    data.Properties.SetString("machineName", Global.machineName);
                    data.Properties.SetString("categoryName", pe.categoryName);
                    data.Properties.SetString("instanceName", instanceName);

                    data.Properties.SetString("timestamp", DateTime.Now.Subtract(new DateTime(1970,1,1)).TotalSeconds.ToString());
                    data.Body.SetString("value", instances.GetValue(instanceName).ToString());
                    eventMsgPublisher.Send(data);
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.ToString());
            }
        }

        /// <summary>
        /// 预处理，这是为了实现上传的消息循环
        /// </summary>
        /// <param name="e">事件信息</param>
        /// <returns>是否跳过处理方法</returns>
        protected override bool PretreatToSkip(EventArgs e)
        {
            if (e is PerformanceDataUploadEvent)
            {
                DateTime Now = DateTime.Now;
                PerformanceDataUploadEvent pe = (PerformanceDataUploadEvent)e;
                if (!Global.uploadPeriodTable.ContainsKey(pe.categoryName)) return true;
                try
                {
                    if (pe.sendDate.Add(Global.uploadPeriodTable[pe.categoryName]) <= Now)
                    {
                        pe.sendDate = Now;  //到达上传时间，预约下一次采样
                        SendEvent(this, pe);
                        return false;
                    }
                    else
                    {
                        SendEvent(this, pe);
                        return true;        //未达到上传时间，跳过处理线程
                    }
                }
                catch
                {
                    return true;
                }
            }
            return false;
        }

        /// <summary>
        /// 更改上传周期
        /// </summary>
        /// <param name="sender">发送者</param>
        /// <param name="e">内含上传周期表</param>
        private void ChangeUploadPeriod(Processor sender, EventArgs e)
        {
            try
            {
                ChangeUploadPeriodEvent ce = (ChangeUploadPeriodEvent)e;
                Dictionary<string, TimeSpan> upTable = ce.periodTable;
                foreach (string categoryName in upTable.Keys)
                {
                    if (Global.uploadPeriodTable.ContainsKey(categoryName))
                    {
                        if (upTable[categoryName] == TimeSpan.Zero)
                        {
                            Global.uploadPeriodTable[categoryName] = Timeout.InfiniteTimeSpan;
                        }
                        else
                        {
                            Global.uploadPeriodTable[categoryName] = upTable[categoryName];
                        }
                    }
                    else
                    {
                        SendEvent(this, new PerformanceDataUploadEvent(categoryName, DateTime.Now));    //上传对应内容
                        Global.uploadPeriodTable.Add(categoryName, upTable[categoryName]);
                    }
                }
            }
            catch
            {
                ChangeUploadPeriodError();
            }
        }

        /// <summary>
        /// 更改失败，可选
        /// </summary>
        private void ChangeUploadPeriodError()
        {
            //可选
        }

        #region 初始化
        /// <summary>
        /// 初始化并绑定事件处理方法
        /// </summary>
        public MQClient()
        {
            try
            {
                connFac = new NMSConnectionFactory(new Uri(AMQ_CONN_URI));

                connection = connFac.CreateConnection();//如果你是缺省方式启动Active MQ服务，则不需填用户名、密码

                //创建Session
                session = connection.CreateSession();

                //新建生产者对象
                startMsgPublisher = session.CreateProducer(SessionUtil.GetDestination(session, "topic://" + START_TOPIC));
                startMsgPublisher.DeliveryMode = MsgDeliveryMode.NonPersistent;//ActiveMQ服务器停止工作后，消息不再保留

                eventMsgPublisher = session.CreateProducer(SessionUtil.GetDestination(session, "topic://" + EVENT_TOPIC));
                eventMsgPublisher.DeliveryMode = MsgDeliveryMode.NonPersistent;//ActiveMQ服务器停止工作后，消息不再保留

                
                string selector = "machineName = '"+ Environment.MachineName + "'";
                consumer = session.CreateConsumer(
                    session.GetTopic(CHANGE_PERIOD_TOPIC)
                    , selector, false);

                //设置消息接收事件
                consumer.Listener += new MessageListener(OnMessage);

                //启动来自Active MQ的消息侦听
                connection.Start();

            }
            catch (Exception e)
            {
                Console.WriteLine(e.StackTrace);
            }

            eventHandlePeriod = new TimeSpan(0, 0, 0, 0, 100); //事件驱动事件为100ms
            eventHandleTable.Add(typeof(ClientStartNotificationEvent), this.ClientStartNotify);
            eventHandleTable.Add(typeof(PerformanceDataUploadEvent), this.PerformanceDataUpload);
            eventHandleTable.Add(typeof(ChangeUploadPeriodEvent), this.ChangeUploadPeriod);
        }
        #endregion

        protected void OnMessage(IMessage receivedMsg)
        {
            //接收消息

            IMapMessage m = receivedMsg as IMapMessage;
            Dictionary<string, TimeSpan> dict = new Dictionary<string, TimeSpan>();
            foreach (string key in m.Body.Keys)
            {
                dict.Add(key, new TimeSpan(0,0,m.Body.GetInt(key)));
            }
            SendEvent(Global.hub, new ServerEvent(new ChangeUploadPeriodEvent(dict)));
        }
    }
}
