<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="page-head"><h1>Report: overdue EMIs (as of ${today})</h1></div>
<%@ include file="/WEB-INF/jsp/reports/report-nav.jspf" %>
<div class="wrap"><table>
  <thead><tr><th>Loan</th><th>Customer</th><th>Instalment</th><th>Due date</th><th class="num">Amount</th></tr></thead>
  <tbody>
  <c:forEach items="${rows}" var="e">
    <tr><td><a href="${ctx}/loans/${e.loan.id}/emi">#${e.loan.id}</a></td><td><c:out value="${e.loan.customer.fullName}"/></td>
      <td>${e.installmentNo} / ${e.loan.tenureMonths}</td><td>${e.dueDate}</td>
      <td class="num"><fmt:formatNumber value="${e.emiAmount}" minFractionDigits="2" maxFractionDigits="2"/></td></tr>
  </c:forEach>
  <c:if test="${empty rows}"><tr><td colspan="5">No overdue instalments.</td></tr></c:if>
  </tbody>
</table></div>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
