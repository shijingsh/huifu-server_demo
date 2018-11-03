package com.huifu.example.pay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.huifu.npay.master.domain.DivDetailBo;
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
	public String pay(ModelMap map, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) throws Exception {
		log.info("************app支付请求**************");

		String orderId = makeOrderId();

		SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
		String orderDate = dateformat.format(System.currentTimeMillis());

		//组装支付+请求参数
		Map<String, String> payParams = new HashMap<>();
		payParams.put(Constants.VERSION, Constants.VERSION_VALUE);
		payParams.put(Constants.CMD_ID, "208");
		payParams.put(Constants.MER_CUST_ID, merCustId);
		payParams.put(Constants.ORDER_ID, orderId);
		payParams.put(Constants.ORDER_DATE, orderDate);
		//payParams.put(Constants.USER_CUST_ID,userCustId);
		//payParams.put(Constants.IN_CUST_ID, inCustId);
		//payParams.put(Constants.IN_ACCT_ID, inAcctId);

		//从selfParamInfo这个中获取支付信息
		if(!StringUtils.isBlank(request.getParameter(Constants.TRANS_AMT))){
			payParams.put(Constants.TRANS_AMT, request.getParameter(Constants.TRANS_AMT));
		}else{
			payParams.put(Constants.TRANS_AMT, "0.01");
		}
		payParams.put(Constants.GOODS_DESC, "测试用");	
		payParams.put(Constants.BG_RET_URL,bgRetUrl);
		payParams.put(Constants.MER_PRIV, "");

		//分账串
		DivDetailBo divBo = new DivDetailBo();
		divBo.setDivCustId(inCustId);//inCustId是原支付入账用户客户号
		divBo.setDivAcctId(inAcctId);//inAcctId是原支付入账账户
		divBo.setDivAmt("0.01");//金额
		divBo.setDivFreezeFg("01"); //divFreezeFg 01：冻结; 00：不冻结
		List<DivDetailBo> divBoList = new ArrayList<>();
		divBoList.add(divBo);
		payParams.put(Constants.DIV_DETAIL, JSON.toJSONString(divBoList));

		//加解签证书参数
		CfcaInfoBo cfcaInfoBo = new CfcaInfoBo();
		// 解签证书
		cfcaInfoBo.setCerFile(cerFile);
		// 加签证书
		cfcaInfoBo.setPfxFile(pfxFile);
		// 加签密码
		cfcaInfoBo.setPfxFilePwd(pfxFilePwd);
		// 商户ID
		cfcaInfoBo.setNpayMerId(hfMerId);
		
		// 转换成json格式
		String paramsStr = JSON.toJSONString(payParams);
		log.info("app支付请求参数："+paramsStr);
		// 加签
		String sign = SecurityService.sign(paramsStr,cfcaInfoBo);

		map.put("version", Constants.VERSION_VALUE);
		map.put("cmdId", "208");
		map.put("merCustId", merCustId);
		//设置加签后的接口参数
		map.put("sign", sign);
		//设置接口地址
		map.put("url", url);

		return "submit";
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
	private String pfxFile;
	private String pfxFilePwd;
	private String cerFile;
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
