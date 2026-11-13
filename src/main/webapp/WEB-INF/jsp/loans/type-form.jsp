<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="page-head"><h1>${empty loanType.id ? 'New loan type' : 'Edit loan type'}</h1></div>
<form:form modelAttribute="loanType" method="post" action="${ctx}/loans/types/save" cssClass="card validate" novalidate="novalidate">
  <form:hidden path="id"/>
  <div class="field"><label>Name</label><form:input path="name" required="required"/><form:errors path="name" cssClass="err"/></div>
  <div class="field"><label>Annual interest rate (%)</label>
    <form:input path="interestRate" type="number" step="0.01" min="0" max="40" required="required"/><form:errors path="interestRate" cssClass="err"/></div>
  <div class="field"><label>Maximum tenure (months)</label>
    <form:input path="maxTenureMonths" type="number" min="1" max="480" required="required"/><form:errors path="maxTenureMonths" cssClass="err"/></div>
  <div class="field"><label>Income multiple</label>
    <form:input path="incomeMultiple" type="number" step="0.01" min="1" required="required"/>
    <div class="hint">Maximum loan = multiple x customer's monthly income.</div><form:errors path="incomeMultiple" cssClass="err"/></div>
  <button class="btn" type="submit">Save</button> <a class="btn secondary" href="${ctx}/loans/types">Cancel</a>
</form:form>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
