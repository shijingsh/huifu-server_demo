package com.huifu.example.account;

import com.alibaba.fastjson.JSON;
import com.huifu.npay.master.domain.CfcaInfoBo;
import com.huifu.npay.master.transQuery.TransQueryService;
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
 * 页面方式支付 调用支付过程中有汇付页面
 * 
 * @author yinfeng.yuan
 *
 */
@Controller
@RequestMapping(value = "/account")
public class OpenAccount {

	public static Logger log = LoggerFactory.getLogger(OpenAccount.class);
	public static String orderId = null;
	public static String orderDate = null;
	public static String platform_seq_id = null;
	public static String user_cust_id = null;
	
	// 从商户页面提交
	@RequestMapping(value = "/open")
	@ResponseBody
	public String open(ModelMap map, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) throws Exception {
		orderId = makeOrderId();

		SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
		orderDate = dateformat.format(System.currentTimeMillis());
		//组装开户+请求参数
		Map<String, String> payParams = new HashMap<>();
		payParams.put(Constants.VERSION, Constants.VERSION_VALUE);
		payParams.put(Constants.CMD_ID, "101");
		payParams.put(Constants.MER_CUST_ID, merCustId);
		payParams.put(Constants.ORDER_ID, orderId);
		payParams.put(Constants.ORDER_DATE, orderDate);

		//如果需要完成后跳转指定页面，则需要设置此项
		payParams.put(Constants.BG_RET_URL,bgRetUrl);
		payParams.put(Constants.MER_PRIV, "");
		payParams.put(Constants.EXTENSION, "");
		//user_name
		payParams.put("user_name", "365me测试");
		payParams.put("cert_id", "441201199010245791");
		payParams.put("user_mobile", "15921359797");
		payParams.put("cust_prov", "0031");
		payParams.put("cust_area", "3100");
		payParams.put("solo_flg", "00000100");

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
		log.info("开户请求参数："+paramsStr);
		// 进行加签
		String sign = SecurityService.sign(paramsStr,cfcaInfoBo);

		HttpRequest httpRequest = HttpRequest.post(url).charset("UTF-8");

		// 组织post数据
		String postStr = "cmd_id=" + 101
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
		log.info("开户返回参数："+resultStr);
		return resultStr;
	}


	// 从商户页面提交
	@RequestMapping(value = "/openCompany")
	public String openCompany(ModelMap map, HttpServletRequest request,
					   HttpServletResponse response, HttpSession session) throws Exception {
		orderId = makeOrderId();

		SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
		orderDate = dateformat.format(System.currentTimeMillis());
		//组装开户+请求参数
		Map<String, String> payParams = new HashMap<>();
		payParams.put(Constants.VERSION, Constants.VERSION_VALUE);
		payParams.put(Constants.CMD_ID, "102");
		payParams.put(Constants.MER_CUST_ID, merCustId);
		payParams.put(Constants.ORDER_ID, orderId);
		payParams.put(Constants.ORDER_DATE, orderDate);

		//如果需要完成后跳转指定页面，则需要设置此项
		payParams.put(Constants.BG_RET_URL,bgRetUrl);
		payParams.put(Constants.MER_PRIV, "");
		payParams.put(Constants.EXTENSION, "");
		//user_name
		payParams.put("apply_id", "2000001hh43");
		payParams.put("operate_type", "00090001"); //00090000 新增 00090001 修改
		payParams.put("corp_license_type", "01030100");//01030100.普通营业执照企业 01030101.三证合一企业
		payParams.put("corp_type", "01030001");	//01030000.普通企业 01030001.个体户
		payParams.put("corp_name", "全球黑科");
		//三证
		payParams.put("business_code", "a1225431204");
		payParams.put("institution_code", "088100129");
		payParams.put("tax_code", "a199945675");

		payParams.put("license_start_date", "20160830");
		payParams.put("license_end_date", "20190830");
		payParams.put("corp_business_address", "上海市");
		payParams.put("corp_reg_address", "上海杨北路");
		payParams.put("corp_fixed_telephone", "021-12345678900");
		payParams.put("business_scope", "上海市浦张杨");
		//payParams.put("controlling_shareholder", "[{\"name\":\"20条\",\"certType\":\"01020100\",\"certId\":\"370101197209143545\"}]");
		payParams.put("legal_name", "球");
		payParams.put("legal_cert_type", "01020100");
		payParams.put("legal_cert_id", "140203195101011256");
		payParams.put("legal_cert_start_date", "20150809");
		payParams.put("legal_cert_end_date", "20190809");
		payParams.put("legal_mobile", "15911115334");
		payParams.put("contact_name", "科技球");
		payParams.put("contact_mobile", "15911552132");
		payParams.put("contact_email", "1234901234567890_.jo@qq.ne");
		payParams.put("bank_acct_name", "全球黑科");
		payParams.put("bank_id", "01050000");
		payParams.put("bank_acct_no", "6232510000001001");
		payParams.put("bank_branch", "支付");
		payParams.put("bank_prov", "0031");
		payParams.put("bank_area", "3100");
		payParams.put("industry", "01");

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
		log.info("开户请求参数："+paramsStr);
		// 进行加签
		String sign = SecurityService.sign(paramsStr,cfcaInfoBo);


		map.put("version", Constants.VERSION_VALUE);
		map.put("cmdId", "102");
		map.put("merCustId", merCustId);
		//设置加签后的接口参数
		map.put("sign", sign);
		//设置接口地址
		map.put("url", url);

		return "open";
	}

	private String makeOrderId(){
		return "TEST"+System.currentTimeMillis();
	}

	// 同步轮询终态
	@RequestMapping("/demo/queryStat")
	public String queryStat(ModelMap map,
							HttpServletRequest request, HttpServletResponse response,
							HttpSession session) throws IOException {
		/*{
			"resp_desc": "交易成功",
				"acct_id_list": "[{\"acctBusinessType\":\"00\",\"acctId\":\"67787\",\"acctName\":\"基本支付账户\",\"statFlag\":\"1\"}]",
				"cmd_id": "317",
				"cert_type": "00",
				"cert_id": "140203195101011256",
				"business_code": "a1225431204",
				"user_cust_id": "6666000000056432",
				"institution_code": "088100129",
				"user_name": "全球黑科",
				"contact_mobile": "15911552132",
				"tax_code": "a199945675",
				"stat_flag": "1",
				"resp_code": "317000",
				"social_credit_code": "",
				"role_type": "01",
				"mer_cust_id": "6666000000054655"
		}*/
		// 获取页面数据、加签
		Map<String, String> payParams = new HashMap<String, String>();
		payParams.put(Constants.MER_CUST_ID, merCustId);
		payParams.put(Constants.URL,url);
		payParams.put(Constants.CMD_ID,"317");
		payParams.put("business_code","a1225431204");
		//payParams.put("cert_id","a1225431204");

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
			map.put("stat", "开通成功");
			map.put("skipFlg", false);
		} else if (Constants.TRANS_FAIL.equals(resultMap
				.get(Constants.TRANS_STAT))) {
			map.put("skipFlg", false);
			map.put("stat", "开通失败");
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
