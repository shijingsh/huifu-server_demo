package com.huifu.example.pay;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.huifu.npay.master.domain.CfcaInfoBo;
import com.huifu.npay.master.util.constant.Constants;
import com.huifu.npay.master.util.security.SecurityService;
import com.huifu.saturn.cfca.util.StringUtils;

/**
 * 后台方式获取支付凭证信息 调用支付过程中没有汇付页面 此接口与支持微信支付、支付宝统一下单支付 支付宝APP支付和applePay支付暂时不支持
 * 
 * @author yinfeng.yuan
 */
@Controller
@RequestMapping(value = "/appPay")
public class AppPay {
	public static Logger log = LoggerFactory.getLogger(AppPay.class);
	
	@RequestMapping("pay")
	@ResponseBody
	public String pay(ModelMap map, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) throws Exception {
		log.info("************app支付请求**************");
		
		// 支付请求数据
		String payType = request.getParameter(Constants.PAY_TYPE);
				
		//组装支付+请求参数
		Map<String, String> payParams = new HashMap<String, String>();
		payParams.put(Constants.VERSION, Constants.VERSION_VALUE);
		payParams.put(Constants.CMD_ID, Constants.APP_CMD_ID);		
		payParams.put(Constants.MER_CUST_ID, merCustId);
		payParams.put(Constants.ORDER_ID, makeOrderId());
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
		String dateStr = dateformat.format(System.currentTimeMillis());
		payParams.put(Constants.ORDER_DATE, dateStr);
		payParams.put(Constants.USER_CUST_ID,userCustId);
		payParams.put(Constants.IN_CUST_ID, inCustId);
		payParams.put(Constants.IN_ACCT_ID, inAcctId);
		payParams.put(Constants.PAY_TYPE, payType);
		if(Constants.WX_APP_PAY.equals(payType)){
			payParams.put(Constants.APP_ID, appId);
		}
		//从selfParamInfo这个中获取支付信息
		if(!StringUtils.isBlank(request.getParameter(Constants.TRANS_AMT))){
			payParams.put(Constants.TRANS_AMT, request.getParameter(Constants.TRANS_AMT));
		}else{
			payParams.put(Constants.TRANS_AMT, "0.01");
		}
		payParams.put(Constants.GOODS_DESC, "测试用");	
		payParams.put(Constants.BG_RET_URL,bgRetUrl);
		payParams.put(Constants.MER_PRIV, "");
		//加解签证书参数
		CfcaInfoBo cfcaInfoBo = new CfcaInfoBo();
		// 解签证书
		// 解签证书
		cfcaInfoBo.setCerFile(System.getProperty("cerFile"));
		// 加签证书
		cfcaInfoBo.setPfxFile(System.getProperty("pfxFile"));
		// 加签密码
		cfcaInfoBo.setPfxFilePwd(System.getProperty("pfxFilePwd"));
		//商户ID
		cfcaInfoBo.setNpayMerId(hfMerId);
		
		// 转换成json格式
		String paramsStr = JSON.toJSONString(payParams);
		log.info("app支付请求参数："+paramsStr);
		// 加签
		String sign = SecurityService.sign(paramsStr,cfcaInfoBo);
		HttpRequest httpRequest = HttpRequest.post(url).charset("UTF-8");
		// 组织post数据
		String postStr = "cmd_id=" + Constants.APP_CMD_ID 
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
		String resultStr = SecurityService.parseCVResult(body,cfcaInfoBo);
		log.info("app支付返回参数："+resultStr);
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
	private String hfMerId;
	private String userCustId;
	private String inCustId;
	private String inAcctId;
	private String appId;
	private String url;
    private String bgRetUrl;
	@Value("#{prop.merCustId}") 
	public void setMerCustId(String merCustId) {  
		this.merCustId=merCustId;
	}
	@Value("#{prop.hfMerId}") 
	public void setHfMerId(String hfMerId) {  
		this.hfMerId=hfMerId;
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
	@Value("#{prop.appId}") 
	public void setAppId(String appId) {
		this.appId = appId;
	}
	@Value("#{prop.url}") 
	public void setUrl(String url) {
		this.url = url;
	}

	@Value("#{prop.bgRetUrl}") 
	public void setBgRetUrl(String bgRetUrl) {
		this.bgRetUrl = bgRetUrl;
	}
}
