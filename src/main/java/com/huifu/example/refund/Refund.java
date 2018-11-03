package com.huifu.example.refund;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.huifu.example.pay.QuickPay;
import com.huifu.npay.master.transQuery.TransQueryService;
import com.huifu.saturn.cfca.util.StringUtils;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.huifu.npay.master.domain.CfcaInfoBo;
import com.huifu.npay.master.domain.DivDetailBo;
import com.huifu.npay.master.util.constant.Constants;
import com.huifu.npay.master.util.security.SecurityService;
import com.huifu.saturn.cfca.CFCASignature;
import com.huifu.saturn.cfca.VerifyResult;

/**
 * 后台方式执行退款 调用退款过程中没有汇付页面
 * 
 * @author yinfeng.yuan
 */
@Controller
@RequestMapping(value = "/refund")
public class Refund {
	public static Logger log = LoggerFactory.getLogger(Refund.class);
	public static String orderId = null;
	public static String orderDate = null;
	@RequestMapping("/refund")
	@ResponseBody
	public String refund(ModelMap map, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) throws Exception {

		String type = request.getParameter("type");
		orderId = makeOrderId();

		SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
		orderDate = dateformat.format(System.currentTimeMillis());

		// 请求数据map格式
		Map<String, String> payParams = new HashMap<>();

		// 根据接口规范，传入请求数据
		payParams.put(Constants.VERSION, Constants.VERSION_VALUE);
		payParams.put(Constants.CMD_ID, Constants.REFUND_CMD_ID);		
		payParams.put(Constants.MER_CUST_ID, merCustId);
		payParams.put(Constants.USER_CUST_ID, QuickPay.user_cust_id);
		payParams.put(Constants.ORDER_ID, orderId);
		payParams.put(Constants.ORDER_DATE, orderDate);
		payParams.put(Constants.TRANS_AMT,"0.01");
		//原交易的交易唯一标识号
		payParams.put(Constants.ORGINAL_PLATFORM_SEQ_ID, QuickPay.platform_seq_id);
		//0 是：1 原交易为快捷支付WEB版或快捷支付APP版时，设为是
		//扫码支付不要传
		if(StringUtils.isBlank(type)){
			payParams.put(Constants.QUICKPAY_PAGE_FLAG,"1");
		}

		//退款分账串
		DivDetailBo divBo = new DivDetailBo();
		divBo.setDivCustId(inCustId);//inCustId是原支付入账用户客户号
		divBo.setDivAcctId(inAcctId);//inAcctId是原支付入账账户
		divBo.setDivAmt("0.01");//退款金额
		List<DivDetailBo> divBoList = new ArrayList<>();
		divBoList.add(divBo);
		payParams.put(Constants.DIV_DETAIL, JSON.toJSONString(divBoList));
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
		String postStr = "cmd_id=" + Constants.REFUND_CMD_ID 
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
		log.info("退款返回参数："+resultStr);
		return resultStr;
	}

	// 同步轮询终态
	@RequestMapping("/demo/queryStat")
	public String queryStat(ModelMap map,
							HttpServletRequest request, HttpServletResponse response,
							HttpSession session) throws IOException {

		// 获取页面数据、加签
		Map<String, String> payParams = new HashMap<String, String>();
		payParams.put(Constants.MER_CUST_ID, merCustId);
		payParams.put(Constants.ORDER_ID, orderId);
		payParams.put(Constants.ORDER_DATE, orderDate);
		payParams.put(Constants.TRANS_TYPE, "04");
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
		//platform_seq_id = (String) resultMap.get("platform_seq_id");
		//user_cust_id = (String) resultMap.get("user_cust_id");
		// 获得终态，返回页面信息
		map.put("statResult", statResult);
		if ("02000009".equals(resultMap.get(Constants.TRANS_STAT))) {
			map.put("stat", "退款成功");
			map.put("skipFlg", false);
		} else if (Constants.TRANS_FAIL.equals(resultMap
				.get(Constants.TRANS_STAT))) {
			map.put("skipFlg", false);
			map.put("stat", "退款失败");
		}
		return "result";
	}

	/**
	 * 生成订单号
	 * @return
	 */
	private String makeOrderId(){
		return "TEST"+System.currentTimeMillis();
	}
	
	// 接收virgo返回
	@RequestMapping("/demo/callBack")
	public String callBack(ModelMap map, HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws IOException {
		request.setCharacterEncoding("utf-8");

		// 取得密文
		String sign = request.getParameter("check_value");

		log.info("check_value :" + sign);

		// 验签cer文件名
		String cerName = "CFCA_TEST_OCA1.cer";
		String decrptyContent = null;

		// 进行验签，固定为100001
		VerifyResult verifyResult = CFCASignature.verifyMerSign("100001", sign,
				"utf-8", "/app/etc/product/" + cerName);
		log.info(verifyResult.toString());

		if ("000".equals(verifyResult.getCode())) {

			// 取得base64格式内容
			String content = new String(verifyResult.getContent(),
					Charset.forName("utf-8"));
			log.info("content = " + content);

			// base64格式解码
			decrptyContent = new String(Base64.decodeBase64(content),
					Charset.forName("utf-8"));
			log.info("decrptyContent = " + decrptyContent);

		} else {
			log.info("验签失败");
		}

		return decrptyContent;
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
