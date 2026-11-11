<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<c:set var="nd" value="${dir eq 'asc' ? 'desc' : 'asc'}"/>
<div class="page-head"><h1>Account statement</h1><a class="btn secondary" href="${ctx}/accounts">Back to accounts</a></div>
<div class="card">
  <b><c:out value="${account.accountNumber}"/></b> - <c:out value="${account.holderNames}"/><br>
  <c:out value="${account.branch.name}"/> | <c:out value="${account.accountType.name}"/> |
  Balance: <b><fmt:formatNumber value="${account.balance}" minFractionDigits="2" maxFractionDigits="2"/></b>
</div>
<form method="get" class="filter">
  <label>From <input type="date" name="from" value="${param.from}"></label>
  <label>To <input type="date" name="to" value="${param.to}"></label>
  <button class="btn">Apply</button> <a class="btn secondary" href="${ctx}/accounts/${account.id}/statement">Reset</a>
</form>
<div class="wrap"><table>
  <thead><tr>
    <th><a href="?sort=txnTime&amp;dir=${nd}&amp;from=${param.from}&amp;to=${param.to}">Date and time</a></th>
    <th><a href="?sort=type&amp;dir=${nd}&amp;from=${param.from}&amp;to=${param.to}">Type</a></th>
    <th>Description</th><th>Reference</th>
    <th class="num"><a href="?sort=amount&amp;dir=${nd}&amp;from=${param.from}&amp;to=${param.to}">Amount</a></th>
    <th class="num">Balance after</th>
  </tr></thead>
  <tbody>
  <c:forEach items="${data.content}" var="t">
    <tr>
      <td>${fn:replace(t.txnTime, 'T', ' ')}</td>
      <td><span class="badge ${t.type}">${t.type}</span></td>
      <td><c:out value="${t.description}"/></td><td><c:out value="${t.referenceNo}"/></td>
      <td class="num"><fmt:formatNumber value="${t.amount}" minFractionDigits="2" maxFractionDigits="2"/></td>
      <td class="num"><fmt:formatNumber value="${t.balanceAfter}" minFractionDigits="2" maxFractionDigits="2"/></td>
    </tr>
  </c:forEach>
  <c:if test="${empty data.content}"><tr><td colspan="6">No transactions in this period.</td></tr></c:if>
  </tbody>
</table></div>
<%@ include file="/WEB-INF/jsp/common/pager.jspf" %>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
