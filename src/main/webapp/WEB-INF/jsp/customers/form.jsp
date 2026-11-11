<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="page-head"><h1>${empty customer.id ? 'New customer' : 'Edit customer'}</h1></div>
<form:form modelAttribute="customer" method="post" action="${ctx}/customers/save" cssClass="card validate" novalidate="novalidate">
  <form:hidden path="id"/>
  <div class="field"><label>Full name</label>
    <form:input path="fullName" required="required" maxlength="100"/><form:errors path="fullName" cssClass="err"/></div>
  <div class="field"><label>Email</label>
    <form:input path="email" type="email" required="required"/><form:errors path="email" cssClass="err"/></div>
  <div class="field"><label>Phone (10 digits)</label>
    <form:input path="phone" required="required" pattern="[0-9]{10}" title="Phone must be exactly 10 digits"/><form:errors path="phone" cssClass="err"/></div>
  <div class="field"><label>PAN</label>
    <form:input path="pan" cssClass="uppercase" required="required" maxlength="10" pattern="[A-Z]{5}[0-9]{4}[A-Z]" title="PAN must look like ABCDE1234F"/><form:errors path="pan" cssClass="err"/></div>
  <div class="field"><label>Date of birth</label>
    <form:input path="dob" type="date" required="required"/><form:errors path="dob" cssClass="err"/></div>
  <div class="field"><label>Monthly income</label>
    <form:input path="monthlyIncome" type="number" step="0.01" min="0" required="required"/><form:errors path="monthlyIncome" cssClass="err"/></div>
  <div class="field"><label>Address</label><form:input path="address"/></div>
  <button class="btn" type="submit">Save</button> <a class="btn secondary" href="${ctx}/customers">Cancel</a>
</form:form>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
