<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">
<head>
</head>
<p><font color="red">${error}</font></p><br/>
Numer of seats Avilable :: <c:out value="${numofseats }" /><br/>
<form action="<c:out value="${url}" />">
	Number of seat :<br> <input type="text" name="noOfSeats"><br>
	Email:<br> <input type="text" name="email">
	<input type="submit" value="Click to proceed" />
</form>
<c:out value="${table}" />
</html>