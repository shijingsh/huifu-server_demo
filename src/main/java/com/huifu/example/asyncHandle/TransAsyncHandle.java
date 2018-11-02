package com.huifu.example.asyncHandle;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.huifu.npay.master.domain.CfcaInfoBo;
import com.huifu.npay.master.util.security.SecurityService;

/**
 * 后台方式获取支付凭证信息 调用支付过程中没有汇付页面 此接口与支持微信支付、支付宝统一下单支付 支付宝APP支付和applePay支付暂时不支持
 * 
 * @author yinfeng.yuan
 */
@Controller
@RequestMapping(value = "/asyncHandle")
public class TransAsyncHandle {
	public static Logger log = LoggerFactory.getLogger(TransAsyncHandle.class);
	
	// 接收virgo返回
	@RequestMapping("/demo/callBack")
	@ResponseBody
	public String callBack(ModelMap map, HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws IOException {
		request.setCharacterEncoding("utf-8");
		// 取得密文
		String sign = request.getParameter("check_value");		
		//加解签证书参数
		CfcaInfoBo cfcaInfoBo = new CfcaInfoBo();
		// 解签证书
		cfcaInfoBo.setCerFile(System.getProperty("cerFile"));
		// 加签证书
		cfcaInfoBo.setPfxFile(System.getProperty("pfxFile"));
		// 加签密码
		cfcaInfoBo.setPfxFilePwd(System.getProperty("pfxFilePwd"));
		String decrptyContent = SecurityService.parseResult(sign,cfcaInfoBo);
		Map<String,String> mapResult = JSON.parseObject(decrptyContent, Map.class );
		log.info("支付异步返回结果 :" + decrptyContent);
		return "RECV_ORD_ID_" + mapResult.get("order_id");
	}
}
