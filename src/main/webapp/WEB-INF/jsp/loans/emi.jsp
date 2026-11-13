<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="page-head"><h1>EMI schedule - Loan #${loan.id}</h1><a class="btn secondary" href="${ctx}/loans">Back to loans</a></div>
<div class="card">
  <c:out value="${loan.customer.fullName}"/> | <c:out value="${loan.loanType.name}"/> |
  Principal <b><fmt:formatNumber value="${loan.principal}" minFractionDigits="2" maxFractionDigits="2"/></b> |
  ${loan.annualRate}% for ${loan.tenureMonths} months | EMI <b><fmt:formatNumber value="${loan.emiAmount}" minFractionDigits="2" maxFractionDigits="2"/></b> |
  Pays from <c:out value="${loan.account.accountNumber}"/> | <span class="badge ${loan.status}">${loan.status}</span>
  <c:if test="${loan.status == 'ACTIVE'}">
    <form method="post" action="${ctx}/loans/${loan.id}/pay-emi" class="inline confirm-action" data-msg="Debit the next EMI from the loan account?" style="margin-left:12px">
      <button class="btn small ok">Pay next EMI</button></form>
  </c:if>
</div>
<div class="wrap"><table>
  <thead><tr><th>#</th><th>Due date</th><th class="num">EMI</th><th class="num">Principal</th><th class="num">Interest</th><th class="num">Balance</th><th>Status</th></tr></thead>
  <tbody>
  <c:forEach items="${schedule}" var="e">
    <tr><td>${e.installmentNo}</td><td>${e.dueDate}</td>
      <td class="num"><fmt:formatNumber value="${e.emiAmount}" minFractionDigits="2" maxFractionDigits="2"/></td>
      <td class="num"><fmt:formatNumber value="${e.principalComponent}" minFractionDigits="2" maxFractionDigits="2"/></td>
      <td class="num"><fmt:formatNumber value="${e.interestComponent}" minFractionDigits="2" maxFractionDigits="2"/></td>
      <td class="num"><fmt:formatNumber value="${e.balanceOutstanding}" minFractionDigits="2" maxFractionDigits="2"/></td>
      <td><span class="badge ${e.status}">${e.status}</span> <c:if test="${e.status == 'PAID'}"><small>${e.paidOn}</small></c:if></td></tr>
  </c:forEach>
  </tbody>
</table></div>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
