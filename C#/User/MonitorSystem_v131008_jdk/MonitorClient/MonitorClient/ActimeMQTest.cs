using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Input;

using Apache.NMS;
using System.Diagnostics;
using Apache.NMS.Util;

/*
 * 功能描述：C#使用ActiveMQ示例
 * 修改次数：2
 * 最后更新： by Kagula,2012-07-31
 * 
 * 前提条件：
 * [1]apache-activemq-5.4.2
 * [2]Apache.NMS.ActiveMQ-1.5.6-bin
 * [3]WinXP SP3
 * [4]VS2008 SP1
 * [5]WPF工程 With .NET Framework 3.5
 * 
 * 启动
 *  
 * 不带安全控制方式启动
 * [你的解压路径]\apache-activemq-5.4.2\bin\activemq.bat
 * 
 * 安全方式启动
 * 添加环境变量：            ACTIVEMQ_ENCRYPTION_PASSWORD=activemq
 * [你的解压路径]\apache-activemq-5.4.2\bin>activemq xbean:file:../conf/activemq-security.xml
 * 
 * Active MQ 管理地址
 * http://127.0.0.1:8161/admin/
 * 添加访问"http://127.0.0.1:8161/admin/"的限制
 * 
 * 第一步：添加访问限制
 * 修改D:\apache\apache-activemq-5.4.2\conf\jetty.xml文件
 * 下面这行编码，原
 * <property name="authenticate" value="true" />
 * 修改为
 * <property name="authenticate" value="false" />
 * 
 * 第二步：修改登录用户名密码，缺省分别为admin，admin
 * D:\apache\apache-activemq-5.4.2\conf\jetty-realm.properties
 * 
 * 用户管理(前提:以安全方式启动ActiveMQ)
 * 
 * 在[你的解压路径]\apache-activemq-5.4.2\conf\credentials.properties文件中修改默认的用户名密码
 * 在[你的解压路径]\apache-activemq-5.4.2\conf\activemq-security.xml文件中可以添加新的用户名
 * e.g.  添加oa用户，密码同用户名。
 * <authenticationUser username="oa" password="oa" groups="users,admins"/>
 * 
 * 在[你的解压路径]\apache-activemq-5.4.2\conf\activemq-security.xml文件中你还可以设置指定的Topic或Queue
 * 只能被哪些用户组read 或 write。
 * 
 * 
 * 配置C# with WPF项目
 * 项目的[Application]->[TargetFramework]属性设置为[.NETFramework 3.5](这是VS2008WPF工程的默认设置)
 * 添加[你的解压路径]\Apache.NMS.ActiveMQ-1.5.6-bin\lib\Apache.NMS\net-3.5\Apache.NMS.dll的引用
 * Apache.NMS.dll相当于接口
 * 
 * 如果是以Debug方式调试
 * 把[你的解压路径]\Apache.NMS.ActiveMQ-1.5.6-bin\build\net-3.5\debug\目录下的
 * Apache.NMS.ActiveMQ.dll文件复制到你项目的Debug目录下
 * Apache.NMS.ActiveMQ.dll相当于实现
 * 
 * 如果是以Release方式调试
 * 参考上文，去取Apache.NMS,Release目录下相应的DLL文件，并复制到你项目的Release目录下。
 * 
 * 
 * 参考资料
 * [1]《C#调用ActiveMQ官方示例》 http://activemq.apache.org/nms/examples.html
 * [2]《ActiveMQ NMS下载地址》http://activemq.apache.org/nms/activemq-downloads.html
 * [3]《Active MQ在C#中的应用》http://www.cnblogs.com/guthing/archive/2010/06/17/1759333.html
 * [4]《NMS API Reference》http://activemq.apache.org/nms/nms-api.html
 */

namespace testActiveMQSubscriber
{
    /// <summary>
    /// Interaction logic for Window1.xaml
    /// </summary>
    public class ActiveMQTest 
    {
        private static IConnectionFactory connFac;

        private static IConnection connection;
        private static ISession session;
        private static IDestination destination;
        private static IMessageProducer producer;
        private static IMessageConsumer consumer;


        protected static ITextMessage message = null;

        public ActiveMQTest()
        {
            initAMQ("MyFirstTopic");
        }

                 
        private void initAMQ(String strTopicName)
        {
            try
            {
                connFac = new NMSConnectionFactory(new Uri("activemq:failover:(tcp://localhost:60000)"));

                //新建连接
                connection = connFac.CreateConnection();//设置连接要用的用户名、密码
                
                //如果你要持久“订阅”，则需要设置ClientId，这样程序运行当中被停止，恢复运行时，能拿到没接收到的消息！
                connection.ClientId = "testing listener";
                connection = connFac.CreateConnection();//如果你是缺省方式启动Active MQ服务，则不需填用户名、密码

                //创建Session
                session = connection.CreateSession();

                //发布/订阅模式，适合一对多的情况
                destination = SessionUtil.GetDestination(session, "topic://" + strTopicName);

                //新建生产者对象
                producer = session.CreateProducer(destination);
                producer.DeliveryMode = MsgDeliveryMode.NonPersistent;//ActiveMQ服务器停止工作后，消息不再保留

                //新建消费者对象:普通“订阅”模式
                //consumer = session.CreateConsumer(destination);//不需要持久“订阅”       

                //新建消费者对象:持久"订阅"模式：
                //    持久“订阅”后，如果你的程序被停止工作后，恢复运行，
                //从第一次持久订阅开始，没收到的消息还可以继续收
                consumer = session.CreateDurableConsumer(
                    session.GetTopic(strTopicName)
                    , connection.ClientId, null, false);

                //设置消息接收事件
                consumer.Listener += new MessageListener(OnMessage);

                //启动来自Active MQ的消息侦听
                connection.Start();

                System.Threading.Thread.Sleep(5000);

                ITextMessage request = session.CreateTextMessage(DateTime.Now.ToLocalTime().ToString());
                IMapMessage m = session.CreateMapMessage();
                m.Properties.SetString("a", "aafoaj");
                m.Properties.SetString("b", "fajos");
                producer.Send(m);
                Console.ReadLine();
            }
            catch (Exception e)
            {
                Console.WriteLine(e.StackTrace);
            }
        }

        protected void OnMessage(IMessage receivedMsg)
        {
            //接收消息

            IMapMessage message = receivedMsg as IMapMessage;

            Console.WriteLine(message.Properties.GetString("a"));

        }
    }
}

