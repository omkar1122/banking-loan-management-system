<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="card" style="text-align:center">
  <h1 style="font-size:48px;color:var(--bad)"><c:out value="${empty errCode ? status : errCode}"/></h1>
  <h2><c:out value="${empty errTitle ? 'Error' : errTitle}"/></h2>
  <p><c:out value="${empty errMessage ? message : errMessage}"/></p>
  <a class="btn" href="${ctx}/">Back to dashboard</a>
</div>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
