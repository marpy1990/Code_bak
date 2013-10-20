using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Threading;
using ZedGraph;

namespace test6
{
    public partial class Form1 : Form
    {
        Random ran = new Random();

        PointPairList list = new PointPairList();
        PointPairList list2 = new PointPairList();

        LineItem myCurve,myCurve2;

        int count=0;
        int num = 100;
        int itv = 100;
        bool a = false;
        bool b = false;

        double t0;
        double dt = 1.0 / 24 / 3600;

        public Form1()
        {
            InitializeComponent();
            //createPane(zedGraphControl1);
            this.timer1.Interval =itv;
            this.timer1.Enabled = false;

         
            this.zedGraphControl1.GraphPane.Title.Text = "动态折线图";

            this.zedGraphControl1.GraphPane.XAxis.Title.Text = "时间";

            this.zedGraphControl1.GraphPane.YAxis.Title.Text = "数量";

            this.zedGraphControl1.GraphPane.XAxis.Type = ZedGraph.AxisType.Date;

            //this.zedGraphControl1.GraphPane
          
            double now = (double) new XDate(DateTime.Now);
            t0 = now;

            
            this.zedGraphControl1.GraphPane.XAxis.Scale.Max = t0+20*dt;
            this.zedGraphControl1.GraphPane.XAxis.Scale.Min = now;

            myCurve = zedGraphControl1.GraphPane.AddCurve("My Curve",

                    list, Color.DarkGreen, SymbolType.None);
            
            myCurve.Line.Style = DashStyle.Dash;
           //this.zedGraphControl1.GraphPane.AddCurve
            myCurve2 = zedGraphControl1.GraphPane.AddCurve("My Curve2",

                    list2, Color.Red, SymbolType.None);

            this.zedGraphControl1.AxisChange();

            this.zedGraphControl1.Refresh();
        }

        

        private void zedGraphControl1_Load(object sender, EventArgs e)
        {
            MessageBox.Show("Hello World");
        }
        
        private void timer2_Tick(object sender, EventArgs e)
        {
            //zedGraphControl1.GraphPane.XAxis.Scale.MaxAuto = true;
            double t1=0;

            if (b == false)
            {
                t0 = (double)new XDate(DateTime.Now);
                list.Add(t0, 1);
                b = true;
            }
            else
            {
                t1=(double)new XDate(DateTime.Now);
                list.Add(t1, 1);
            }


            if (a)
            {
                list2.Add(t1, 2);
            }
            //MessageBox.Show(this.zedGraphControl1.GraphPane.XAxis.Scale.Max.ToString());

            double time = (double)new XDate(DateTime.Now);
            if (time > this.zedGraphControl1.GraphPane.XAxis.Scale.Max)
            {
                this.zedGraphControl1.GraphPane.XAxis.Scale.Max = time;
                this.zedGraphControl1.GraphPane.XAxis.Scale.Min = time-20*dt;
            }
            this.zedGraphControl1.AxisChange();

            this.zedGraphControl1.Refresh();
        }

        private void timer3_Tick(object sender, EventArgs e)
        {
            a = true;
            //list2.Add((double)new XDate(DateTime.Now.AddSeconds(10)), 10);
            //this.zedGraphControl1.GraphPane.XAxis.Scale.Min = 5;
            this.timer3.Enabled = false;
            
        }

        private void timer4_Tick(object sender, EventArgs e)
        {
            //this.zedGraphControl1.GraphPane.CurveList.Clear();
            //this.zedGraphControl1.Refresh();
            //this.zedGraphControl1.GraphPane.CurveList.Add(myCurve);
            //this.zedGraphControl1.GraphPane.XAxis.Scale.Min = 0;
            //b = true;
            this.timer4.Enabled = false;
            //this.timer2.Enabled = false;
        }

        private void createPane(ZedGraphControl zgc)
        {
            GraphPane myPane = zgc.GraphPane;



            //设置图标标题和x、y轴标题

            myPane.Title.Text = "机票波动情况";

            myPane.XAxis.Title.Text = "波动日期";

            myPane.YAxis.Title.Text = "机票价格";



            //更改标题的字体

            FontSpec myFont = new FontSpec("Arial", 20, Color.Red, false, false, false);

            myPane.Title.FontSpec = myFont;

            myPane.XAxis.Title.FontSpec = myFont;

            myPane.YAxis.Title.FontSpec = myFont;



            // 造一些数据，PointPairList里有数据对x，y的数组

            Random y = new Random();

            PointPairList list1 = new PointPairList();

            for (int i = 0; i < 36; i++)
            {

                double x = i;

                //double y1 = 1.5 + Math.Sin((double)i * 0.2);

                double y1 = y.NextDouble() * 1000;

                list1.Add(x, y1); //添加一组数据

            }



            // 用list1生产一条曲线，标注是“东航”

            LineItem myCurve = myPane.AddCurve("东航", list1, Color.Red, SymbolType.Default);



            //填充图表颜色

            myPane.Fill = new Fill(Color.White, Color.FromArgb(200, 200, 255), 45.0f);



            //以上生成的图标X轴为数字，下面将转换为日期的文本

            string[] labels = new string[36];

            for (int i = 0; i < 36; i++)
            {

                labels[i] = System.DateTime.Now.AddDays(i).ToShortDateString();

            }

            myPane.XAxis.Scale.TextLabels = labels; //X轴文本取值

            myPane.XAxis.Type = AxisType.Text;   //X轴类型



            //画到zedGraphControl1控件中，此句必加

            zgc.AxisChange();



            //重绘控件

            Refresh();
        }
        
        private void timer1_Tick(object sender, EventArgs e)
        {   
            zedGraphControl1.GraphPane.XAxis.Scale.MaxAuto = true;

            double x = (double)new XDate(DateTime.Now);

            double y = ran.NextDouble();

            if (count == num)
            {
                list.RemoveAt(0);
                list.Add(x, y);
            }
            else
            {
                list.ElementAt(count).X = x;
                list.ElementAt(count).Y = y;
                count += 1;
            }

            this.zedGraphControl1.AxisChange();

            this.zedGraphControl1.Refresh();
        }

        private void comboBox1_SelectedIndexChanged(object sender, EventArgs e)
        {
            MessageBox.Show(comboBox1.SelectedItem.ToString());
        }

        private void contextMenuStrip1_Opening(object sender, CancelEventArgs e)
        {

        }

        private void eventLog1_EntryWritten(object sender, System.Diagnostics.EntryWrittenEventArgs e)
        {

        }

        private void menuStrip1_ItemClicked(object sender, ToolStripItemClickedEventArgs e)
        {

        }

        private void listBox1_SelectedIndexChanged(object sender, EventArgs e)
        {
        }

        private void listBox1_MouseUp(object sender, MouseEventArgs e)
        {

            if (e.Button == MouseButtons.Right)
            {

                int posindex = listBox1.IndexFromPoint(new Point(e.X, e.Y));

                listBox1.ContextMenuStrip = null;

                if (posindex >= 0 && posindex < listBox1.Items.Count)
                {

                    listBox1.SelectedIndex = posindex;

                    contextMenuStrip1.Show(listBox1, new Point(e.X, e.Y));

                }

            }

            listBox1.Refresh();

        }

        private void sdfsdfToolStripMenuItem1_Click(object sender, EventArgs e)
        {

        }

        private void asdfToolStripMenuItem_Click(object sender, EventArgs e)
        {

        }
        
    }
}
