<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="page-head"><h1>${empty branch.id ? 'New branch' : 'Edit branch'}</h1></div>
<form:form modelAttribute="branch" method="post" action="${ctx}/branches/save" cssClass="card validate" novalidate="novalidate">
  <form:hidden path="id"/>
  <div class="field"><label>Branch name</label><form:input path="name" required="required" maxlength="100"/><form:errors path="name" cssClass="err"/></div>
  <div class="field"><label>IFSC code</label>
    <form:input path="ifscCode" cssClass="uppercase" required="required" maxlength="11" pattern="[A-Z]{4}0[A-Z0-9]{6}" title="IFSC must look like ABCD0123456"/><form:errors path="ifscCode" cssClass="err"/></div>
  <div class="field"><label>City</label><form:input path="city" required="required"/><form:errors path="city" cssClass="err"/></div>
  <div class="field"><label>Address</label><form:input path="address"/></div>
  <div class="field"><label>Phone (optional, 10 digits)</label><form:input path="phone" pattern="[0-9]{10}" title="Phone must be exactly 10 digits"/><form:errors path="phone" cssClass="err"/></div>
  <button class="btn" type="submit">Save</button> <a class="btn secondary" href="${ctx}/branches">Cancel</a>
</form:form>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
