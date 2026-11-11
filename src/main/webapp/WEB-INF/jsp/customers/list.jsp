<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<c:set var="nd" value="${dir eq 'asc' ? 'desc' : 'asc'}"/>
<div class="page-head"><h1>Customers</h1><a class="btn" href="${ctx}/customers/new">+ New customer</a></div>
<form method="get" class="filter">
  <input type="text" name="q" value="<c:out value='${param.q}'/>" placeholder="Search by name or PAN">
  <button class="btn">Search</button> <a class="btn secondary" href="${ctx}/customers">Reset</a>
</form>
<div class="wrap"><table>
  <thead><tr>
    <th><a href="?sort=fullName&amp;dir=${nd}&amp;q=<c:out value='${param.q}'/>">Name</a></th>
    <th>Email</th><th>Phone</th>
    <th><a href="?sort=pan&amp;dir=${nd}&amp;q=<c:out value='${param.q}'/>">PAN</a></th>
    <th class="num"><a href="?sort=monthlyIncome&amp;dir=${nd}&amp;q=<c:out value='${param.q}'/>">Monthly income</a></th>
    <th>Actions</th>
  </tr></thead>
  <tbody>
  <c:forEach items="${data.content}" var="cu">
    <tr>
      <td><c:out value="${cu.fullName}"/></td><td><c:out value="${cu.email}"/></td><td><c:out value="${cu.phone}"/></td>
      <td><c:out value="${cu.pan}"/></td>
      <td class="num"><fmt:formatNumber value="${cu.monthlyIncome}" minFractionDigits="2" maxFractionDigits="2"/></td>
      <td>
        <a class="btn small secondary" href="${ctx}/customers/${cu.id}/edit">Edit</a>
        <form class="inline confirm-delete" method="post" action="${ctx}/customers/${cu.id}/delete"><button class="btn small danger">Delete</button></form>
      </td>
    </tr>
  </c:forEach>
  <c:if test="${empty data.content}"><tr><td colspan="6">No customers found.</td></tr></c:if>
  </tbody>
</table></div>
<%@ include file="/WEB-INF/jsp/common/pager.jspf" %>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
