using System;
using System.Collections.Generic;
using System.Text;
using Forecast;

namespace WNND
{
    class Program
    {
        static void Main(string[] args)
        {
            Random ran=new Random();
            List<double> a = new List<double>();
            double d = 1;
            for (int i = 0; i < 100; ++i)
            {
                a.Add(ran.NextDouble() * 100);
            }
            Predictor p = new Predictor(a);

            for (int i = 0; i < 20; ++i)
            {
                if (d++ == 3) d = 0;
                p.Add(d);
                Console.WriteLine("{0}   the next is {1}", d, p.Next());
            }
            Console.WriteLine(p.MRE());
        }
        public class WNND
        {
            private List<double> a = new List<double>(5);
            public int k = 5;
            public void setk(int new_k)
            {
                if (k < new_k)
                {
                    List<double> temp = new List<double>(new_k);
                    for (int i = 0; i < a.Count; i++)
                    {
                        temp.Add(a[i]);
                    }
                    a.Clear();
                    a = temp;
                }
                if (k > new_k)
                {
                    a.RemoveRange(0, k - new_k);
                    a.TrimExcess();
                }
                k = new_k;
            }
            public void add(double data)
            {
                if (a.Contains(data))
                {
                    a.Remove(data);
                    a.Add(data);
                }
                else
                {
                    if (a.Count < a.Capacity)
                    {
                        a.Add(data);
                    }
                    else
                    {
                        a.RemoveAt(0);
                        a.Add(data);
                    }
                }
            }
            public double forecastnext()
            {
                double next = 0.0;
                double sum = 0.0;
                for (int i = 0; i < a.Count; i++)
                {
                    sum += a[i];
                }
                next = sum / a.Count;
                return next;
            }

        }
    }
}