<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">
<head>
</head>
<p>Data is refreshed</p><BR/>
<p><a href="/async/startBookings">Click here to use Queue method</a></p>
<p><a href="/dao/startBookings">Click here to use DAO method</a></p><BR/>
<p><a href="/hz/startBookings">Click here to use Hazelcast method</a></p>
</html>