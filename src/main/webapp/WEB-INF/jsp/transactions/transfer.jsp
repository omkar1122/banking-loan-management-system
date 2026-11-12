<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="page-head"><h1>Fund transfer</h1></div>
<form:form modelAttribute="transferForm" method="post" action="${ctx}/transactions/transfer" cssClass="card validate" novalidate="novalidate">
  <form:errors path="" element="div" cssClass="alert error"/>
  <div class="field"><label>From account</label>
    <form:input path="fromAccount" cssClass="js-balance" data-out="#fromInfo" required="required" autocomplete="off"/>
    <div id="fromInfo" class="hint"></div><form:errors path="fromAccount" cssClass="err"/></div>
  <div class="field"><label>To account</label>
    <form:input path="toAccount" cssClass="js-holders" data-out="#toInfo" required="required" autocomplete="off"/>
    <div id="toInfo" class="hint"></div><form:errors path="toAccount" cssClass="err"/></div>
  <div class="field"><label>Amount</label>
    <form:input path="amount" type="number" step="0.01" min="0.01" required="required" title="Enter an amount greater than zero"/><form:errors path="amount" cssClass="err"/></div>
  <div class="field"><label>Remarks (optional)</label><form:input path="remarks" maxlength="100"/></div>
  <button class="btn" type="submit">Transfer</button>
  <p class="hint">Debit, credit and both ledger entries are saved in one database transaction.</p>
</form:form>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
