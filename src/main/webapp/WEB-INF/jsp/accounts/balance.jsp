<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="page-head"><h1>Balance lookup</h1></div>
<div class="card">
  <div class="field"><label>Account number</label>
    <input type="text" id="lookupAccount" placeholder="e.g. AC1000000001" autocomplete="off"></div>
  <button type="button" class="btn" id="lookupBtn">Check balance</button>
  <p id="lookupResult" style="margin-top:14px;font-weight:600"></p>
  <p class="hint">The result is fetched with AJAX (GET /api/accounts/{number}/balance) without reloading the page.</p>
</div>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
