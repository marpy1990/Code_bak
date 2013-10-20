using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Forecast;
using MonitorSystem;
using System.Threading;

namespace test8
{
    class Program
    {
        static void Main(string[] args)
        {
            Random ran = new Random();
            List<double> a=new List<double>();
            for (int i = 0; i < 100; ++i)
            {
                a.Add(ran.NextDouble() * 100);
            }

            Predictor p = new Predictor(a);
            MonitorSystem.Monitor m=MonitorEnviroment.CreateMonitor(Environment.MachineName, "%CPU Usage", "_Total");
            

            for (int i = 0; i < 100; ++i)
            {
                double d=m.GetValue();
                p.Add(d);
                Console.WriteLine("{0} next is {1}", d, p.Next());
                Thread.Sleep(1000);
            }
            Console.WriteLine(p.MRE());
        }
    }
}
