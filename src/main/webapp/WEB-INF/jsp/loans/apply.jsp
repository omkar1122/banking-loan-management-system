<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="page-head"><h1>Loan application</h1></div>
<script>
var LOAN_TYPES = {
<c:forEach items="${loanTypes}" var="ltype" varStatus="s">"${ltype.id}": {rate: ${ltype.interestRate}, max: ${ltype.maxTenureMonths}}${s.last ? '' : ','}
</c:forEach>};
</script>
<form:form modelAttribute="loanApplicationForm" method="post" action="${ctx}/loans/apply" cssClass="card validate" novalidate="novalidate">
  <form:errors path="" element="div" cssClass="alert error"/>
  <div class="field"><label>Customer</label>
    <form:select path="customerId" required="required">
      <form:option value="" label="-- select customer --"/>
      <c:forEach items="${customers}" var="cu"><form:option value="${cu.id}"><c:out value="${cu.fullName}"/> (income <fmt:formatNumber value="${cu.monthlyIncome}" maxFractionDigits="0"/>)</form:option></c:forEach>
    </form:select><form:errors path="customerId" cssClass="err"/></div>
  <div class="field"><label>Loan type</label>
    <form:select path="loanTypeId" required="required">
      <form:option value="" label="-- select loan type --"/>
      <c:forEach items="${loanTypes}" var="ltype"><form:option value="${ltype.id}"><c:out value="${ltype.name}"/> - ${ltype.interestRate}%</form:option></c:forEach>
    </form:select><form:errors path="loanTypeId" cssClass="err"/></div>
  <div class="field"><label>Disbursement account (must belong to the customer)</label>
    <form:select path="accountId" required="required">
      <form:option value="" label="-- select account --"/>
      <c:forEach items="${accounts}" var="a"><form:option value="${a.id}"><c:out value="${a.accountNumber}"/> - <c:out value="${a.holderNames}"/></form:option></c:forEach>
    </form:select><form:errors path="accountId" cssClass="err"/></div>
  <div class="field"><label>Loan amount</label>
    <form:input path="amount" type="number" step="0.01" min="1000" required="required" title="Minimum loan amount is 1000"/><form:errors path="amount" cssClass="err"/></div>
  <div class="field"><label>Tenure (months)</label>
    <form:input path="tenureMonths" type="number" min="1" required="required"/><form:errors path="tenureMonths" cssClass="err"/></div>
  <p id="emiPreview" class="hint"></p>
  <button class="btn" type="submit">Submit application</button>
</form:form>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
