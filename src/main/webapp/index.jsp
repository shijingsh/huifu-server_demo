<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<body>
<div class="container">
    <a href="/quickPay/pay" target="_blank">去支付</a>
    <a href="/quickPay/demo/queryStat"  target="_blank">查询支付状态</a>

    <a href="/quickPay/payFrozen" target="_blank">去支付冻结</a>
    <a href="/refund/demo/thaw"  target="_blank">订单解冻</a>

    <a href="/refund/refund" target="_blank">退款</a>
    <a href="/refund/demo/queryStat"  target="_blank">查询退款状态</a>

    <a href="/account/open"  target="_blank">个人开户</a>
    <a href="/account/openCompany"  target="_blank">企业开户</a>

    <a href="/transQuery/balance?inCustId=6666000000054655&inAcctId=64782"  target="_blank">查询中间帐号余额</a>
    <a href="/transQuery/balance"  target="_blank">查询测试帐号余额</a>


    <div>
        <ul>
            <li>
                <a href="/account/demo/queryStat"  target="_blank">查询企业开户状态</a>
            </li>
        </ul>
    </div>
</div>

</body>


</html>
