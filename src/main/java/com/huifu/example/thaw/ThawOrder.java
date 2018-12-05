package com.huifu.example.thaw;

import com.alibaba.fastjson.JSON;
import com.huifu.example.pay.QuickPay;
import com.huifu.npay.master.domain.CfcaInfoBo;
import com.huifu.npay.master.domain.DivDetailBo;
import com.huifu.npay.master.util.MemoryCache;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台方式执行退款 调用退款过程中没有汇付页面
 * 
 * @author yinfeng.yuan
 */
@Controller
@RequestMapping(value = "/order")
public class ThawOrder {
	public static Logger log = LoggerFactory.getLogger(ThawOrder.class);

	/**
	 * 订单解冻
	 * @param map
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/demo/thaw")
	@ResponseBody
	public String thaw(ModelMap map,
							HttpServletRequest request, HttpServletResponse response,
							HttpSession session) throws IOException {
		String orderId = makeOrderId();

		SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
		String orderDate = dateformat.format(System.currentTimeMillis());
		// 请求数据map格式
		Map<String, String> payParams = new HashMap<>();

		// 根据接口规范，传入请求数据
		payParams.put(Constants.VERSION, Constants.VERSION_VALUE);
		payParams.put(Constants.CMD_ID, "212");
		payParams.put(Constants.MER_CUST_ID, merCustId);
		payParams.put(Constants.USER_CUST_ID, MemoryCache.user_cust_id);  	// 这个是什么值？
		payParams.put(Constants.ORDER_ID, orderId);
		payParams.put(Constants.ORDER_DATE, orderDate);
		//payParams.put(Constants.TRANS_AMT,"0.01");
		//原交易的交易唯一标识号
		payParams.put(Constants.ORGINAL_PLATFORM_SEQ_ID, MemoryCache.platform_seq_id);
		payParams.put(Constants.QUICKPAY_PAGE_FLAG,"1");
		//退款分账串
		DivDetailBo divBo = new DivDetailBo();
		divBo.setDivCustId(inCustId);//inCustId是原支付入账用户客户号
		divBo.setDivAcctId(inAcctId);//inAcctId是原支付入账账户
		divBo.setDivAmt("0.01");//金额
		divBo.setDivFreezeFg("01"); //01：冻结; 00：不冻结

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
		String postStr = "cmd_id=" + 212
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
