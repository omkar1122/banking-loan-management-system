<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="page-head"><h1>${editing ? 'Edit account' : 'Open account'}</h1></div>
<form:form modelAttribute="accountForm" method="post" action="${ctx}/accounts/save" cssClass="card validate" novalidate="novalidate">
  <form:errors path="" element="div" cssClass="alert error"/>
  <form:hidden path="id"/>
  <c:if test="${editing}"><p><b>Account number:</b> <c:out value="${accountNumber}"/></p></c:if>
  <div class="field"><label>Branch</label>
    <form:select path="branchId" required="required">
      <form:option value="" label="-- select branch --"/>
      <form:options items="${branches}" itemValue="id" itemLabel="name"/>
    </form:select><form:errors path="branchId" cssClass="err"/></div>
  <c:choose>
    <c:when test="${editing}">
      <form:hidden path="accountTypeId"/><form:hidden path="initialDeposit"/>
      <div class="field"><label>Status</label>
        <form:select path="status"><form:option value="ACTIVE"/><form:option value="FROZEN"/><form:option value="CLOSED"/></form:select>
        <div class="hint">An account can be CLOSED only when its balance is zero.</div></div>
    </c:when>
    <c:otherwise>
      <div class="field"><label>Account type</label>
        <form:select path="accountTypeId" required="required">
          <form:option value="" label="-- select type --"/>
          <form:options items="${accountTypes}" itemValue="id" itemLabel="name"/>
        </form:select><form:errors path="accountTypeId" cssClass="err"/></div>
      <div class="field"><label>Opening deposit</label>
        <form:input path="initialDeposit" type="number" step="0.01" min="0" required="required"/>
        <div class="hint">Must be at least the account type's minimum balance.</div><form:errors path="initialDeposit" cssClass="err"/></div>
    </c:otherwise>
  </c:choose>
  <div class="field" data-require-one="1"><label>Account holder(s) - select more than one for a joint account</label>
    <c:forEach items="${customers}" var="cu">
      <label class="check"><input type="checkbox" name="holderIds" value="${cu.id}" ${accountForm.holderIds.contains(cu.id) ? 'checked' : ''}>
        <c:out value="${cu.fullName}"/> (<c:out value="${cu.pan}"/>)</label>
    </c:forEach>
    <form:errors path="holderIds" cssClass="err"/>
  </div>
  <button class="btn" type="submit">Save</button> <a class="btn secondary" href="${ctx}/accounts">Cancel</a>
</form:form>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
