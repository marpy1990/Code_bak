/********************************************************************************
 * Author:  marpy
 * Date:    2013/10/4
 * Description: 数据上传模块，向服务器传输数据/事件。
 ********************************************************************************/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net;
using System.Net.Sockets;

namespace MonitorClient
{
    /// <summary>
    /// 上传处理器，负责向服务器上传数据/事件
    /// </summary>
    class Upload : Processor
    {
        #region 启动项目模块
        /// <summary>
        /// 通知服务器本客户端已经启动，主控方法
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ClientStartNotify(Processor sender, EventArgs e)
        {
            string sendText = ClientStartNotifyText();
            ConnectServerToSendText(sendText);
        }

        private string ClientStartNotifyText()
        {
            /********************************************************************
             * 告诉服务器本客户端已经启动，内容就是这个函数的返回值。
             * 服务器收到这个消息后，必须给本客户端发送一个<监视项目,上传周期>表。
             * 在Download模块会接收这张表。
             ********************************************************************/
            return null;
        }
        #endregion

        #region 性能上传模块
        /// <summary>
        /// 连接服务器并上传Text，之后关闭与服务器的连接
        /// </summary>
        /// <param name="Text">上传内容</param>
        void ConnectServerToSendText(string sendText)
        {
            try
            {
                IPAddress serverIP = IPAddress.Parse(Global.ip);
                IPEndPoint iep = new IPEndPoint(serverIP, Global.uploadPort);

                Socket socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);

                socket.Connect(iep);
                byte[] buffersend = System.Text.Encoding.Default.GetBytes(sendText);
                socket.Send(buffersend);
                socket.Shutdown(SocketShutdown.Both);
                socket.Close();
            }
            catch
            {
                ConnectServerToSendTextError();
            }
        }

        /// <summary>
        /// 连接服务器异常处理，可选
        /// </summary>
        void ConnectServerToSendTextError()
        {
            //可选
        }

        /// <summary>
        /// 上传的数据格式
        /// </summary>
        private class UploadData
        {
            public string machineName;  //机器名
            public string categoryName; //监视项目名
            public string instanceName; //实例名
            public DateTime date;       //上传日期
            public object value;        //值（泛型）
            public UploadData(string machineName, string categoryName, string instanceName, DateTime date, object value)
            {
                this.machineName = machineName;
                this.categoryName = categoryName;
                this.instanceName = instanceName;
                this.date = date;
                this.value = value;
            }
        }

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
                List<UploadData> upDataLst = new List<UploadData>();
                InstanceValues instances = Global.categoryValues[pe.categoryName];
                foreach (string instanceName in instances.instanceNames)
                {
                    UploadData data = new UploadData(Global.machineName, pe.categoryName, instanceName, DateTime.Now, instances.GetValue(instanceName));
                    upDataLst.Add(data);
                }
                string sendText = TransDataListToText(upDataLst);
                ConnectServerToSendText(sendText);
            }
            catch
            {
                PerformanceDataUploadError();
            }
        }

        private string TransDataListToText(List<UploadData> upDataLst)
        {
            /********************************************************************
             * 将一串UpLoadData转化为字符串，内容就是这个函数的返回值。
             * 服务器收到字符串后，会将其反转为对应项目信息值。
             * 注意UpLoadData字段有个DateTime，不知道Java有没有。
             *********************************************************************/
            return null;
        }

        /// <summary>
        /// 上传失败处理方法，可选
        /// </summary>
        private void PerformanceDataUploadError()
        {
            //可选
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
                        Global.uploadPeriodTable[categoryName] = upTable[categoryName];
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
        #endregion

        #region 初始化
        /// <summary>
        /// 初始化并绑定事件处理方法
        /// </summary>
        public Upload()
        {
            eventHandlePeriod = new TimeSpan(0, 0, 0, 0, 100); //事件驱动事件为100ms
            eventHandleTable.Add(typeof(ClientStartNotificationEvent), this.ClientStartNotify);
            eventHandleTable.Add(typeof(PerformanceDataUploadEvent), this.PerformanceDataUpload);
            eventHandleTable.Add(typeof(ChangeUploadPeriodEvent), this.ChangeUploadPeriod);
        }
        #endregion

    }
}
