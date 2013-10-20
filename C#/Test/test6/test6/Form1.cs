using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using MonitorSystem;

namespace test6
{
    public partial class Form1 : Form
    {
        Monitor mt;

        public Form1()
        {
            InitializeComponent();
            foreach (string name in MonitorEnviroment.MachineNames())
            {
                comboBox1.Items.Add(name);
            }
        }

        private void comboBox1_SelectedIndexChanged(object sender, EventArgs e)
        {
            foreach (string name in MonitorEnviroment.CategoryNames())
            {
                comboBox2.Items.Add(name);
            }
        }

        private void comboBox2_SelectedIndexChanged(object sender, EventArgs e)
        {
            string machine = (string)comboBox1.SelectedItem;
            string category = (string)comboBox2.SelectedItem;
            foreach (string name in MonitorEnviroment.InstanceNames(machine,category))
            {
                comboBox3.Items.Add(name);
            }
        }

        private void button1_Click(object sender, EventArgs e)
        {
            string machine = (string)comboBox1.SelectedItem;
            string category = (string)comboBox2.SelectedItem;
            string instance = (string)comboBox3.SelectedItem;
            mt = MonitorEnviroment.CreateMonitor(machine, category, instance);
        }

        private void textBox1_TextChanged(object sender, EventArgs e)
        {

        }

        private void button2_Click(object sender, EventArgs e)
        {
            textBox1.Text = mt.GetValue().ToString();
        }
    }
}
