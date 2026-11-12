$(function () {
  // ---------- client-side form validation (forms with class "validate") ----------
  $('form.validate').on('submit', function (e) {
    var ok = true, $f = $(this);
    $f.find('.err-client').remove();
    $f.find('input, select, textarea').each(function () {
      if (this.type === 'hidden' || this.disabled) return;
      if (!this.checkValidity()) {
        ok = false;
        var msg = this.validity.valueMissing ? 'This field is required' : (this.title || this.validationMessage);
        $('<span class="err err-client"></span>').text(msg).insertAfter(this);
        $(this).addClass('invalid');
      } else { $(this).removeClass('invalid'); }
    });
    // "choose at least one" groups, e.g. account holders
    $f.find('[data-require-one]').each(function () {
      if ($(this).find('input:checked').length === 0) {
        ok = false;
        $('<span class="err err-client"></span>').text('Select at least one option').appendTo(this);
      }
    });
    if (!ok) { e.preventDefault(); $f.find('.invalid:first').focus(); }
  });

  $('.uppercase').on('input', function () { this.value = this.value.toUpperCase(); });
  $('form.confirm-delete').on('submit', function () { return confirm('Are you sure you want to delete this record?'); });
  $('form.confirm-action').on('submit', function () { return confirm($(this).data('msg') || 'Are you sure?'); });

  // ---------- interactive: live table filter ----------
  $('.table-filter').on('input', function () {
    var v = this.value.toLowerCase();
    $($(this).data('target') + ' tbody tr').each(function () {
      $(this).toggle($(this).text().toLowerCase().indexOf(v) > -1);
    });
  });

  // ---------- AJAX 1: balance lookup ----------
  function lookupBalance(no, $out) {
    no = $.trim(no);
    if (!no) { $out.text(''); return; }
    $out.removeClass('info-ok info-bad').text('Checking...');
    $.getJSON(CTX + '/api/accounts/' + encodeURIComponent(no) + '/balance')
      .done(function (d) {
        $out.addClass('info-ok').text('Balance: ' + Number(d.balance).toFixed(2) + ' | Available above minimum: '
            + Number(d.available).toFixed(2) + ' | Status: ' + d.status);
      })
      .fail(function (xhr) { $out.addClass('info-bad').text(xhr.status === 404 ? 'Account not found' : 'Lookup failed'); });
  }
  $('input.js-balance').on('blur', function () { lookupBalance(this.value, $($(this).data('out'))); });
  $('#lookupBtn').on('click', function () { lookupBalance($('#lookupAccount').val(), $('#lookupResult')); });
  $('#lookupAccount').on('keydown', function (e) { if (e.key === 'Enter') { e.preventDefault(); $('#lookupBtn').click(); } });

  // ---------- AJAX 2: destination account check ----------
  $('input.js-holders').on('blur', function () {
    var no = $.trim(this.value), $out = $($(this).data('out'));
    if (!no) { $out.text(''); return; }
    $out.removeClass('info-ok info-bad').text('Checking...');
    $.getJSON(CTX + '/api/accounts/' + encodeURIComponent(no) + '/holders')
      .done(function (d) {
        var cls = d.status === 'ACTIVE' ? 'info-ok' : 'info-bad';
        $out.addClass(cls).text('Holder(s): ' + d.holders.join(', ') + ' | Branch: ' + d.branch + ' | ' + d.status);
      })
      .fail(function (xhr) { $out.addClass('info-bad').text(xhr.status === 404 ? 'Account not found' : 'Lookup failed'); });
  });

  // ---------- interactive: EMI preview on the loan application form ----------
  function emi(p, rate, n) {
    var r = rate / 1200;
    if (r === 0) return p / n;
    var f = Math.pow(1 + r, n);
    return p * r * f / (f - 1);
  }
  function updateEmi() {
    if (typeof LOAN_TYPES === 'undefined') return;
    var lt = LOAN_TYPES[$('#loanTypeId').val()], p = parseFloat($('#amount').val()), n = parseInt($('#tenureMonths').val(), 10);
    var $o = $('#emiPreview');
    if (!lt || !(p > 0) || !(n > 0)) { $o.text('Choose a loan type, amount and tenure to see the EMI.'); return; }
    if (n > lt.max) { $o.addClass('info-bad').text('Maximum tenure for this loan type is ' + lt.max + ' months.'); return; }
    var e = emi(p, lt.rate, n);
    $o.removeClass('info-bad').addClass('info-ok').text('Estimated EMI: ' + e.toFixed(2) + ' per month at ' + lt.rate
        + '% | Total interest: ' + (e * n - p).toFixed(2));
  }
  $('#loanTypeId, #amount, #tenureMonths').on('input change', updateEmi);
  updateEmi();
});
