<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<c:set var="nd" value="${dir eq 'asc' ? 'desc' : 'asc'}"/>
<div class="page-head"><h1>Branches</h1><a class="btn" href="${ctx}/branches/new">+ New branch</a></div>
<form method="get" class="filter">
  <input type="text" name="q" value="<c:out value='${param.q}'/>" placeholder="Search name, city or IFSC">
  <button class="btn">Search</button> <a class="btn secondary" href="${ctx}/branches">Reset</a>
</form>
<div class="wrap"><table>
  <thead><tr>
    <th><a href="?sort=name&amp;dir=${nd}&amp;q=<c:out value='${param.q}'/>">Name</a></th>
    <th><a href="?sort=ifscCode&amp;dir=${nd}&amp;q=<c:out value='${param.q}'/>">IFSC</a></th>
    <th><a href="?sort=city&amp;dir=${nd}&amp;q=<c:out value='${param.q}'/>">City</a></th><th>Phone</th><th>Actions</th>
  </tr></thead>
  <tbody>
  <c:forEach items="${data.content}" var="b">
    <tr>
      <td><c:out value="${b.name}"/></td><td><c:out value="${b.ifscCode}"/></td><td><c:out value="${b.city}"/></td><td><c:out value="${b.phone}"/></td>
      <td>
        <a class="btn small secondary" href="${ctx}/branches/${b.id}/edit">Edit</a>
        <form class="inline confirm-delete" method="post" action="${ctx}/branches/${b.id}/delete"><button class="btn small danger">Delete</button></form>
      </td>
    </tr>
  </c:forEach>
  <c:if test="${empty data.content}"><tr><td colspan="5">No branches found.</td></tr></c:if>
  </tbody>
</table></div>
<%@ include file="/WEB-INF/jsp/common/pager.jspf" %>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
