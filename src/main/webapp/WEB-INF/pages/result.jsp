<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<body>
<script src="${pageContext.request.contextPath}/statics/js/queryStat.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/statics/js/jquery/jquery-1.9.0.min.js"></script>
<div class="container">
    <br/>
    <p class="txt">温馨提示：${stat}</p>
    <br/>
    <%-- <textarea class="fund-input" name="result" id="result" style="word-break:break-all;word-wrap: break-word;margin: 0px; height: 90px; width: 710px;">${result}</textarea> --%>
	<br/>
	<p class="txt">${statResult}</p>
	<div style="display: none;">
		<input type="hidden" name="merCustId" value="${merCustId}">
		<input type="hidden" name="orderId" value="${orderId}">
		<input type="hidden" name="orderDate" value="${orderDate}">
	</div>
</div>

</body>

<script type="text/javascript">
	var ctxUrl = "${pageContext.request.contextPath}/quickPay/demo/queryStat";
	var postData = {
		"merCustId" : '${merCustId}',
		"orderId" : '${orderId}',
		"orderDate" : '${orderDate}',
	}
	var skipFlg;
	skipFlg = ${skipFlg};
	if (skipFlg) {
		checkPayStatus(postData);
	} else {
		
	}
	

</script>

</html>
