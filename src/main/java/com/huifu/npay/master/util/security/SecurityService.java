/**
 * 
 */
package com.huifu.npay.master.util.security;

import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huifu.npay.master.domain.CfcaInfoBo;
import com.huifu.saturn.cfca.CFCASignature;
import com.huifu.saturn.cfca.SignResult;
import com.huifu.saturn.cfca.VerifyResult;

/**
 * 
 * @author jili.hua_c
 *
 */

public class SecurityService {
    private final static Logger logger = LoggerFactory.getLogger(SecurityService.class);
	/**
	 * npay 加签
	 * 
	 * @param params
	 * @return
	 */
	public static String sign(String params,CfcaInfoBo cfcaInfoBo) {
		logger.info("加解签参数："+cfcaInfoBo.toString());
		logger.info("待加签内容："+params);
		// 进行base64转换
		String base64RequestParams = Base64.encodeBase64String(params.getBytes(Charset.forName("utf-8")));

		// 加签
		SignResult signResult = CFCASignature.signature(cfcaInfoBo.getPfxFile(), cfcaInfoBo.getPfxFilePwd(), base64RequestParams, "utf-8");

		if ("000".equals(signResult.getCode())) {
			logger.info("加签结果："+signResult.getSign());		
			return signResult.getSign();
		} else {
			return "加签失败";
		}
	}

	/**
	 * npay 验签
	 * 
	 * @param responseJson
	 * @return
	 */
	public static String parseCVResult(String responseJson,CfcaInfoBo cfcaInfoBo) {
		logger.info("加解签参数："+cfcaInfoBo.toString());
		logger.info("待解签内容："+responseJson);
		// 将json格式字符串转换为json对象
		JSONObject jsonObject = JSON.parseObject(responseJson);
		// 取得返回数据密文
		String sign = jsonObject.getString("check_value");

		// 进行验签，参数1为汇付商户号，固定为100001
		VerifyResult verifyResult = CFCASignature.verifyMerSign(cfcaInfoBo.getNpayMerId(), sign,
				"utf-8",cfcaInfoBo.getCerFile());
		if ("000".equals(verifyResult.getCode())) {

			// 取得base64格式内容
			String content = new String(verifyResult.getContent(),
					Charset.forName("utf-8"));
			// base64格式解码
			String decrptyContent = new String(Base64.decodeBase64(content),
					Charset.forName("utf-8"));
			
			logger.info("解签结果："+responseJson);
			return decrptyContent;
		} else {
			logger.info("解签失败");	
			return "验签失败";
		}
	}
	
	/**
	 * npay 验签
	 * 
	 * @param responseJson
	 * @return
	 */
	public static String parseResult(String sign,CfcaInfoBo cfcaInfoBo) {
		// 进行验签，参数1为汇付商户号，固定为100001
		VerifyResult verifyResult = CFCASignature.verifyMerSign(cfcaInfoBo.getNpayMerId(), sign,
				"utf-8",cfcaInfoBo.getCerFile());

		System.out.println(verifyResult.toString());
		System.out.println("verifyResult" + verifyResult.toString());

		if ("000".equals(verifyResult.getCode())) {

			// 取得base64格式内容
			String content = new String(verifyResult.getContent(),
					Charset.forName("utf-8"));
			System.out.println("content = " + content);

			// base64格式解码
			String decrptyContent = new String(Base64.decodeBase64(content),
					Charset.forName("utf-8"));
			System.out.println("decrptyContent = " + decrptyContent);

			return decrptyContent;
		} else {
			return "验签失败";
		}
	}

}
