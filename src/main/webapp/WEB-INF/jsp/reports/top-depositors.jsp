<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="page-head"><h1>Report: top ${n} depositors</h1></div>
<%@ include file="/WEB-INF/jsp/reports/report-nav.jspf" %>
<form method="get" class="filter">
  <label>Show top <input type="number" name="n" min="1" max="50" value="${n}" style="width:80px"></label>
  <button class="btn">Update</button>
</form>
<div class="wrap"><table>
  <thead><tr><th>Rank</th><th>Customer</th><th>PAN</th><th class="num">Total balance</th></tr></thead>
  <tbody>
  <c:forEach items="${rows}" var="r" varStatus="s">
    <tr><td>${s.index + 1}</td><td><c:out value="${r[0]}"/></td><td><c:out value="${r[1]}"/></td>
      <td class="num"><fmt:formatNumber value="${r[2]}" minFractionDigits="2" maxFractionDigits="2"/></td></tr>
  </c:forEach>
  <c:if test="${empty rows}"><tr><td colspan="4">No data.</td></tr></c:if>
  </tbody>
</table></div>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
