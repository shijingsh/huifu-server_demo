<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<body>
<div class="container">
    <table>
        <tr>
            <td><a href="/appPay/pay?pay_type=10" target="_blank">APP支付</a></td>
        </tr>
        <tr>
            <td>
                <a href="/quickPay/pay" target="_blank">去支付</a>
            </td>
        </tr>
        <tr>
            <td>
                <a href="/quickPay/demo/queryStat"  target="_blank">查询支付状态</a>
            </td>
        </tr>
        <tr>
            <td>
                <a href="/quickPay/payFrozen" target="_blank">去支付冻结</a>
                <a href="/order/demo/thaw"  target="_blank">订单解冻</a>
            </td>
        </tr>
        <tr>
            <td>
                <a href="/quickPay/payQrcode" target="_blank">微信扫描支付</a>
                <a href="/quickPay/payQrcode?pay_type=05"  target="_blank">支付宝扫描支付</a>
            </td>
        </tr>
        <tr>
            <td>
                <a href="/refund/refund" target="_blank">退款</a>
                <a href="/refund/demo/queryStat"  target="_blank">查询退款状态</a>
            </td>
        </tr>
        <tr>
            <td>
                <a href="/order/transfer" target="_blank">转账</a>
            </td>
        </tr>
        <tr>
            <td>
                <a href="/account/open"  target="_blank">个人开户</a>
                <a href="/account/openCompany"  target="_blank">企业开户</a>
                <a href="/account/demo/queryStat"  target="_blank">查询企业开户状态</a>
            </td>
        </tr>
        <tr>
            <td>
                <a href="/transQuery/balance?inCustId=6666000000054655&inAcctId=64782"  target="_blank">查询中间帐号余额</a>
                <a href="/transQuery/balance"  target="_blank">查询测试帐号余额</a>
            </td>
        </tr>
    </table>
</div>

</body>


</html>
