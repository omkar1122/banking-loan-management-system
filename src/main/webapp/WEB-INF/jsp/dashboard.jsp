<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="page-head"><h1>Dashboard</h1></div>
<div class="grid">
  <div class="stat"><div class="n">${customerCount}</div><div class="l">Customers</div></div>
  <div class="stat"><div class="n">${branchCount}</div><div class="l">Branches</div></div>
  <div class="stat"><div class="n">${accountCount}</div><div class="l">Accounts</div></div>
  <div class="stat"><div class="n"><fmt:formatNumber value="${totalDeposits}" type="number" minFractionDigits="2" maxFractionDigits="2"/></div><div class="l">Total deposits</div></div>
  <div class="stat"><div class="n">${pendingApplications}</div><div class="l"><a href="${ctx}/loans/applications">Pending loan applications</a></div></div>
  <div class="stat"><div class="n">${activeLoans}</div><div class="l"><a href="${ctx}/loans">Active loans</a></div></div>
  <div class="stat"><div class="n">${overdueEmis}</div><div class="l"><a href="${ctx}/reports/overdue-emis">Overdue EMIs</a></div></div>
  <div class="stat"><div class="n">${todayTxnCount}</div><div class="l">Transactions today</div></div>
</div>
<div class="card">
  <h3 style="margin-top:0">Recent activity</h3>
  <div class="wrap"><table>
    <thead><tr><th>Time</th><th>Account</th><th>Type</th><th class="num">Amount</th><th>Description</th></tr></thead>
    <tbody>
    <c:forEach items="${recent}" var="t">
      <tr>
        <td>${fn:replace(t.txnTime, 'T', ' ')}</td>
        <td><a href="${ctx}/accounts/${t.account.id}/statement"><c:out value="${t.account.accountNumber}"/></a></td>
        <td><span class="badge ${t.type}">${t.type}</span></td>
        <td class="num"><fmt:formatNumber value="${t.amount}" minFractionDigits="2" maxFractionDigits="2"/></td>
        <td><c:out value="${t.description}"/></td>
      </tr>
    </c:forEach>
    <c:if test="${empty recent}"><tr><td colspan="5">No transactions yet.</td></tr></c:if>
    </tbody>
  </table></div>
</div>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
