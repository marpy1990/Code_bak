/*
insert 学生信息4(姓名)
values('zzz')

select * from 学生信息4

CREATE TABLE 学生信息5
(
学号 varchar(14) ,
姓名 varchar(8) ,
成绩 varchar(20),
primary key(学号,姓名)
)*/
/*
if object_id(N'学生信息6',N'U') is null
begin
CREATE TABLE 学生信息6
(
学号 varchar(14) ,
姓名 varchar(8) ,
成绩 varchar(20),
primary key(学号,姓名)
)
end
else
begin
insert 学生信息6(学号,姓名,成绩) values('101','zs','223')
end
*/
/*
select * from 学生信息6
*/
/*
if exists(select 1 from master..sysdatabases where name='测试用1') 
print '在' 
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
select * from 监控性能数据 
/*
use MonitorSystem
Go
insert 监控性能数据(机器名,项目名,实例名,日期,性能值) values('SDF-PC','%CPU Usage','2013-10-07-21-30-17','6.14404154') */