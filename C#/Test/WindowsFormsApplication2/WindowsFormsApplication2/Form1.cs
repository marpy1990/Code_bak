using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Drawing.Drawing2D;

namespace WindowsFormsApplication2
{
    public partial class Form1 : Form
    {
        private Color m_StartColor;
        private Color m_EndColor;

        ///
        /// 封装字段
        ///
        public Color StartColor
        {
            get { return m_StartColor; }
            set { m_StartColor = value; }
        }
        ///
        /// 封装字段
        ///
        public Color EndColor
        {
            get { return m_EndColor; }
            set { m_EndColor = value; }
        }

        private static string[] colorList ={
            "AliceBlue|Green","Aqua|Aquamarine","Red|SkyGreen",
            "Bisque|Black","BlanchedAlmond|Blue","BlueViolet|Brown",
            "BurlyWood|CadetBlue","Chartreuse|Chocolate",
            "CornflowerBlue|Cornsilk","Crimson|Cyan","DarkBlue|DarkCyan",
            "DarkGoldenrod|DarkGray","DarkGreen|DarkKhaki",
            "DarkMagenta|DarkOliveGreen","DarkOrange|DarkOrchid"
        };
        public event EventHandler SelectColorChanged;
        
        public Form1()
        {
            InitializeComponent();
            this.listView1.GridLines = false;
            ListViewItem lv1 = new ListViewItem();
            lv1.UseItemStyleForSubItems = false; 
            lv1.Text = "111";
            lv1.ForeColor = Color.Red;
            lv1.SubItems.Add("222");
            lv1.SubItems.Add("333");
            lv1.SubItems.Add("______", Color.Red, Color.White, null);
            listView1.Items.Add(lv1);
            //listView1.DrawSubItem+=listView1_DrawSubItem;
            //listView1.Items[0].BackColor = Color.Red;
            Color color = Color.Red;
            comboBox1.Items.Add(Color.Red);
            comboBox1.Items.Add(Color.Blue);
            comboBox1.DrawItem+=comboBox1_DrawItem;
        }

        private void listView1_DrawSubItem(object sender, DrawListViewSubItemEventArgs e)
        {
            if(e.ColumnIndex==3)
                e.Graphics.FillRectangle(new SolidBrush(Color.Red), e.SubItem.Bounds);
            else
                e.DrawText(TextFormatFlags.Bottom | TextFormatFlags.HorizontalCenter);
        }

        private void bindingNavigatorAddNewItem_Click(object sender, EventArgs e)
        {
        }

        private void bindingNavigator1_RefreshItems(object sender, EventArgs e)
        {

        }

        private void bindingNavigatorDeleteItem_Click(object sender, EventArgs e)
        {

        }

        private void SaveSToolStripButton_Click(object sender, EventArgs e)
        {
            MessageBox.Show("AAA");
        }

        private void toolStripButton1_Click(object sender, EventArgs e)
        {

        }

        private void 打开OToolStripButton_Click(object sender, EventArgs e)
        {

        }

        private void bindingNavigatorAddNewItem_Click_1(object sender, EventArgs e)
        {

        }

        private void toolStripButton1_Click_1(object sender, EventArgs e)
        {

        }

        private void groupBox1_Enter(object sender, EventArgs e)
        {

        }

        private void hScrollBar1_Scroll(object sender, ScrollEventArgs e)
        {

        }

        private void maskedTextBox1_MaskInputRejected(object sender, MaskInputRejectedEventArgs e)
        {

        }

        private void listView1_SelectedIndexChanged(object sender, EventArgs e)
        {

        }

        private void listView1_SelectedIndexChanged_1(object sender, EventArgs e)
        {
            //MessageBox.Show("aaaa");
        }

        private void button1_Click(object sender, EventArgs e)
        {
            Form2 f2 = new Form2();

            //this.Visible = false;
            f2.ShowDialog();
            this.textBox1.Text=f2.TTT;
            //this.Visible = true;
        }

        private void openFileDialog1_FileOk(object sender, CancelEventArgs e)
        {

        }

        private void comboBox1_SelectedIndexChanged(object sender, EventArgs e)
        {
            if (SelectColorChanged != null)
            {

                SelectColorChanged(this, e);

            }
        }

        private void comboBox1_DrawItem(object sender, DrawItemEventArgs e)
        {

            if (e.Index < 0)

                return;
            Rectangle rect = e.Bounds;
            rect.Y = e.Bounds.Y+ e.Bounds.Height* 1 / 5;
            rect.Height = e.Bounds.Height * 3 / 5;
            rect.X = e.Bounds.X + e.Bounds.Width * 1 / 11;
            rect.Width = e.Bounds.Width * 9 / 11;
            Color c=(Color)(comboBox1.Items[e.Index]);
            float a = 0F;
            LinearGradientBrush lBrush = new LinearGradientBrush(rect, c, c, a);
            //e.Graphics.DrawRectangle(Pens.Black, e.Bounds);
            e.Graphics.FillRectangle(lBrush, rect);
            
        }

        private void Form1_Load(object sender, EventArgs e)
        {

        }
    }
}
