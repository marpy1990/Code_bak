using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using ZedGraph;
using MonitorSystem.MonitorSystem;

namespace WindowsFormsApplication1
{
    public partial class Form1 : Form
    {
        List<Monitor> monitorList=new List<Monitor>();

        List<PointPairList> PLlist = new List<PointPairList>();

        List<LineItem> myCurveList=new List<LineItem>();

        int rowNum=100;

        Random colorRan = new Random();

        Dictionary<int,Color> clear=new Dictionary<int,Color>();

        bool hide = false;

        public Form1()
        {
            InitializeComponent();
        }

        private void ZedGraphInitialize()
        {
            this.zedGraphControl1.GraphPane.Title.Text = "Monitor";
            this.zedGraphControl1.GraphPane.XAxis.Title.Text = "Time/Second";
            this.zedGraphControl1.GraphPane.YAxis.Title.Text = "Value";
            this.zedGraphControl1.GraphPane.XAxis.Type = ZedGraph.AxisType.DateAsOrdinal;
        }

        private void zedGraphControl1_Load(object sender, EventArgs e)
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

        private void button1_Click(object sender, EventArgs e)
        {
            try
            {
                Monitor monitor = ((Instance)this.comboBox3.SelectedItem).Monitor();
                monitorList.Add(monitor);
                PointPairList list = new PointPairList();
                for (int i = 0; i < rowNum; ++i)
                {
                    double x = (double)new XDate(DateTime.Now.AddSeconds(-(rowNum - i)));
                    double y = 0;
                    list.Add(x, y);
                }
                Color color=Color.FromArgb(colorRan.Next(256), colorRan.Next(256), colorRan.Next(256));
                myCurveList.Add(zedGraphControl1.GraphPane.AddCurve(monitor.CategoryName() + " " + monitor.InstanceName(), list, color, SymbolType.None));
                PLlist.Add(list);
                if (!timer1.Enabled)
                    this.timer1.Start();
                this.listBox1.Items.Add(monitor.ToString());
            }
            catch
            {
            }
        }

        private void comboBox1_SelectedIndexChanged(object sender, EventArgs e)
        {
            Machine machine=(Machine)this.comboBox1.SelectedItem;
            List<Category> lst = machine.Categorys();
            this.comboBox2.Items.Clear();
            foreach (Category ctg in lst)
            {
                this.comboBox2.Items.Add(ctg);
            }
        }

        private void comboBox2_SelectedIndexChanged(object sender, EventArgs e)
        {
            Category category=(Category)this.comboBox2.SelectedItem;
            List<Instance> lst = category.Instances();
            this.comboBox3.Items.Clear();
            foreach (Instance ist in lst)
            {
                this.comboBox3.Items.Add(ist);
            }
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            this.listBox1.MouseUp += listBox1_MouseUp;
            ZedGraphInitialize();
            Machine machine = new MonitorSystem.MonitorSystem.Machine(Environment.MachineName);
            this.comboBox1.Items.Add(machine);
        }

        private void timer1_Tick(object sender, EventArgs e)
        {
            zedGraphControl1.GraphPane.XAxis.Scale.MaxAuto = true;
            for(int i =0; i<monitorList.Count; ++i)
            {
                double x = (double)new XDate(DateTime.Now);
                double y = monitorList[i].GetValue();

                PLlist[i].Add(x, y);

                if (PLlist[i].Count > rowNum)
                {
                  PLlist[i].RemoveAt(0);
                }
                this.listBox1.Items[i] = monitorList[i].ToString() + "  :   " + y;
            }
           
            this.zedGraphControl1.AxisChange();
            this.zedGraphControl1.Refresh();
        }

        private void deleteToolStripMenuItem_Click(object sender, EventArgs e)
        {
            int index = this.listBox1.SelectedIndex;
            monitorList.RemoveAt(index);
            PLlist.RemoveAt(index);
            //LineItem line = myCurveList[index];
            //line.Clear();
            this.zedGraphControl1.GraphPane.CurveList.RemoveAt(index);
            this.zedGraphControl1.Refresh();
            myCurveList.RemoveAt(index);
            listBox1.Items.RemoveAt(index);
        }

        private void clearToolStripMenuItem_Click(object sender, EventArgs e)
        {
            hide = true;
            int index = this.listBox1.SelectedIndex;
            LineItem line = myCurveList[index];
            clear.Add(index, line.Color);
            line.Color = Color.Empty;
        }

        private void refreshToolStripMenuItem_Click(object sender, EventArgs e)
        {
            try
            {
                int index = this.listBox1.SelectedIndex;
                LineItem line = myCurveList[index];
                Color color = clear[index];
                line.Color = color;
                clear.Remove(index);
                hide = false;
            }
            catch
            {
            }
        }

        private void contextMenuStrip1_Opening(object sender, CancelEventArgs e)
        {
            if (hide)
            {
                this.contextMenuStrip1.Items[1].Enabled = false;
                this.contextMenuStrip1.Items[2].Enabled = true;
            }
            else
            {
                this.contextMenuStrip1.Items[2].Enabled = false;
                this.contextMenuStrip1.Items[1].Enabled = true;
            }
        }
    }
}
