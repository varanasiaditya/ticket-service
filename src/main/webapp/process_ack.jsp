<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">
<head>
</head>
<p><font color="red">${error}</font></p></br>
<form action="<c:out value="${url}" />">
<p>Please provide your ID and email to reserve that is sent to your mail.</p></br>

	ID:<br> <input type="text" name="seatHoldId" ><br>
	Email:${seatHold.customerEmail }<br> <input type="text" name="email" ">
	<input type="submit" value="Reserve" />
</form>

<c:out value="${table}" />
</html>