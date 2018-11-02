package com.huifu.example.pay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huifu.saturn.cfca.CFCASignature;
import com.huifu.saturn.cfca.SignResult;
import com.huifu.saturn.cfca.VerifyResult;

import com.huifu.saturn.cfca.util.StringUtils;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;

import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.nio.charset.Charset;


/**
 * Function : 代理商开户测试
 * Date : 2018年9月26日 下午4:45:53
 */
public class CreateMerUserMain {
    // 地址可以询问对方运营
    public static String TEST_FILE_URL = "http://mertest.chinapnr.com/npay/fileMerchantRequest";
    public static String TEST_URL = "http://mertest.chinapnr.com/npay/merchantRequest";
    // 加签用pfx文件
    public static String PFX_FILE_NAME = "D:/key.pfx";
    // 加签用密码
    public static String PFX_FILE_PWD = "1111";
    //商户号
    public static String MER_CUST_ID = "6666000000054655";
    //文件可以询问对方运营
    public static String CER_NAME = "D:/CFCA_ACS_TEST_OCA31.cer";
    public static String VERSION = "10";
    public static String CHARSET = "UTF-8";

    public static void main(String[] args) {
        String attachNos = "c1454422338,c1454422339";
        //上传附件
        uploadFile(attachNos);
        //代理商开户---同类型附件最多只能有一个
        createAgentUser(attachNos);
    }

    /**
     * 商户附件上传
     *
     * @param attachNos
     */
    public static void uploadFile(String attachNos) {
        if (!StringUtils.isBlank(attachNos)) {
            String[] attachNoList = attachNos.split(",");
            for (String attachNo : attachNoList) {
                //循环上传附件
                uploadFileMain(attachNo);
                try {
                    //测试环境，休眠一会再上传；正式环境不需要
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void uploadFileMain(String attachNo) {
        String valueObj = "{\"version\":\"10\",\"cmd_id\":\"122\",\"mer_cust_id\":\"" + MER_CUST_ID + "\",\"attach_no\":\"" + attachNo + "\",\"trans_type\":\"01\",\"attach_type\":\"05\",\"extension\":\"1\"}";
        // 进行base64转换
        String base64RequestParams = Base64.encodeBase64String(valueObj.getBytes(Charset.forName("utf-8")));
        // 加签
        SignResult signResult = CFCASignature.signature(PFX_FILE_NAME, PFX_FILE_PWD, base64RequestParams,
                "utf-8");
        if (!"000".equals(signResult.getCode())) {
            System.out.println("加签错误");
            return;
        }
        String checkValue = signResult.getSign();
        String cmdId = "122";

        //如果文件名称中有中文，请先使用URLEncoder.encode(filename, "UTF-8")对文件名编码
        File newFile = new File("D:\\0.png");

        HttpRequest httpRequest = HttpRequest.post(TEST_FILE_URL).charset(CHARSET);
        HttpResponse httpResponse = httpRequest.contentType("multipart/form-data").form(
                "cmd_id", cmdId,
                "version", VERSION,
                "mer_cust_id", MER_CUST_ID,
                "check_value", checkValue,
                "attach_file", newFile).send();
        String body = httpResponse.bodyText();
        // 响应解密 验签失败
        JSONObject jsonObject = JSON.parseObject(body);
        String sign = jsonObject.getString("check_value");
        VerifyResult verifyResult = CFCASignature.verifyMerSign("100001", sign, CHARSET,
                 CER_NAME);
        if (!"000".equals(verifyResult.getCode())) {
            System.out.println("验签失败");
            return;
        }
        String content = new String(verifyResult.getContent(), Charset.forName(CHARSET));
        String decrptyContent = new String(Base64.decodeBase64(content), Charset.forName(CHARSET));
        System.out.println(decrptyContent);
    }

    /**
     * 代理商开户申请
     *
     * @param attachNos
     */
    public static void createAgentUser(String attachNos) {
        String socialCreditCode = "344452111561313162";
        String valueObj = "{\"version\":\"10\",\"cmd_id\":\"120\",\"mer_cust_id\":\"6666000000026086\",\"mer_name\":\"代理商商户测试\",\"mer_short_name\":\"商户简称\",\"mer_en_name\":\"我爱你中国\",\"reg_fund\":\"10000000000000\",\"paid_in_fund\":\"90000000000000\",\"mer_website\":\"www.baidu.com\",\"per_icp_code\":\"dhfdshfhsdfsd\",\"est_date\":\"20160512\",\"mer_prov\":\"0011\",\"mer_area\":\"1100\",\"mer_addr\":\"无法adfdf\",\"mer_reg_addr\":\"普通商户注册地\",\"license_type\":\"01030101\",\"business_code\":\"\",\"institution_code\":\"\",\"tax_code\":\"\",\"social_credit_code\":\""+socialCreditCode+"\",\"license_start_date\":\"20120612\",\"license_end_date\":\"20190612\",\"business_scope\":\"fdksfhsdkfggfg\",\"stockholders\":\"fdksfhsdkfggfg\",\"legal_name\":\"张大庄\",\"legal_cert_type\":\"01020100\",\"legal_cert_id\":\"020103197101062287\",\"legal_id_start_date\":\"20111220\",\"legal_id_end_date\":\"21111220\",\"legal_mobile\":\"15811111111\",\"contact_name\":\"测试sh\",\"contact_mobile\":\"15911111111\",\"contact_email\":\"15911111111@163.com\",\"bank_acct_no\":\"6217001257387383\",\"bank_branch\":\"gfdgfdg\",\"bank_prov\":\"0012\",\"bank_area\":\"1200\",\"addr_fileid\":\"\",\"gate_fileid\":\"\",\"manage_fileid\":\"\",\"social_credit_code_fileid\":\"\",\"business_license_fileid\":\"\",\"org_code_fileid\":\"\",\"tax_reg_fileid\":\"\",\"other_data_fileid\":\"\",\"legal_license_fileid1\":\"\",\"legal_license_fileid2\":\"\",\"bank_acct_open_lice_fileid\":\"\",\"elec_protocol_fileid\":\"\",\"bg_ret_url\":\"http://192.168.0.74:8001/npayCallBack/asyncHandle.json\",\"mer_priv\":\"\",\"extension\":\"\",\"attach_nos\":\""+attachNos+"\"}";
        // 进行base64转换
        String base64RequestParams = Base64.encodeBase64String(valueObj.getBytes(Charset.forName("utf-8")));
        // 加签
        SignResult signResult = CFCASignature.signature(PFX_FILE_NAME, PFX_FILE_PWD, base64RequestParams, "utf-8");
        if (!"000".equals(signResult.getCode())) {
            System.out.println("加签错误");
            return;
        }

        String checkValue = signResult.getSign();
        String cmdId = "120";
        String contentType = "application/x-www-form-urlencoded";
        String postStr = "cmd_id=" + cmdId + "&version=" + VERSION + "&mer_cust_id=" + MER_CUST_ID + "&check_value=" + checkValue;

        HttpRequest httpRequest = HttpRequest.post(TEST_URL).charset(CHARSET);
        HttpResponse httpResponse = httpRequest.contentType(contentType).body(postStr).send();
        String body = httpResponse.bodyText();
        // 响应解密 验签失败
        JSONObject jsonObject = JSON.parseObject(body);
        String sign = jsonObject.getString("check_value");
        VerifyResult verifyResult = CFCASignature.verifyMerSign("100001", sign, CHARSET,
                CER_NAME);
        if (!"000".equals(verifyResult.getCode())) {
            System.out.println("验签失败");
            return;
        }
        String content = new String(verifyResult.getContent(), Charset.forName(CHARSET));
        String decrptyContent = new String(Base64.decodeBase64(content), Charset.forName(CHARSET));
        System.out.println(decrptyContent);
    }
}