<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="page-head"><h1>Loan types</h1><a class="btn" href="${ctx}/loans/types/new">+ New loan type</a></div>
<div class="wrap"><table>
  <thead><tr><th>Name</th><th class="num">Interest %</th><th class="num">Max tenure (months)</th><th class="num">Income multiple</th><th>Actions</th></tr></thead>
  <tbody>
  <c:forEach items="${types}" var="ltype">
    <tr>
      <td><c:out value="${ltype.name}"/></td><td class="num">${ltype.interestRate}</td><td class="num">${ltype.maxTenureMonths}</td><td class="num">${ltype.incomeMultiple}</td>
      <td>
        <a class="btn small secondary" href="${ctx}/loans/types/${ltype.id}/edit">Edit</a>
        <form class="inline confirm-delete" method="post" action="${ctx}/loans/types/${ltype.id}/delete"><button class="btn small danger">Delete</button></form>
      </td>
    </tr>
  </c:forEach>
  <c:if test="${empty types}"><tr><td colspan="5">No loan types defined.</td></tr></c:if>
  </tbody>
</table></div>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
