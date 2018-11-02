package com.huifu.npay.master.transQuery;

import java.util.Map;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.huifu.npay.master.domain.CfcaInfoBo;
import com.huifu.npay.master.util.constant.Constants;
import com.huifu.npay.master.util.security.SecurityService;
import com.huifu.saturn.cfca.util.StringUtils;

/**
 * 后台方式执行退款 调用退款过程中没有汇付页面
 * 
 * @author yinfeng.yuan
 */
public class TransQueryService {
    private final static Logger logger = LoggerFactory.getLogger(TransQueryService.class);

	public static String query(Map<String, String> params,CfcaInfoBo cfcaInfoBo) throws Exception {
	    logger.info("状态查询请求开始："+params.toString());
		// 生产访问地址
		String url = Constants.HTTP_ENVIRNMENT_URL;
		if (!StringUtils.isBlank(params.get(Constants.URL))) {
			url = params.get(Constants.URL);
		}

		// version
		String cmdId = Constants.TRANS_QUERY_CMD_ID;
		if (!StringUtils.isBlank(params.get(Constants.CMD_ID))) {
			cmdId = params.get(Constants.CMD_ID);
		}else{
			params.put(Constants.CMD_ID,cmdId);
		}

		// cmd_id
		String version = Constants.VERSION_VALUE;
		if (!StringUtils.isBlank(params.get(Constants.VERSION))) {
			version = params.get(Constants.VERSION);
		}else{
			params.put(Constants.VERSION, version);
		}

		// 请求数据json串
		String paramsStr = "";
		// 签名密文
		String sign = null;

		HttpRequest httpRequest;
		HttpResponse httpResponse = null;
		String resultStr = null;
		try{
			// 去除多余字段
			params.remove(Constants.URL);
			// 转换成json格式
			paramsStr = JSON.toJSONString(params);
	
			// 进行加签
			sign = SecurityService.sign(paramsStr,cfcaInfoBo);
	
			httpRequest = HttpRequest.post(url).charset("UTF-8");
	
			// 组织post数据
			String postStr = "cmd_id=" + cmdId + "&version=" + version
					+ "&mer_cust_id=" + params.get(Constants.MER_CUST_ID) + "&check_value="
					+ sign;
	
			// 发送请求给汇付
			httpResponse = httpRequest
					.contentType("application/x-www-form-urlencoded").body(postStr)
					.send();
			logger.info("状态查询请求状态："+httpResponse.statusCode());
			// 取得同步返回数据
			String body = httpResponse.bodyText();
			// 进行验签
			resultStr =  SecurityService.parseCVResult(body,cfcaInfoBo);
		}catch(Exception e){
			logger.error("状态查询请求失败",e);
		}
	    logger.info("状态查询请求结束："+resultStr);
		return resultStr;
		
	}

	public static String thaw(Map<String, String> params,CfcaInfoBo cfcaInfoBo) throws Exception {
		logger.info("状态查询请求开始："+params.toString());
		// 生产访问地址
		String url = Constants.HTTP_ENVIRNMENT_URL;
		if (!StringUtils.isBlank(params.get(Constants.URL))) {
			url = params.get(Constants.URL);
		}

		// version
		String cmdId = Constants.TRANS_QUERY_CMD_ID;
		if (!StringUtils.isBlank(params.get(Constants.CMD_ID))) {
			cmdId = params.get(Constants.CMD_ID);
		}else{
			params.put(Constants.CMD_ID,cmdId);
		}

		// cmd_id
		String version = Constants.VERSION_VALUE;
		if (!StringUtils.isBlank(params.get(Constants.VERSION))) {
			version = params.get(Constants.VERSION);
		}else{
			params.put(Constants.VERSION, version);
		}

		// 请求数据json串
		String paramsStr = "";
		// 签名密文
		String sign = null;

		HttpRequest httpRequest;
		HttpResponse httpResponse = null;
		String resultStr = null;
		try{
			// 去除多余字段
			params.remove(Constants.URL);
			// 转换成json格式
			paramsStr = JSON.toJSONString(params);

			// 进行加签
			sign = SecurityService.sign(paramsStr,cfcaInfoBo);

			httpRequest = HttpRequest.post(url).charset("UTF-8");

			// 组织post数据
			String postStr = "cmd_id=" + cmdId + "&version=" + version
					+ "&mer_cust_id=" + params.get(Constants.MER_CUST_ID) + "&check_value="
					+ sign;

			// 发送请求给汇付
			httpResponse = httpRequest
					.contentType("application/x-www-form-urlencoded").body(postStr)
					.send();
			logger.info("状态查询请求状态："+httpResponse.statusCode());
			// 取得同步返回数据
			String body = httpResponse.bodyText();
			// 进行验签
			resultStr =  SecurityService.parseCVResult(body,cfcaInfoBo);
		}catch(Exception e){
			logger.error("状态查询请求失败",e);
		}
		logger.info("状态查询请求结束："+resultStr);
		return resultStr;

	}
}
