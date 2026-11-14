<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="page-head"><h1>Report: branch-wise deposits</h1></div>
<%@ include file="/WEB-INF/jsp/reports/report-nav.jspf" %>
<div class="wrap"><table>
  <thead><tr><th>Branch</th><th class="num">Accounts</th><th class="num">Total deposits</th></tr></thead>
  <tbody>
  <c:forEach items="${rows}" var="r">
    <tr><td><c:out value="${r[0]}"/></td><td class="num">${r[1]}</td>
      <td class="num"><fmt:formatNumber value="${r[2]}" minFractionDigits="2" maxFractionDigits="2"/></td></tr>
  </c:forEach>
  <c:if test="${empty rows}"><tr><td colspan="3">No data.</td></tr></c:if>
  </tbody>
</table></div>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
