using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;

namespace test11
{
    static class Program
    {
        /// <summary>
        /// 应用程序的主入口点。
        /// </summary>
        [STAThread]
        static void Main()
        {
            bool a = TryToStop();
            System.Windows.Forms.MessageBox.Show(a.ToString());
            Thread.Sleep(1000000000);
            
        }

        public static Semaphore sem = new Semaphore(0, 1);
        public static bool TryToStop()
        {
            bool isStoped = false;
            Thread thread = new Thread(() => Go(out isStoped));
            Thread thread2 = new Thread(() => Go(out isStoped));
            thread.Start();
            thread2.Start();
            sem.WaitOne(5000);   //父线程挂起
            thread.Interrupt();
            thread2.Join();
            System.Windows.Forms.MessageBox.Show("fin");
            return isStoped;
        }

        public static Semaphore sem2 = new Semaphore(1, 1);
        public static void Go(out bool b)
        {
            sem2.WaitOne();
            Thread.Sleep(6000);
            b = true;
            System.Windows.Forms.MessageBox.Show("222");
            sem.Release();
        }

        public static void Go2(out bool b)
        {
            Thread.Sleep(1000);
            sem2.WaitOne();
            b = true;
            System.Windows.Forms.MessageBox.Show("333");
            sem.Release();
        }
    }
}
