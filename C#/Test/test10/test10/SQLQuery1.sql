/*
insert ѧ����Ϣ4(����)
values('zzz')

select * from ѧ����Ϣ4

CREATE TABLE ѧ����Ϣ5
(
ѧ�� varchar(14) ,
���� varchar(8) ,
�ɼ� varchar(20),
primary key(ѧ��,����)
)*/
/*
if object_id(N'ѧ����Ϣ6',N'U') is null
begin
CREATE TABLE ѧ����Ϣ6
(
ѧ�� varchar(14) ,
���� varchar(8) ,
�ɼ� varchar(20),
primary key(ѧ��,����)
)
end
else
begin
insert ѧ����Ϣ6(ѧ��,����,�ɼ�) values('101','zs','223')
end
*/
/*
select * from ѧ����Ϣ6
*/
/*
if exists(select 1 from master..sysdatabases where name='������1') 
print '��' 
else 
begin
*/
/*
create database MonitorSystem
ON
(NAME=MonitorSystem_DATA,
FILENAME='D:\SQL Server\DATABASE\MonitorSystem.mdf',
SIZE=10MB,
MAXSIZE=15MB,
FILEGROWTH=10%
)
LOG ON
(NAME=MonitorSystem_LOG,
FILENAME='D:\SQL Server\DATABASE\MonitorSystem.ldf',
SIZE=3MB,
MAXSIZE=8MB,
FILEGROWTH=5%
)*/

use MonitorSystem
Go
select * from ����������� 
/*
use MonitorSystem
Go
insert �����������(������,��Ŀ��,ʵ����,����,����ֵ) values('SDF-PC','%CPU Usage','2013-10-07-21-30-17','6.14404154') */