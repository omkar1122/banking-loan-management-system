<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<c:set var="isDeposit" value="${mode eq 'deposit'}"/>
<div class="page-head"><h1>${isDeposit ? 'Deposit' : 'Withdraw'}</h1></div>
<form:form modelAttribute="cashForm" method="post" action="${ctx}/transactions/${mode}" cssClass="card validate" novalidate="novalidate">
  <form:errors path="" element="div" cssClass="alert error"/>
  <div class="field"><label>Account number</label>
    <form:input path="accountNumber" cssClass="js-balance" data-out="#balInfo" required="required" autocomplete="off"/>
    <div id="balInfo" class="hint"></div><form:errors path="accountNumber" cssClass="err"/></div>
  <div class="field"><label>Amount</label>
    <form:input path="amount" type="number" step="0.01" min="0.01" required="required" title="Enter an amount greater than zero"/><form:errors path="amount" cssClass="err"/></div>
  <div class="field"><label>Description (optional)</label><form:input path="description" maxlength="100"/></div>
  <button class="btn ${isDeposit ? 'ok' : ''}" type="submit">${isDeposit ? 'Deposit' : 'Withdraw'}</button>
  <c:if test="${!isDeposit}"><p class="hint">Rules: the account must keep its minimum balance and stay within the daily withdrawal limit.</p></c:if>
</form:form>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
