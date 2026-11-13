<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="page-head"><h1>Loans</h1></div>
<h3>Active loans</h3>
<div class="wrap"><table>
  <thead><tr><th>#</th><th>Customer</th><th>Type</th><th class="num">Principal</th><th class="num">Rate %</th><th class="num">Tenure</th><th class="num">EMI</th><th>Disbursed</th><th>Schedule</th></tr></thead>
  <tbody>
  <c:forEach items="${activeLoans}" var="l">
    <tr><td>${l.id}</td><td><c:out value="${l.customer.fullName}"/></td><td><c:out value="${l.loanType.name}"/></td>
      <td class="num"><fmt:formatNumber value="${l.principal}" minFractionDigits="2" maxFractionDigits="2"/></td>
      <td class="num">${l.annualRate}</td><td class="num">${l.tenureMonths}</td>
      <td class="num"><fmt:formatNumber value="${l.emiAmount}" minFractionDigits="2" maxFractionDigits="2"/></td>
      <td>${l.disbursedOn}</td><td><a class="btn small" href="${ctx}/loans/${l.id}/emi">EMI schedule</a></td></tr>
  </c:forEach>
  <c:if test="${empty activeLoans}"><tr><td colspan="9">No active loans.</td></tr></c:if>
  </tbody>
</table></div>
<h3>Closed loans</h3>
<div class="wrap"><table>
  <thead><tr><th>#</th><th>Customer</th><th>Type</th><th class="num">Principal</th><th>Schedule</th></tr></thead>
  <tbody>
  <c:forEach items="${closedLoans}" var="l">
    <tr><td>${l.id}</td><td><c:out value="${l.customer.fullName}"/></td><td><c:out value="${l.loanType.name}"/></td>
      <td class="num"><fmt:formatNumber value="${l.principal}" minFractionDigits="2" maxFractionDigits="2"/></td>
      <td><a class="btn small secondary" href="${ctx}/loans/${l.id}/emi">View</a></td></tr>
  </c:forEach>
  <c:if test="${empty closedLoans}"><tr><td colspan="5">No closed loans.</td></tr></c:if>
  </tbody>
</table></div>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
