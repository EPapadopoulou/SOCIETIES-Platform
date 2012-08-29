<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Societies services - Create CIS - Privacy Policy</title>
<style>
.error {
	color: #ff0000;
}

.errorblock {
	color: #000;
	background-color: #ffEEEE;
	border: 3px solid #ff0000;
	padding: 8px;
	margin: 16px;
}
label{
display: block;
padding: 0;
margin:  2px 0;
text-align: left;
font-size: 1.2em;
}
label.inline{
display: inline;
}
input, select{
margin-bottom: 20px;
padding: 5px;
}
.clear{
display block;
}
th{
text-align: center
}
</style>
</head>

<body>
	<!-- HEADER -->
	<jsp:include page="../../header.jsp" />
	<!-- END HEADER -->

	<!-- LEFTBAR -->
	<jsp:include page="../../leftbar.jsp" />
	<!-- END LEFTBAR -->
	<!-- .................PLACE YOUR CONTENT HERE ................ -->

	<h3>CIS Manager Service</h3>
	<h4><strong>CIS Creation</strong> - Step 1 > <strong>Step 2: privacy policy</strong> > Step 3</h4>

	<form:form method="POST" action="create-cis-step-3.html" commandName="cisCreationForm" class="updatePrivacyPolicy">
	<p>
		<pre><c:out value="${ResultMsg}" /></pre>
	</p>
		<%-- <fieldset class="simple-mode">
			<legend>Simple mode: CIS</legend>
			<label for="mode1">Actions to apply on this resource</label>
			<form:checkbox path="mode" value="PRIVATE" />
			<label for="mode1" class="inline">Private: nobody can access CIS data</label>
			<form:checkbox path="mode" value="SHARED" />
			<label for="mode2" class="inline">Shared: only CIS members can access CIS data</label>
			<form:checkbox path="mode" value="PUBLIC" />
			<label for="mode3" class="inline">Public: everybody can access CIS data</label>
			<form:errors path="mode" cssClass="error" />
		</fiedlset> --%>
		<fieldset class="advanced-mode">
			<legend>Advanced mode: Requested data by the CIS</legend>
			<c:forEach var="resource" items="${cisCreationForm.resources}" varStatus="status">
				<c:choose>
					<c:when test="${status.count == fn:length(cisCreationForm.resources)}">
						<fieldset class="requestedData lastResource resource${status.index}">
					</c:when>
					<c:otherwise>
						<fieldset class="requestedData resource${status.index}">
					</c:otherwise>
				</c:choose>
				<legend>Resource #${status.count}</legend>

					<form:label path="resources[${status.index}].resourceType">Resource Type</form:label>
					<form:select path="resources[${status.index}].resourceType"
						class="resources${status.index}resourceType">
						<option value="NONE">--- Select ---</option>
						<c:forEach var="resourceType" items="${ResourceList}" varStatus="statusResource">
							<form:option value="${resourceType}">${ResourceHumanList[statusResource.index]}</form:option>
						</c:forEach>
					</form:select>
					<form:errors path="resources[${status.index}].resourceType"
						cssClass="error" />
					<form:label path="resources[${status.index}].resourceTypeCustom"
						class="inline">or tip a custom one</form:label>
					<!--Scheme-->
					<form:select path="resources[${status.index}].resourceSchemeCustom"
						class="resources${status.index}resourceSchemeCustom">
						<c:forEach var="resourceScheme" items="${ResourceSchemeList}">
							<form:option value="${resourceScheme}">${resourceScheme}</form:option>
						</c:forEach>
					</form:select>
					<span class="resources${status.index}resourceSchemeError error"></span>
					<form:errors path="resources[${status.index}].resourceSchemeCustom"
						cssClass="error" />
					<!-- Type -->
					<form:input path="resources[${status.index}].resourceTypeCustom"
						class="resources${status.index}resourceTypeCustom"
						placeholder="e.g. mood" />
					<span class="resources${status.index}resourceTypeError error"></span>
					<form:errors path="resources[${status.index}].resourceTypeCustom"
						cssClass="error" />


					<div class="clear"></div>

					<%-- <form:label path="resources[${status.index}].optional" class="inline">Optional?</form:label>
				<form:checkbox path="resources[${status.index}].optional" value="1" />
				<form:errors path="resources[${status.index}].optional" cssClass="error" /> --%>

					<label for="resources[${status.index}].actions1">Actions to apply on this resource</label>
					<c:forEach var="action" items="${ActionList}"
						varStatus="statusAction">
						<form:checkbox
							path="resources[${status.index}].actions[${statusAction.index}].action"
							value="${action}" />
						<label for="resources${status.index}.actions${statusAction.index}.action1'" class="inline">${action}</label>
					(<form:checkbox
							path="resources[${status.index}].actions[${statusAction.index}].optional"
							value="1" />
						<label for="resources${status.index}.actions${statusAction.index}.optional1" class="inline">Optional?</label>)
					<div class="clear"></div>
					</c:forEach>
					<form:errors path="resources[${status.index}].actions"
						cssClass="error" />

					<label for="resources${status.index}conditionTypeAdd">Add a
						condition</label>
					<table>
						<thead>
							<tr>
								<th>Condition type</th>
								<th>Condition value</th>
								<th>Optional?</th>
								<th>Action</th>
							</tr>
						</thead>
						<tbody>
							<tr
								class="resources${status.index}conditionAddAction conditionFromResource${status.index}">
								<td><select
									name="resources${status.index}.conditionTypeAdd"
									id="resources${status.index}conditionTypeAdd">
										<option value="NONE">--- Select ---</option>
										<c:forEach var="condition" items="${ConditionList}">
											<option value="${condition}" /> ${condition}
									</c:forEach>
								</select></td>
								<td><input type="text"
									name="resources${status.index}.conditionValueAdd"
									id="resources${status.index}conditionValueAdd" /></td>
								<td><input type="checkbox"
									name="resources${status.index}.conditionOptionalAdd"
									id="resources${status.index}conditionOptionalAdd" value="1" />
								</td>
								<td><input type="button" name="resources${status.index}"
									class="addCondition" value="Add" /></td>
							</tr>
							<c:forEach var="condition" items="${cisCreationForm.resources[status.index].conditions}" varStatus="statusCondition">
								<c:choose>
									<c:when
										test="${statusCondition.count == fn:length(cisCreationForm.resources[status.index].conditions)}">
										<tr class="conditionFromResource${status.index} lastCondition${status.index}">
									</c:when>
									<c:otherwise>
										<tr class="conditionFromResource${status.index}">
									</c:otherwise>
								</c:choose>
								<td><form:select path="resources[${status.index}].conditions[${statusCondition.index}].theCondition">
										<form:option value="NONE">--- Select ---</form:option>
										<c:forEach var="conditionType" items="${ConditionList}"
											varStatus="statusConditionList">
											<form:option value="${conditionType}">${conditionType}</form:option>
										</c:forEach>
									</form:select> <form:errors
										path="resources[${status.index}].conditions[${statusCondition.index}].theCondition"
										cssClass="error" /></td>
								<td><form:input
										path="resources[${status.index}].conditions[${statusCondition.index}].value" />
									<form:errors
										path="resources[${status.index}].conditions[${statusCondition.index}].value"
										cssClass="error" /></td>
								<td><form:checkbox
										path="resources[${status.index}].conditions[${statusCondition.index}].optional"
										value="1" /> <form:errors
										path="resources[${status.index}].conditions[${statusCondition.index}].optional"
										cssClass="error" /></td>
								<td class="action"><c:if
										test="${statusCondition.count == fn:length(cisCreationForm.resources[status.index].conditions)}">
										<input type="button" name="resources${status.index}"
											class="removeCondition removeCondition${status.index}"
											value="Remove" />
									</c:if></td>
								</tr>
								<c:set var="ConditionNumber" value="${statusCondition.count}" />
							</c:forEach>
							<input type="hidden" name="resources${status.index}.resourceId"
								id="resources${status.index}resourceId" value="${status.index}" />
							<input type="hidden" name="resources${status.index}.conditionId"
								id="resources${status.index}conditionId"
								value="${ConditionNumber}" />
						</tbody>
					</table>
					<%-- <c:if test="${status.count == fn:length(cisCreationForm.resources)}"> --%>
						<input type="button" name="resource${status.index}"
							class="removeResource removeResource${status.index}"
							value="Remove this resource" />
						<input type="hidden" name="resource${status.index}" id="resource${status.index}" value="${status.index}" />
					<%-- </c:if> --%>
				</fieldset>
			</c:forEach>

			<input type="button" value="Add a requested data"
				class="addRequestedData" />
		</fieldset>
		<!-- CIS Creation Data -->
		<input type="hidden" name="method" value="${cisCreationForm.method}" />
		<input type="hidden" name="cisName" value="${cisCreationForm.cisName}" />
		<input type="hidden" name="cisType" value="${cisCreationForm.cisType}" />
		<input type="hidden" name="attribute" value="${cisCreationForm.attribute}" />
		<input type="hidden" name="operator" value="${cisCreationForm.operator}" />
		<input type="hidden" name="value" value="${cisCreationForm.value}" />
		
		<input type="submit" value="Submit" />
		<a href="cismanager.html">Cancel</a>
		<span class="globalError error"></span>
	</form:form>


	<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="../../footer.jsp" />
	<!-- END FOOTER -->
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
	<script type="text/javascript">
	var actionList = [<c:forEach var="action" items="${ActionList}">"${action}", </c:forEach>];
	var conditionList = [<c:forEach var="condition" items="${ConditionList}">"${condition}", </c:forEach>];
	var resourceTypeList = [<c:forEach var="resourceType" items="${ResourceList}">"${resourceType}", </c:forEach>];
	var resourceTypeHumanList = [<c:forEach var="resourceType" items="${ResourceHumanList}">"${resourceType}", </c:forEach>];
	var resourceSchemeList = [<c:forEach var="resourceScheme" items="${ResourceSchemeList}">"${resourceScheme}", </c:forEach>];
	var lastResourceId = ${fn:length(privacyPolicy.resources)};
	</script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/privacypolicy/form.js"></script>
</body>
</html>

