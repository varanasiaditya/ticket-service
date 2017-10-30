<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">
<head>
</head>
<p><font color="red">${error}</font></p>
<form>${seatmessage }</form>
<a href="<c:out value="${url}" />">Back to bookings</a>

<c:out value="${table}" />


</html>