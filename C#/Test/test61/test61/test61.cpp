// test61.cpp: 主项目文件。

#include "stdafx.h"
#define AutoEvent 1
using namespace System;
using namespace System::Threading;
using namespace System::Diagnostics;
using namespace System::Timers;
using namespace System::Data::Sql;
using namespace Microsoft::SqlServer::Management::Smo;
using namespace Microsoft::SqlServer::Management::Common;

#if !AutoEvent

void DisplayData(Data::DataTable ^table)
{
	for each (Data::DataRow ^row in table->Rows)
    {
      for each (Data::DataColumn ^col in table->Columns)
      {
        Console::WriteLine("{0} = {1}", col->ColumnName, row[col]);
      }
      Console::WriteLine("============================");
    }
}

int main(array<System::String ^> ^args)
{
	String^ objectName = "Processor";
	String^ counterName = "% Processor Time";
	String^ instanceName = "_Total";
	float value;

	SqlDataSourceEnumerator ^instance=gcnew SqlDataSourceEnumerator()->Instance;
	Data::DataTable ^table=instance->GetDataSources();
	DisplayData(table);
	//Data::Sql::SqlDataSourceEnumerator ^db=gcnew Data::Sql::SqlDataSourceEnumerator();
	//Console::WriteLine(db->Instance);
	//Console::WriteLine(Data::Sql::SqlDataSourceEnumerator::Instance->ToString());
	//Database ^db=gcnew Database();
	//Console::WriteLine("SpaceUsage"+db->DataSpaceUsage);
	
	/*
	PerformanceCounter^ pc = gcnew PerformanceCounter(objectName,counterName);
	PerformanceCounterCategory^ pcc = gcnew PerformanceCounterCategory(objectName); 
	array<String^,1> ^a=pcc->GetInstanceNames();
	for(int i=0; i<a->Length; ++i){
		Console::WriteLine(a[i]);
	}
	Console::WriteLine(Environment::UserDomainName);
	Console::WriteLine(pc->InstanceName);
	
	
	pc->NextValue();
	Thread::Sleep(1000);
	value=pc->NextValue();
	Console::WriteLine("CPU rate:	"+value+"%");
	pc->CounterName="% Interrupt Time";
	value=pc->NextValue();
	Thread::Sleep(1000);
	value=pc->NextValue();
	Console::WriteLine("Interrupt rate:	"+value+"%");
	*/
	return 0;
}

#else
ref struct TimerObject
{
public:
   static String^ m_instanceName;
   static PerformanceCounter^ m_theCounter;

public:
   static void OnTimer(Object^ source, ElapsedEventArgs^ e)
   {
      try 
      {
         Console::WriteLine("CPU time used: {0,16} ",
          m_theCounter->NextValue( ).ToString("f"));
      } 
      catch(Exception^ e)
      {
         if (dynamic_cast<InvalidOperationException^>(e))
         {
            Console::WriteLine("Instance '{0}' does not exist",
                  m_instanceName);
            return;
         }
         else
         {
            Console::WriteLine("Unknown exception... ('q' to quit)");
            return;
         }
      }
   }
};

int main()
{
   String^ objectName = "Memory";
   String^ counterName = "Available Mbytes";
   String^ instanceName = "";
   String^ machineName=".";

   try
   {
      if ( !PerformanceCounterCategory::Exists(objectName) )
      {
         Console::WriteLine("Object {0} does not exist", objectName);
         return -1;
      }
   }
   catch (UnauthorizedAccessException ^ex)
   {
      Console::WriteLine("You are not authorized to access this information.");
      Console::Write("If you are using Windows Vista, run the application with ");
      Console::WriteLine("administrative privileges.");
      Console::WriteLine(ex->Message);
      return -1;
   }

   if ( !PerformanceCounterCategory::CounterExists(
          counterName, objectName) )
   {
      Console::WriteLine("Counter {0} does not exist", counterName);
      return -1;
   }

   TimerObject::m_instanceName = instanceName;
   TimerObject::m_theCounter = gcnew PerformanceCounter(objectName, counterName, instanceName, machineName);
   TimerObject::m_theCounter->NextValue();
   System::Timers::Timer^ aTimer = gcnew System::Timers::Timer();
   aTimer->Elapsed += gcnew ElapsedEventHandler(&TimerObject::OnTimer);
   aTimer->Interval = 1000;
   aTimer->Enabled = true;
   aTimer->AutoReset = true;

   Console::WriteLine("reporting rate for the next 600 seconds");
   Thread::Sleep(10000);

   return 0;
}
#endif
