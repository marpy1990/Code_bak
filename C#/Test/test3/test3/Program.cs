using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace test3
{
    class Program
    {
        public class KNN
        {
            public int step = 10;

            int dim = 7;

            List<double> lst;

            Dictionary<int, double> dict = new Dictionary<int, double>();

            public KNN(int dim)
            {
                this.step = dim;
                this.dim = dim;
                lst = new List<double>();
            }

            public KNN(int dim, List<double> lst)
            {
                this.step = dim;
                this.dim = dim;
                this.lst = lst;
            }

            public void SetDim(int n)
            {
                dim = n;
            }

            public void Add(double value)
            {
                lst.Add(value);
            }

            public double Next(int index)
            {
                double rst = 0;
                double minDst = -1;
                int tail = lst.Count - step - dim + 1;
                for (int i = 0; i < tail; ++i)
                {
                    double dst = 0;
                    for (int j = 0; j < dim; ++j)
                    {
                        dst += (lst[i + j] - lst[tail + j]) * (lst[i + j] - lst[tail + j]);
                    }

                    if (minDst == -1)
                    {
                        minDst = dst;
                        rst = lst[i + dim + index - 1];
                    }
                    else if (dst < minDst)
                    {
                        minDst = dst;
                        rst = lst[i + dim + index - 1];
                    }
                }
                if (minDst != -1)
                    dict.Add(lst.Count - 1 + index, rst);
                return rst;
            }

            public double MRE()
            {
                double mre = 0;
                foreach (int index in dict.Keys)
                {
                    if (index < lst.Count)
                    {
                        mre += (dict[index] - lst[index]) / lst[index];
                    }
                }
                return mre;
            }
        }
        
        static void Main(string[] args)
        {
            KNN knn = new KNN(3);
            for (int i = 1; i < 30; ++i)
            {
                knn.Add(i % 3+1);
                Console.WriteLine("{0}   {1}", i%3, knn.Next(1));
            }
            Console.WriteLine(knn.MRE());
        }
    }
}
