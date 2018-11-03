package com.huifu.example.transQuery;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.huifu.saturn.cfca.util.StringUtils;
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


/**
 * 后台方式执行退款
 * 调用退款过程中没有汇付页面
 * @author yinfeng.yuan
 */
@Controller
@RequestMapping(value="/transQuery")
public class TransQuery {
	public static Logger log = LoggerFactory.getLogger(TransQuery.class);

	@RequestMapping("/transStat")
	@ResponseBody
	public String query(ModelMap map, HttpServletRequest request, HttpServletResponse response,
            HttpSession session) throws Exception {
		
		String transType = request.getParameter(Constants.TRANS_TYPE);
		String orderId = request.getParameter(Constants.ORDER_ID);
		String orderDate = request.getParameter(Constants.ORDER_DATE);
        // 请求数据map格式
        Map<String, String> payParams = new HashMap<String, String>();
        
        // 根据接口规范，传入请求数据
        payParams.put(Constants.VERSION, Constants.VERSION_VALUE);
		payParams.put(Constants.CMD_ID, Constants.TRANS_QUERY_CMD_ID);
        payParams.put(Constants.MER_CUST_ID,merCustId);
        payParams.put(Constants.ORDER_ID,orderId);
        payParams.put(Constants.ORDER_DATE,orderDate);
        payParams.put(Constants.TRANS_TYPE,transType);
        payParams.put(Constants.MER_PRIV, "");
        payParams.put(Constants.EXTENSION, "");
        
        CfcaInfoBo cfcaInfoBo = new CfcaInfoBo();
        // 解签证书
 		cfcaInfoBo.setCerFile(System.getProperty("cerFile"));
 		// 加签证书
 		cfcaInfoBo.setPfxFile(System.getProperty("pfxFile"));
 		// 加签密码
 		cfcaInfoBo.setPfxFilePwd(System.getProperty("pfxFilePwd"));
 		
 	    // 转换成json格式
		String paramsStr = JSON.toJSONString(payParams);
		
		log.info("状态查询请求参数：" + paramsStr);

		// 进行加签
		String sign = SecurityService.sign(paramsStr,cfcaInfoBo);

		HttpRequest httpRequest = HttpRequest.post(url).charset("UTF-8");

		// 组织post数据
		String postStr = "cmd_id=" + Constants.TRANS_QUERY_CMD_ID 
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


	@RequestMapping("/balance")
	@ResponseBody
	public String balance(ModelMap map, HttpServletRequest request, HttpServletResponse response,
						HttpSession session) throws Exception {


		String inCustId = request.getParameter("inCustId");
		String inAcctId = request.getParameter("inAcctId");
		if(StringUtils.isBlank(inCustId)){
			inCustId = this.inCustId;
		}
		if(StringUtils.isBlank(inAcctId)){
			inAcctId = this.inAcctId;
		}
		// 请求数据map格式
		Map<String, String> payParams = new HashMap<String, String>();

		// 根据接口规范，传入请求数据
		payParams.put(Constants.VERSION, Constants.VERSION_VALUE);
		payParams.put(Constants.CMD_ID, "303");
		payParams.put(Constants.MER_CUST_ID,merCustId);
		payParams.put(Constants.USER_CUST_ID,inCustId);
		payParams.put(Constants.ACCT_ID,inAcctId);


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
		String postStr = "cmd_id=" + 303
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

	@RequestMapping("/bindCard")
	@ResponseBody
	public String bindCard(ModelMap map, HttpServletRequest request, HttpServletResponse response,
						HttpSession session) throws Exception {

		String transType = request.getParameter(Constants.TRANS_TYPE);
		String orderId = request.getParameter(Constants.ORDER_ID);
		String orderDate = request.getParameter(Constants.ORDER_DATE);
		// 请求数据map格式
		Map<String, String> payParams = new HashMap<>();

		// 根据接口规范，传入请求数据
		payParams.put(Constants.VERSION, Constants.VERSION_VALUE);
		payParams.put(Constants.CMD_ID, Constants.TRANS_QUERY_CMD_ID);
		payParams.put(Constants.MER_CUST_ID,merCustId);
		payParams.put(Constants.ORDER_ID,orderId);
		payParams.put(Constants.ORDER_DATE,orderDate);
		payParams.put(Constants.TRANS_TYPE,transType);
		payParams.put(Constants.MER_PRIV, "");
		payParams.put(Constants.EXTENSION, "");

		CfcaInfoBo cfcaInfoBo = new CfcaInfoBo();
		// 解签证书
		cfcaInfoBo.setCerFile(System.getProperty("cerFile"));
		// 加签证书
		cfcaInfoBo.setPfxFile(System.getProperty("pfxFile"));
		// 加签密码
		cfcaInfoBo.setPfxFilePwd(System.getProperty("pfxFilePwd"));

		// 转换成json格式
		String paramsStr = JSON.toJSONString(payParams);

		log.info("状态查询请求参数：" + paramsStr);

		// 进行加签
		String sign = SecurityService.sign(paramsStr,cfcaInfoBo);

		HttpRequest httpRequest = HttpRequest.post(url).charset("UTF-8");

		// 组织post数据
		String postStr = "cmd_id=" + Constants.TRANS_QUERY_CMD_ID
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
	private String merCustId;
	private String url;
	private String userCustId;
	private String inCustId;
	private String inAcctId;
	private String pfxFile;
	private String pfxFilePwd;
	private String cerFile;
    @Value("#{prop.merCustId}") 
	public void setMerCustId(String merCustId) {  
		this.merCustId=merCustId;
	}
	@Value("#{prop.url}") 
	public void setUrl(String url) {
		this.url = url;
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
