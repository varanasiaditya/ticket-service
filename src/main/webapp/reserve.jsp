<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">
<head>
</head>
<p><font color="red">${error}</font></p></br>
<p>You session will exipre in 30 seconds....</p>
<form action="<c:out value="${url}" />">
	Ref ID : ${seatHold.seatId }
	Seat Number :${seatHold.seatInfo } <br> <input type="hidden" name="seatHoldId" value="<c:out value="${seatHold.seatId }" />"><br>
	Email:${seatHold.customerEmail }<br> <input type="text" name="email" value="<c:out value="${seatHold.customerEmail }" />">
	<input type="submit" value="Reserve" />
</form>

<c:out value="${table}" />
</html>