<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="page-head"><h1>Loan applications</h1>
  <div>
    <a class="btn ${status == 'PENDING' ? '' : 'secondary'}" href="?status=PENDING">Pending</a>
    <a class="btn ${status == 'APPROVED' ? '' : 'secondary'}" href="?status=APPROVED">Approved</a>
    <a class="btn ${status == 'REJECTED' ? '' : 'secondary'}" href="?status=REJECTED">Rejected</a>
  </div>
</div>
<div class="wrap"><table>
  <thead><tr><th>#</th><th>Customer</th><th>Loan type</th><th>Account</th><th class="num">Amount</th><th class="num">Tenure</th><th>Applied</th><th>Status</th><th>Action / remarks</th></tr></thead>
  <tbody>
  <c:forEach items="${applications}" var="ap">
    <tr>
      <td>${ap.id}</td><td><c:out value="${ap.customer.fullName}"/></td><td><c:out value="${ap.loanType.name}"/></td>
      <td><c:out value="${ap.account.accountNumber}"/></td>
      <td class="num"><fmt:formatNumber value="${ap.amount}" minFractionDigits="2" maxFractionDigits="2"/></td>
      <td class="num">${ap.tenureMonths}</td><td>${ap.appliedOn}</td>
      <td><span class="badge ${ap.status}">${ap.status}</span></td>
      <td>
        <c:choose>
          <c:when test="${ap.status == 'PENDING'}">
            <form method="post" action="${ctx}/loans/applications/${ap.id}/approve" class="inline confirm-action" data-msg="Approve this application and credit the amount?">
              <button class="btn small ok">Approve</button></form>
            <form method="post" action="${ctx}/loans/applications/${ap.id}/reject" class="inline">
              <input type="text" name="remarks" placeholder="Reason" required style="width:140px;max-width:140px">
              <button class="btn small danger">Reject</button></form>
          </c:when>
          <c:otherwise><c:out value="${ap.remarks}"/> <small>(${ap.decidedOn})</small></c:otherwise>
        </c:choose>
      </td>
    </tr>
  </c:forEach>
  <c:if test="${empty applications}"><tr><td colspan="9">No ${fn:toLowerCase(status)} applications.</td></tr></c:if>
  </tbody>
</table></div>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
