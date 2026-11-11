<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<c:set var="nd" value="${dir eq 'asc' ? 'desc' : 'asc'}"/>
<div class="page-head"><h1>Accounts</h1><a class="btn" href="${ctx}/accounts/new">+ Open account</a></div>
<form method="get" class="filter">
  <input type="text" name="q" value="<c:out value='${param.q}'/>" placeholder="Account number">
  <select name="branchId">
    <option value="">All branches</option>
    <c:forEach items="${branches}" var="b"><option value="${b.id}" ${param.branchId == b.id ? 'selected' : ''}><c:out value="${b.name}"/></option></c:forEach>
  </select>
  <button class="btn">Filter</button> <a class="btn secondary" href="${ctx}/accounts">Reset</a>
</form>
<div class="wrap"><table>
  <thead><tr>
    <th><a href="?sort=accountNumber&amp;dir=${nd}&amp;q=<c:out value='${param.q}'/>&amp;branchId=${param.branchId}">Account no.</a></th>
    <th>Holders</th><th>Branch</th><th>Type</th>
    <th class="num"><a href="?sort=balance&amp;dir=${nd}&amp;q=<c:out value='${param.q}'/>&amp;branchId=${param.branchId}">Balance</a></th>
    <th>Status</th><th>Actions</th>
  </tr></thead>
  <tbody>
  <c:forEach items="${data.content}" var="a">
    <tr>
      <td><c:out value="${a.accountNumber}"/></td>
      <td><c:out value="${a.holderNames}"/></td>
      <td><c:out value="${a.branch.name}"/></td>
      <td><c:out value="${a.accountType.name}"/></td>
      <td class="num"><fmt:formatNumber value="${a.balance}" minFractionDigits="2" maxFractionDigits="2"/></td>
      <td><span class="badge ${a.status}">${a.status}</span></td>
      <td>
        <a class="btn small" href="${ctx}/accounts/${a.id}/statement">Statement</a>
        <a class="btn small secondary" href="${ctx}/accounts/${a.id}/edit">Edit</a>
        <form class="inline confirm-delete" method="post" action="${ctx}/accounts/${a.id}/delete"><button class="btn small danger">Delete</button></form>
      </td>
    </tr>
  </c:forEach>
  <c:if test="${empty data.content}"><tr><td colspan="7">No accounts found.</td></tr></c:if>
  </tbody>
</table></div>
<%@ include file="/WEB-INF/jsp/common/pager.jspf" %>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
