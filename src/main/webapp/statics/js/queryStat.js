/** 实时查询交易状态 */
var checkPayStatus = function(postData) {
	// 状态check持续半小时超时
	expireTime = 1800;
	
	// 交易完成判定，用以结束定时器
	var finalStatus = false;
	
	function ajaxReq() {
		$.ajax({
			url : ctxUrl,
			type : 'post',
			data : postData
		}).done(
				function(resp) {
					if (!resp || !resp.result || "success" == resp.result
							|| "fail" == resp.result) {
						finalStatus = true;
					} else if ("process" == resp.result) {
						expireTime -= 2;
					} else {
						finalStatus = true;
					}
				});
	}

	var timer = setInterval(function() {
		if (finalStatus || expireTime <= 0) {
			console.log('timer stopped');
			clearInterval(timer);
			if (expireTime<= 0) {
				var now = new Date();
				var format = now.getFullYear() + '/' + now.getMonth() + '/' + now.getDate() + ' ' + 
				+ now.getHours() + ':' + now.getMinutes() + ':' +  now.getSeconds() + '.' +  now.getMilliseconds();
				console.log('支付请求超时：' + format);
			}
		} else {
			ajaxReq();
		}

	}, 2000);

}

