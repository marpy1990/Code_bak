using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace MonitorClient
{
    public partial class TestForm : Form
    {
        public TestForm()
        {
            InitializeComponent();
            this.FormClosing += TestForm_FormClosing;
        }

        private void TestForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            if (e.CloseReason == CloseReason.UserClosing)
            {
                this.ShowInTaskbar = false;
                this.Visible = false;   //隐藏窗体
                Global.performanceCollector.TryToStop(new TimeSpan(1,0,0));
                Global.userConfig.TryToStop(new TimeSpan(1, 0, 0));
                Global.test.Stop();
            }
        }

        private void textBox1_TextChanged(object sender, EventArgs e)
        {

        }

        private void timer1_Tick(object sender, EventArgs e)
        {
            if (Global.categoryValues.Contains(@"%CPU Usage"))
            {
                this.textBox1.Text = Global.categoryValues[@"%CPU Usage"].GetValue("_Total").ToString();
            }

            this.textBox2.Text = Processor.threadCount.ToString();
        }
    }
}
