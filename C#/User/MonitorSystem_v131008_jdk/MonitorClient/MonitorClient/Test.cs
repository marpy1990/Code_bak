/********************************************************************************
 * Author:  marpy
 * Date:    2013/10/3
 * Description: 测试模块，新建一个窗口用以测试整个系统。
 *              仅供测试用！
 ********************************************************************************/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace MonitorClient
{
    class Test : Processor
    {
        private void CreatForm(Processor sender, EventArgs e)
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new TestForm());
        }

        public Test()
        {
            this.eventHandlePeriod = new TimeSpan(0, 0, 1);
            this.eventHandleTable.Add(typeof(CreatTestFormEvent), this.CreatForm);
        }
    }
}
