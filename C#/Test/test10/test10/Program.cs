using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Security;
using System.Security.Cryptography;
using System.Collections;
/*
using System.ServiceModel;
using System.ServiceModel.Web;
using System.Runtime.Serialization.Json;
using System.IO;
*/
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using System.Data.SqlClient;
using Probe;

namespace test10
{
   

    class Program
    {
        static void Main(string[] args)
        {
            /*
            string name=Environment.MachineName;
            SqlConnection con = new SqlConnection();
            con.ConnectionString = "server=.;integrated security=true;database=监控系统"; 
            con.Open();
            //string sql = "insert into 学生信息3(学号,姓名) values('110234','3a2aa')";
            SqlCommand cmd = new SqlCommand(sql, con);
            cmd.ExecuteNonQuery();
            con.Close();
            DateTime d = DateTime.Now;
            Console.WriteLine(d.ToString("yyyy-MM-dd HH:mm:ss"));
             * */
            /*
            Probe.Probe p = new SQLProbe.SQLProbe();
            DateTime d = DateTime.Now;
            foreach (Probe.DetectedData data in p.GetValues())
            {
                Console.WriteLine("{0}  {1}  {2}",data.categoryName,data.instanceName,data.value);
            }*/

            Probe.Probe p = new IIProbe.IISProbe();
            foreach (Probe.DetectedData data in p.GetValues())
            {
                Console.WriteLine("{0}  {1}  {2}", data.categoryName, data.instanceName, data.value);
            }
          
        } 
    }
}
