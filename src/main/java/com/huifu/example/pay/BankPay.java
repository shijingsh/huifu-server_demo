package com.huifu.example.pay;

import com.alibaba.fastjson.JSON;
import com.huifu.npay.master.domain.CfcaInfoBo;
import com.huifu.npay.master.domain.DivDetailBo;
import com.huifu.npay.master.transQuery.TransQueryService;
import com.huifu.npay.master.util.constant.Constants;
import com.huifu.npay.master.util.security.SecurityService;
import com.huifu.saturn.cfca.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 页面方式支付 调用支付过程中有汇付页面
 * 
 * @author yinfeng.yuan
 *
 */
@Controller
@RequestMapping(value = "/bankPay")
public class BankPay {

	public static Logger log = LoggerFactory.getLogger(BankPay.class);
	public static String orderId = null;
	public static String orderDate = null;
	public static String platform_seq_id = null;
	public static String user_cust_id = null;
	
	// 从商户页面提交
	@RequestMapping(value = "/pay")
	public String pay(ModelMap map, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) throws Exception {
		orderId = makeOrderId();

		SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
		orderDate = dateformat.format(System.currentTimeMillis());
		//组装支付+请求参数
		Map<String, String> payParams = new HashMap<String, String>();
		payParams.put(Constants.VERSION, Constants.VERSION_VALUE);
		payParams.put(Constants.CMD_ID, "204");
		payParams.put(Constants.MER_CUST_ID, merCustId);
		payParams.put(Constants.ORDER_ID, orderId);
		payParams.put(Constants.ORDER_DATE, orderDate);
		//payParams.put(Constants.USER_CUST_ID, userCustId); 支付用户的客户id
		payParams.put(Constants.IN_CUST_ID, inCustId);
		payParams.put(Constants.IN_ACCT_ID, inAcctId);
		payParams.put("goods_desc", "商品件曾");
		//设置订单 金额
		if(!StringUtils.isBlank(request.getParameter(Constants.TRANS_AMT))){
			payParams.put(Constants.TRANS_AMT, request.getParameter(Constants.TRANS_AMT));
		}else{
			payParams.put(Constants.TRANS_AMT, "0.01");
		}
		//如果需要支付完成后跳转指定页面，则需要设置此项
		//payParams.put(Constants.RET_URL,"http://172.31.18.80:8089/service-demo/quickPay/demo/ret");
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
		// 商户ID
		cfcaInfoBo.setNpayMerId(hfMerId);
		
		// 转换成json格式
		String paramsStr = JSON.toJSONString(payParams);
		log.info("快捷支付请求参数："+paramsStr);
		// 加签
		String sign = SecurityService.sign(paramsStr,cfcaInfoBo);
		
		map.put("version", Constants.VERSION_VALUE);
		map.put("cmdId", "204");
		map.put("merCustId", merCustId);
		//设置加签后的接口参数
		map.put("sign", sign);
		//设置接口地址
		map.put("url", url);

		return "submit";
	}

	// 从商户页面提交，支付后冻结
	@RequestMapping(value = "/payFrozen")
	public String payFrozen(ModelMap map, HttpServletRequest request,
					  HttpServletResponse response, HttpSession session) throws Exception {
		orderId = makeOrderId();

		SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
		orderDate = dateformat.format(System.currentTimeMillis());
		//组装支付+请求参数
		Map<String, String> payParams = new HashMap<>();
		payParams.put(Constants.VERSION, Constants.VERSION_VALUE);
		payParams.put(Constants.CMD_ID, "204");
		payParams.put(Constants.MER_CUST_ID, merCustId);
		payParams.put(Constants.ORDER_ID, orderId);
		payParams.put(Constants.ORDER_DATE, orderDate);
		//payParams.put(Constants.USER_CUST_ID, userCustId); 支付用户的客户id
		//支付不设置冻结的时候
		//payParams.put(Constants.IN_CUST_ID, inCustId);
		//payParams.put(Constants.IN_ACCT_ID, inAcctId);

		//分账串
		DivDetailBo divBo = new DivDetailBo();
		divBo.setDivCustId(inCustId);//inCustId是原支付入账用户客户号
		divBo.setDivAcctId(inAcctId);//inAcctId是原支付入账账户
		divBo.setDivAmt("0.01");//金额
		divBo.setDivFreezeFg("01"); //divFreezeFg 01：冻结; 00：不冻结
		List<DivDetailBo> divBoList = new ArrayList<>();
		divBoList.add(divBo);
		payParams.put(Constants.DIV_DETAIL, JSON.toJSONString(divBoList));
		//设置订单 金额
		if(!StringUtils.isBlank(request.getParameter(Constants.TRANS_AMT))){
			payParams.put(Constants.TRANS_AMT, request.getParameter(Constants.TRANS_AMT));
		}else{
			payParams.put(Constants.TRANS_AMT, "0.01");
		}
		//如果需要支付完成后跳转指定页面，则需要设置此项
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
		// 商户ID
		cfcaInfoBo.setNpayMerId(hfMerId);

		// 转换成json格式
		String paramsStr = JSON.toJSONString(payParams);
		log.info("快捷支付请求参数："+paramsStr);
		// 加签
		String sign = SecurityService.sign(paramsStr,cfcaInfoBo);

		map.put("version", Constants.VERSION_VALUE);
		map.put("cmdId", "204");
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

	// 同步轮询终态
	@RequestMapping("/demo/queryStat")
	public String queryStat(ModelMap map,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) throws IOException {

		String trans_type = request.getParameter("trans_type");
		if(StringUtils.isBlank(trans_type)){
			trans_type ="10";
		}
		// 获取页面数据、加签
		Map<String, String> payParams = new HashMap<>();
		payParams.put(Constants.MER_CUST_ID, merCustId);
		payParams.put(Constants.ORDER_ID, orderId);
		payParams.put(Constants.ORDER_DATE, orderDate);
		payParams.put(Constants.TRANS_TYPE, trans_type);
		payParams.put(Constants.URL,url);

		// 调用交易状态查询接口
		String statResult = null;
		try {
			CfcaInfoBo cfcaInfoBo = new CfcaInfoBo();
			// 解签证书
			cfcaInfoBo.setCerFile(cerFile);
			// 加签证书
			cfcaInfoBo.setPfxFile(pfxFile);
			// 加签密码
			cfcaInfoBo.setPfxFilePwd(pfxFilePwd);
			statResult = TransQueryService.query(payParams,cfcaInfoBo);
		} catch (Exception e) {
			map.put("stat", "查询状态异常");
		}

		// 获取返回，解签，状态处理中则继续
		Map<String, Object> resultMap = JSON.parseObject(statResult);
		platform_seq_id = (String) resultMap.get("platform_seq_id");
		user_cust_id = (String) resultMap.get("user_cust_id");

		map.put("statResult", statResult);
		// 获得终态，返回页面信息
		if (Constants.TRANS_SUCCESS.equals(resultMap.get(Constants.TRANS_STAT))) {
			map.put("stat", "支付成功");
			map.put("skipFlg", false);
		} else if (Constants.TRANS_FAIL.equals(resultMap
				.get(Constants.TRANS_STAT))) {
			map.put("skipFlg", false);
			map.put("stat", "支付失败");
		}
		return "result";
	}

	private String merCustId;
	private String hfMerId;
	private String userCustId;
	private String inCustId;
	private String inAcctId;
	private String url;
    private String pfxFile;
    private String pfxFilePwd;
    private String cerFile;
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
