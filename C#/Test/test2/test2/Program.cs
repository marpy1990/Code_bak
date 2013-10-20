using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Data;
using System.Timers;
using System.Diagnostics;
using System.Management;
using System.ServiceProcess;
using Microsoft.SqlServer.Management.Smo;
using Microsoft.SqlServer.Management.Common;
using Microsoft.Win32;

namespace test2
{
    class SQLInfo
    {
        public string Name;
        public string Path;
        public string Key;
    }

    class HKEY
    {
        private static SQLInfo SQLAgent=new SQLInfo 
        { 
            Name = @"SQL 2008 Agent",
            Path = @"SYSTEM\CurrentControlSet\services\SQLSERVERAGENT",
            Key = "Start"
        };

        private static SQLInfo SQLServerAnalysisServices=new SQLInfo
        { 
            Name = @"SQL Server Analysis Services",
            Path = @"SYSTEM\CurrentControlSet\services\MSSQLServerOLAPService",
            Key = "Start"
        };

        private static SQLInfo SQLServer=new SQLInfo
        { 
            Name = @"SQL Server",
            Path = @"SYSTEM\CurrentControlSet\services\MSSQLSERVER",
            Key = "Start"
        };

        private static SQLInfo SQLIntegrationServices = new SQLInfo
        {
            Name = @"SQL 2005 Integration Services",
            Path = @"SYSTEM\CurrentControlSet\services\MsDtsServer100",
            Key = "Start"
        };

        private static SQLInfo SQLServerReportingServices = new SQLInfo
        {
            Name = @"SQL Server Reporting Services",
            Path = @"SYSTEM\CurrentControlSet\services\ReportServer",
            Key = "Start"
        };

        private static SQLInfo SQLServerFullTextSearchService = new SQLInfo
        {
            Name = @"SQL Server Full Text Search Service",
            Path = @"SYSTEM\CurrentControlSet\services\MSSQLFDLauncher",
            Key = "Start"
        };

        public static List<SQLInfo> SQLServerWindowsServiceMonitors = new List<SQLInfo>
        {
            SQLAgent,
            SQLServerAnalysisServices,
            SQLServer,
            SQLIntegrationServices,
            SQLServerReportingServices,
            SQLServerFullTextSearchService
        };
    }

    class SysInfo
    {
        public string Name;
        public string objectName;
        public string counterName;
        public Dictionary<string,PerformanceCounter> pcDict = new Dictionary<string, PerformanceCounter>();
    }

    class SysMonitor
    {
        private static SysInfo CpuUsage = new SysInfo
        {
            Name = @"%CPU Usage",
            objectName = @"Processor",
            counterName = @"% Processor Time"
        };

        private static SysInfo InterruptTime = new SysInfo
        {
            Name = @"%Interrupt Time",
            objectName = @"Processor",
            counterName = @"% Interrupt Time"
        };

        private static SysInfo ProcessorTime = new SysInfo
        {
            Name = @"%Processor Time",
            objectName = @"Processor",
            counterName = @"% User Time"
        };

        private static SysInfo DPCTime = new SysInfo
        {
            Name = @"%DPC Time",
            objectName = @"Processor",
            counterName = @"% DPC Time"
        };

        private static SysInfo LogicalDiscFreeSpace = new SysInfo
        {
            Name = @"Logical Disc Free Space",
            objectName = @"LogicalDisk",
            counterName = @"Free Megabytes"
        };

        private static SysInfo AvgLogDiskSecTransfer = new SysInfo
        {
            Name = @"Avg. Log. Disk sec/Transfer",
            objectName = @"LogicalDisk",
            counterName = @"Avg. Disk sec/Transfer"
        };

        private static SysInfo AvgLogDiskSecRead = new SysInfo
        {
            Name = @"Avg. Log. Disk sec/Read",
            objectName = @"LogicalDisk",
            counterName = @"Avg. Disk sec/Read"
        };

        private static SysInfo AvgLogDiskSecWrite = new SysInfo
        {
            Name = @"Avg. Log. Disk sec/Write",
            objectName = @"LogicalDisk",
            counterName = @"Avg. Disk sec/Write"
        };

        private static SysInfo AvgPhsDiskSecTransfer = new SysInfo
        {
            Name = @"Avg. Phs. Disk sec/Transfer",
            objectName = @"PhysicalDisk",
            counterName = @"Avg. Disk sec/Transfer"
        };

        private static SysInfo AvgPhsDiskSecRead = new SysInfo
        {
            Name = @"Avg. Phs. Disk sec/Read",
            objectName = @"PhysicalDisk",
            counterName = @"Avg. Disk sec/Read"
        };

        private static SysInfo AvgPhsDiskSecWrite = new SysInfo
        {
            Name = @"Avg. Phs. Disk sec/Write",
            objectName = @"PhysicalDisk",
            counterName = @"Avg. Disk sec/Write"
        };

        private static SysInfo MemoryAvailableMBytes = new SysInfo
        {
            Name = @"Memory Available MBytes",
            objectName = @"Memory",
            counterName = @"Available MBytes"
        };

        public static List<SysInfo> WindowsServiceMonitors = new List<SysInfo> 
        {
            CpuUsage,
            InterruptTime,
            ProcessorTime,
            DPCTime,

            LogicalDiscFreeSpace,
            AvgLogDiskSecTransfer,
            AvgLogDiskSecRead,
            AvgLogDiskSecWrite,

            AvgPhsDiskSecTransfer,
            AvgPhsDiskSecRead,
            AvgPhsDiskSecWrite,

            MemoryAvailableMBytes
        };
    }

    class ProcessInterferenceException:Exception
    {
    }

    class Program
    {
        static void MonitorSystemInfo()
        {
            string machineName = Environment.MachineName;
            for (int test = 0; test < 2; ++test)
            {
                foreach (SysInfo pcObj in SysMonitor.WindowsServiceMonitors)
                {
                    PerformanceCounterCategory pcc = new PerformanceCounterCategory(pcObj.objectName);
                    foreach (string instanceName in pcc.GetInstanceNames().DefaultIfEmpty(""))
                    {
                        float value;
                        PerformanceCounter pc;
                        if (test == 0)
                        {
                            pc = new PerformanceCounter(pcObj.objectName, pcObj.counterName, instanceName, machineName);
                            value = pc.NextValue();
                            pcObj.pcDict.Add(instanceName, pc);
                        }
                        else
                        {
                            pc = pcObj.pcDict[instanceName];
                            value = pc.NextValue();
                            Console.WriteLine("{0,-40}{1,-20}{2,10}", pcObj.Name, instanceName, value);
                        }
                    }
                    if (test == 1)
                        Console.WriteLine("=========================================================================");
                }
                if (test == 0)
                    Thread.Sleep(1000);
            }
        }

        static void MonitorSQLAvaiInfo()
        {
            foreach (SQLInfo keyObj in HKEY.SQLServerWindowsServiceMonitors)
            {
                try
                {
                    RegistryKey svKey = Registry.LocalMachine.OpenSubKey(keyObj.Path);
                    int value = (int)svKey.GetValue(keyObj.Key);
                    string status;
                    if (value != 4)
                        status = "Available";
                    else
                        status = "Unavailable";
                    Console.WriteLine("{0,-50}{1,20}", keyObj.Name, status);
                }
                catch
                {
                    Console.WriteLine("{0,-50}{1,20}", keyObj.Name, "Unavailable");
                }
            }
        }

        static void MonitorSQLPackInfo()
        {
            //确定pack版本号
            string machineName = Environment.MachineName;
            RegistryKey machineKey = RegistryKey.OpenRemoteBaseKey(RegistryHive.LocalMachine, machineName);
            RegistryKey key = machineKey.OpenSubKey(@"SOFTWARE\Microsoft\Microsoft SQL Server");
         
            RegistryKey subKey = key.OpenSubKey(@"Instance Names\SQL");
            int sp = 0;

            foreach (string instanceKeyName in subKey.GetValueNames())
            {
                if (instanceKeyName != null)
                {
                    string instanceName = subKey.GetValue(instanceKeyName).ToString();
                    RegistryKey spKey = key.OpenSubKey(instanceName).OpenSubKey("Setup");
                    sp = (int)spKey.GetValue("SP");
                    Console.WriteLine("{0,-50}{1,20}", instanceKeyName + " Pack Version", sp);
                }
            }

            Console.WriteLine("=========================================================================");
        }

        static void MonitorDatabaseInfo()
        {
            string machineName = Environment.MachineName;
            /*ServerConnection sc = new ServerConnection(machineName);
            try
            {
               sc.Connect();

            }
            catch
            {
                Console.WriteLine("Cannot connect to SQL server");
                return;
            }
            */
            Server sv = new Server(machineName);

            foreach (Database db in sv.Databases)
            {
                Console.WriteLine("{0,-50}{1,20}", "DB Name", db.Name);
                Console.WriteLine("{0,-50}{1,20}", "Auto Close Flag", db.AutoClose);
                Console.WriteLine("{0,-50}{1,20}", "Auto Create Statistics Flag", db.AutoCreateStatisticsEnabled);
                Console.WriteLine("{0,-50}{1,20}", "Auto Shrink Flag", db.AutoShrink);
                Console.WriteLine("{0,-50}{1,20}", "DB Chaining Flag", db.DatabaseOwnershipChaining);
                Console.WriteLine("{0,-50}{1,20}", "Data Space Usage", db.DataSpaceUsage);
                Console.WriteLine("{0,-50}{1,20}", "DB Size", db.Size);
                Console.WriteLine("{0,-50}{1,20}", "Space Available", db.SpaceAvailable);
                Console.WriteLine("{0,-50}{1,20}", "Status", db.Status);
                Console.WriteLine("=========================================================================");
                Console.WriteLine();
            }
        }

        static void MonitorSQLAvailableInfo()
        {
            string machineName = Environment.MachineName;
            //double timeout = 20;
            //ServiceController scServices = new ServiceController("SQL Server", machineName);
            ServiceController scServices = new ServiceController("SQLSERVERAGENT");
            Console.WriteLine(scServices.Status);
            /*
            try
            {
                //waiting until the service status is stable
                switch (scServices.Status)
                {
                    case ServiceControllerStatus.ContinuePending:
                        scServices.WaitForStatus(ServiceControllerStatus.Running, TimeSpan.FromSeconds(timeout));
                        break;
                    case ServiceControllerStatus.PausePending:
                        scServices.WaitForStatus(ServiceControllerStatus.Paused, TimeSpan.FromSeconds(timeout));
                        break;
                    case ServiceControllerStatus.StartPending:
                        scServices.WaitForStatus(ServiceControllerStatus.Running, TimeSpan.FromSeconds(timeout));
                        break;
                    case ServiceControllerStatus.StopPending:
                        scServices.WaitForStatus(ServiceControllerStatus.Stopped, TimeSpan.FromSeconds(timeout));
                        break;
                }

                switch (scServices.Status)
                {
                    case ServiceControllerStatus.Paused:
                        scServices.Continue();
                        scServices.WaitForStatus(ServiceControllerStatus.Running, TimeSpan.FromSeconds(timeout));
                        scServices.Pause();
                        scServices.WaitForStatus(ServiceControllerStatus.Paused, TimeSpan.FromSeconds(timeout));
                        break;
                    case ServiceControllerStatus.Running:
                        break;
                    case ServiceControllerStatus.Stopped:
                        scServices.Start();
                        scServices.WaitForStatus(ServiceControllerStatus.Running, TimeSpan.FromSeconds(timeout));
                        scServices.Stop();
                        scServices.WaitForStatus(ServiceControllerStatus.Stopped, TimeSpan.FromSeconds(timeout));
                        break;
                    default:
                        throw (new ProcessInterferenceException());
                }
                Console.WriteLine("Available");
            }
            catch (ProcessInterferenceException)
            {
                Console.WriteLine("Unkonw");
            }
            catch
            {
                Console.WriteLine("DisAvailable");
            }
            */
        }

        static void Monitor()
        {
            MonitorSystemInfo();
            MonitorSQLAvaiInfo();
            MonitorSQLPackInfo();
            MonitorDatabaseInfo();
            MonitorSQLAvailableInfo();
        }

        static void SomethingMaybeUsage()
        {
            DataTable dtServers = SmoApplication.EnumAvailableSqlServers(true);
            foreach (DataRow row in dtServers.Rows)
            {
                string sqlServerName = row["Server"].ToString();
                if (row["Instance"] != null && row["Instance"].ToString().Length > 0)
                    sqlServerName += @"" + row["Instance"].ToString();
                Console.WriteLine("Name: {0}", sqlServerName);
            }

            ConnectionOptions connOption = new ConnectionOptions();
            connOption.Username = "SDF-PC" + @"\" + "marpy";
            connOption.Password = "mp19900922";

            ManagementPath mngPath = new ManagementPath(@"\\" + "SDF-PC" + @"\root\cimv2:Win32_Process");
            ManagementScope scope = new ManagementScope(mngPath, connOption);
            scope.Connect();

            ObjectGetOptions objOption = new ObjectGetOptions();
            ManagementClass classInstance = new ManagementClass(scope, mngPath, objOption);

            //int ProcessId = 0;
            object[] cmdline = { "cmd /c" + "net start SQLSERVERAGENT" };
        }

        static void Main(string[] args)
        {
            Monitor();
        }
    }
}
