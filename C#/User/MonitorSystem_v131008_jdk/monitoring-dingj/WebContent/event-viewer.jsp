<%@page import="monitoring.manager.DBHelper"%>
<%@ page language="java"  pageEncoding="UTF-8" isELIgnored="false"%> 
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	String mName = request.getParameter("name");
	request.setAttribute("name", mName);
	request.setAttribute("events", DBHelper.INSTANCE.queryEventByMachineName(mName));
%>
<!DOCTYPE html>
<html>
<head>
<title>Distributed Monitoring System</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link href="bootstrap/css/bootstrap.min.css" rel="stylesheet"
	media="screen">
<link href="css/main.css" rel="stylesheet">
<script src="js/jquery.js"></script>
<script src="bootstrap/js/bootstrap.min.js"></script>

<script type="text/javascript"
    src="dwr/engine.js"> </script>
<script type="text/javascript"
    src="dwr/util.js"> </script>
<script type="text/javascript"
    src="dwr/interface/DB.js"> </script>
<!--[if lt IE 9]>
  <script src="../../assets/js/html5shiv.js"></script>
  <script src="../../assets/js/respond.min.js"></script>
<![endif]-->
<script type="text/javascript">
</script>
</head>
<body>
	<nav class="navbar navbar-default navbar-fixed-top" role="navigation">
		<div class="navbar-header">
			<a class="navbar-brand" href="#">Distributed Monitoring System</a>
			<ul class="nav navbar-nav">
				<li><a href="#">Home</a></li>
				<li class="active"><a href="#">Machine</a></li>
				<li><a href="#">Prediction</a></li>
				<li><a href="#">Anomaly Analysis</a></li>
			</ul>
		</div>
	</nav>
	<div class="row" style="padding-top: 50px;">
		<div class="col-md-2">
			<div id="machine-list" class="list-group" >
			<div class="form-group">
			  <label for="machine-filter">Quick filter:</label>
			  <input type="email" class="form-control" id="machine-filter" placeholder="Enter machine name...">
			</div>
			<c:forEach items="${applicationScope.DB.machineList }" var="li">
			<a href="probe-setting.jsp?name=${li }">${li }</a>
			</c:forEach>
			</div>
		</div>
		<div class="col-md-10">
		<div class="page-header">
		  <h3>${requestScope.name}</h3>
		</div>
		<ul class="nav nav-tabs">
		  <li><a href="probe-setting.jsp?name=${requestScope.name }">Probe Settings</a></li>
		  <li class="active"><a href="event-viewer.jsp?name=${requestScope.name }">Event Viewer</a></li>
		  <li><a href="graph.jsp?name=${requestScope.name }">Graph</a></li>
		
		</ul>
		<table class="table table-condensed probe-table">
		<colgroup>
			<col width="10px"/>
			<col width="200px"/>
			<col width="100px"/>
			<col width="150px"/>
			<col width="auto"/>
		</colgroup>
		<thead><tr><th>#</th><th>Type</th><th>Instance Name</th>
		<th>Timestamp</th>
		<th>Value</th></tr></thead>
		<tbody>
		<c:forEach var="p" items="${requestScope.events }">
		<tr>
		<td>${p.id }</td>
		<td>${p.type }</td>
		<td>${p.instanceName }</td>
		<td>${p.timestamp }</td>
		<td>${p.value }</td>
		</tr>
		</c:forEach>
		</tbody>
		</table>
		</div>
	</div>


</body>
</html>