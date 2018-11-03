package com.huifu.example.thaw;

import com.alibaba.fastjson.JSON;
import com.huifu.npay.master.domain.CfcaInfoBo;
import com.huifu.npay.master.util.constant.Constants;
import com.huifu.npay.master.util.security.SecurityService;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 取现接口
 */
@Controller
@RequestMapping(value = "/cash")
public class TakeCash {
	public static Logger log = LoggerFactory.getLogger(TakeCash.class);

	public static String orderId = null;
	public static String orderDate = null;
	/**
	 * 绑定取现卡
	 * @param map
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/bindCard")
	@ResponseBody
	public String bindCard(ModelMap map,
					   HttpServletRequest request, HttpServletResponse response,
					   HttpSession session) throws IOException {
		orderId = makeOrderId();

		SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
		orderDate = dateformat.format(System.currentTimeMillis());
		// 请求数据map格式
		Map<String, String> payParams = new HashMap<>();

		/**
		 * 个人绑卡
		 * "user_cust_id", 6666000000056254
		 * "card_no", "6232510000009003"
		 * "card_mobile", "15921359797"
		 *
		 */
		/**
		 * 企业绑卡
		 * "user_cust_id", 6666000000056432
		 * "card_no", "6232510000009004"
		 * "card_mobile", "15921359798"
		 *
		 */
		// 根据接口规范，传入请求数据
		payParams.put(Constants.VERSION, Constants.VERSION_VALUE);
		payParams.put(Constants.CMD_ID, "104");
		payParams.put(Constants.MER_CUST_ID, merCustId);
		payParams.put(Constants.USER_CUST_ID, "6666000000056432");  	// 这个是什么值？
		payParams.put(Constants.ORDER_ID, orderId);
		payParams.put(Constants.ORDER_DATE, orderDate);

		payParams.put("bank_id", "01050000");
		payParams.put("dc_flag", "0");
		payParams.put("card_no", "6232510000009004");
		payParams.put("card_mobile", "15921359798");
		payParams.put("card_prov", "0031");
		payParams.put("card_area", "3100");

		payParams.put(Constants.BG_RET_URL,bgRetUrl);
		payParams.put(Constants.MER_PRIV, "");
		payParams.put(Constants.EXTENSION, "");
		CfcaInfoBo cfcaInfoBo = new CfcaInfoBo();
		// 解签证书
		cfcaInfoBo.setCerFile(cerFile);
		// 加签证书
		cfcaInfoBo.setPfxFile(pfxFile);
		// 加签密码
		cfcaInfoBo.setPfxFilePwd(pfxFilePwd);

		// 转换成json格式
		String paramsStr = JSON.toJSONString(payParams);
		log.info("退款请求参数："+paramsStr);
		// 进行加签
		String sign = SecurityService.sign(paramsStr,cfcaInfoBo);

		HttpRequest httpRequest = HttpRequest.post(url).charset("UTF-8");

		// 组织post数据
		String postStr = "cmd_id=" + 104
				+ "&version=" + Constants.VERSION_VALUE
				+ "&mer_cust_id=" + merCustId
				+ "&check_value=" + sign;

		// 发送请求给汇付
		HttpResponse httpResponse = httpRequest
				.contentType("application/x-www-form-urlencoded").body(postStr)
				.send();
		// 取得同步返回数据
		String body = httpResponse.bodyText();
		String resultStr = SecurityService.parseCVResult(body,cfcaInfoBo);
		log.info("转账返回参数："+resultStr);
		return resultStr;

	}

	@RequestMapping("/bindCardQuery")
	@ResponseBody
	public String bindCardQuery(ModelMap map, HttpServletRequest request, HttpServletResponse response,
						   HttpSession session) throws Exception {

		String transType = "08 ";
		// 请求数据map格式
		Map<String, String> payParams = new HashMap<>();

		//绑卡需要318接口才能返回 bind_id,310没有这个字段
		//318需要开通接口权限
		// 根据接口规范，传入请求数据
		payParams.put(Constants.VERSION, Constants.VERSION_VALUE);
		payParams.put(Constants.CMD_ID, "318");
		payParams.put(Constants.MER_CUST_ID,merCustId);

		/**
		 * 6666000000056254 个人账户
		 * 6666000000056432 企业账户
		 */
		payParams.put(Constants.USER_CUST_ID, "6666000000056432");
		payParams.put("bind_card_id", "");

		CfcaInfoBo cfcaInfoBo = new CfcaInfoBo();
		// 解签证书
		cfcaInfoBo.setCerFile(cerFile);
		// 加签证书
		cfcaInfoBo.setPfxFile(pfxFile);
		// 加签密码
		cfcaInfoBo.setPfxFilePwd(pfxFilePwd);

		// 转换成json格式
		String paramsStr = JSON.toJSONString(payParams);

		log.info("状态查询请求参数：" + paramsStr);

		// 进行加签
		String sign = SecurityService.sign(paramsStr,cfcaInfoBo);

		HttpRequest httpRequest = HttpRequest.post(url).charset("UTF-8");

		// 组织post数据
		String postStr = "cmd_id=" + "318"
				+ "&version=" + Constants.VERSION_VALUE
				+ "&mer_cust_id=" + merCustId
				+ "&check_value=" + sign;

		// 发送请求给汇付
		HttpResponse httpResponse = httpRequest
				.contentType("application/x-www-form-urlencoded").body(postStr)
				.send();
		// 取得同步返回数据
		String body = httpResponse.bodyText();
		// 进行验签
		String resultStr =  SecurityService.parseCVResult(body,cfcaInfoBo);
		log.info("状态查询返回参数：" + resultStr);
		return resultStr;
	}
	/**
	 * 取现
	 * @param map
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/take")
	@ResponseBody
	public String take(ModelMap map,
							HttpServletRequest request, HttpServletResponse response,
							HttpSession session) throws IOException {
		String orderId = makeOrderId();

		SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
		String orderDate = dateformat.format(System.currentTimeMillis());
		// 请求数据map格式
		Map<String, String> payParams = new HashMap<>();
/*
		{
			"resp_desc": "交易成功",
				"cmd_id": "318",
				"resp_code": "318000",
				"card_list": "[{\"awFlag\":\"00\",\"bindCardId\":\"\",\"cardMobile\":\"\",\"cardNo\":\"6232510000009003\",\"cardType\":\"C\",\"cashBindCardId\":\"77217\",\"cashFlag\":\"01\",\"dcFlag\":\"0\",\"qpFlag\":\"00\"}]",
				"user_cust_id": "6666000000056254",
				"mer_cust_id": "6666000000054655"
		}*/
		/**
		 * 6666000000056254 个人账户
		 * 6666000000056432 企业账户
		 */
		// 根据接口规范，传入请求数据
		payParams.put(Constants.VERSION, Constants.VERSION_VALUE);
		payParams.put(Constants.CMD_ID, "202");
		payParams.put(Constants.MER_CUST_ID, merCustId);
		payParams.put(Constants.USER_CUST_ID, "6666000000056432");
		payParams.put(Constants.ORDER_ID, orderId);
		payParams.put(Constants.ORDER_DATE, orderDate);
		payParams.put("trans_amt","0.01");

		/**
		 * 77217 个人
		 * 77219 企业
		 */
		payParams.put("cash_bind_card_id", "77219");
		payParams.put("cash_type", "02030000");//02030000：T+0取现；02030010：T+1取现

		payParams.put(Constants.BG_RET_URL,bgRetUrl);
		payParams.put(Constants.MER_PRIV, "");
		payParams.put(Constants.EXTENSION, "");
		CfcaInfoBo cfcaInfoBo = new CfcaInfoBo();
		// 解签证书
		cfcaInfoBo.setCerFile(cerFile);
		// 加签证书
		cfcaInfoBo.setPfxFile(pfxFile);
		// 加签密码
		cfcaInfoBo.setPfxFilePwd(pfxFilePwd);

		// 转换成json格式
		String paramsStr = JSON.toJSONString(payParams);
		log.info("退款请求参数："+paramsStr);
		// 进行加签
		String sign = SecurityService.sign(paramsStr,cfcaInfoBo);

		HttpRequest httpRequest = HttpRequest.post(url).charset("UTF-8");

		// 组织post数据
		String postStr = "cmd_id=" + 202
				+ "&version=" + Constants.VERSION_VALUE
				+ "&mer_cust_id=" + merCustId
				+ "&check_value=" + sign;

		// 发送请求给汇付
		HttpResponse httpResponse = httpRequest
				.contentType("application/x-www-form-urlencoded").body(postStr)
				.send();
		// 取得同步返回数据
		String body = httpResponse.bodyText();
		String resultStr = SecurityService.parseCVResult(body,cfcaInfoBo);
		log.info("转账返回参数："+resultStr);
		return resultStr;

	}
	/**
	 * 生成订单号
	 * @return
	 */
	private String makeOrderId(){
		return "TEST"+System.currentTimeMillis();
	}

	private String merCustId;
	private String userCustId;
	private String inCustId;
	private String inAcctId;
	private String url;
    
    private String bgRetUrl;

	private String pfxFile;
	private String pfxFilePwd;
	private String cerFile;

    @Value("#{prop.merCustId}") 
	public void setMerCustId(String merCustId) {  
		this.merCustId=merCustId;
	}
	@Value("#{prop.userCustId}") 
	public void setUserCustId(String userCustId) {
		this.userCustId = userCustId;
	}
	@Value("#{prop.inCustId}") 
	public void setInCustId(String inCustId) {
		this.inCustId = inCustId;
	}
	@Value("#{prop.inAcctId}") 
	public void setInAcctId(String inAcctId) {
		this.inAcctId = inAcctId;
	}
	
	@Value("#{prop.url}") 
	public void setUrl(String url) {
		this.url = url;
	}
	@Value("#{prop.bgRetUrl}") 
	public void setBgRetUrl(String bgRetUrl) {
		this.bgRetUrl = bgRetUrl;
	}

	@Value("#{prop.pfxFile}")
	public void setPfxFile(String pfxFile) {
		this.pfxFile = pfxFile;
	}

	@Value("#{prop.pfxFilePwd}")
	public void setPfxFilePwd(String pfxFilePwd) {
		this.pfxFilePwd = pfxFilePwd;
	}

	@Value("#{prop.cerFile}")
	public void setCerFile(String cerFile) {
		this.cerFile = cerFile;
	}
}
