<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="page-head"><h1>Report: active accounts below ${threshold}</h1></div>
<%@ include file="/WEB-INF/jsp/reports/report-nav.jspf" %>
<form method="get" class="filter">
  <label>Threshold <input type="number" name="threshold" min="0" step="0.01" value="${threshold}" style="width:140px"></label>
  <button class="btn">Update</button>
</form>
<div class="wrap"><table>
  <thead><tr><th>Account</th><th>Holders</th><th>Branch</th><th class="num">Balance</th></tr></thead>
  <tbody>
  <c:forEach items="${rows}" var="a">
    <tr><td><a href="${ctx}/accounts/${a.id}/statement"><c:out value="${a.accountNumber}"/></a></td>
      <td><c:out value="${a.holderNames}"/></td><td><c:out value="${a.branch.name}"/></td>
      <td class="num"><fmt:formatNumber value="${a.balance}" minFractionDigits="2" maxFractionDigits="2"/></td></tr>
  </c:forEach>
  <c:if test="${empty rows}"><tr><td colspan="4">No accounts below the threshold.</td></tr></c:if>
  </tbody>
</table></div>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
