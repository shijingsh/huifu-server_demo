package com.huifu.npay.master.util.constant;

/**
 * 
 * Function : 接口参数常量
 * Date : 2017年8月21日 上午11:37:16
 * 
 * @author yinfeng.yuan 
 * @version v1.0.0 
 * @since JDK 1.7
 */
public class Constants {
	//支付+服务地址
	public static final String HTTP_ENVIRNMENT_URL = "https://finance.chinapnr.com/npay/merchantRequest";
	public static final String CMD_ID = "cmd_id";
    public static final String VERSION = "version";
    public static final String MER_CUST_ID = "mer_cust_id";
    public static final String USER_CUST_ID = "user_cust_id";
    public static final String ORDER_ID = "order_id";
    public static final String ORDER_DATE = "order_date";
    public static final String SIGN = "sign";
    public static final String EXTENSION = "extension";
    public static final String IN_CUST_ID = "in_cust_id";
    public static final String IN_ACCT_ID = "in_acct_id";
    public static final String GOODS_DESC = "goods_desc";
    public static final String TRANS_AMT = "trans_amt";
    public static final String DEVICE_INFO = "device_info";
    public static final String RET_URL = "ret_url";
    public static final String BG_RET_URL = "bg_ret_url";
    public static final String MER_PRIV = "mer_priv";
    public static final String ORDER_EXPIRE_TIME = "order_expire_time";
    public static final String PAY_TYPE = "pay_type";
    public static final String ORGINAL_PLATFORM_SEQ_ID = "orginal_platform_seq_id";
    public static final String QUICKPAY_PAGE_FLAG = "quickpay_page_flag";
    public static final String DIV_DETAIL = "div_detail";
    public static final String TRANS_TYPE = "trans_type";
    public static final String SELF_PARAM_INFO = "self_param_info";
    public static final String APP_ID = "app_id";
    public static final String BUYER_LOGON_ID = "buyer_logon_id";
    public static final String BUYER_ID = "buyer_id";
    public static final String IP_ADDR = "ip_addr";
    public static final String LOCATION_VAL = "location_val";
    public static final String DEV_INFO_JSON = "dev_info_json";
    public static final String PAY_URL = "pay_url";
    public static final String PAY_INFO = "pay_info";
    public static final String SCREEN_TYPE = "screen_type";
    public static final String STYLE_VALUES = "style_values";
    public static final String PAY_METHOD = "pay_method";
    public static final String PLATFORM_SEQ_ID = "platform_seq_id";
    public static final String RESP_CODE = "resp_code";
    public static final String RESP_DESC = "resp_desc";
    public static final String ACCT_ID = "acct_id";
    public static final String BANK_ID = "bank_id";
    public static final String CARD_NO = "card_no";
    public static final String BIND_CARD_ID = "bind_card_id";
    public static final String FEE_AMT = "fee_amt";
    public static final String FEE_CUST_ID = "fee_cust_id";
    public static final String FEE_ACCT_ID = "fee_acct_id";
    public static final String TRANS_STAT = "trans_stat";
    public static final String URL = "url";
    //app支付cmd_id
    public static final String APP_CMD_ID = "218";
    //退款cmd_id
    public static final String REFUND_CMD_ID = "205";
    //交易状态查询cmd_id
    public static final String TRANS_QUERY_CMD_ID = "301";
    //快捷支付cmd_id
    public static final String QP_PAY_CMD_ID = "206";
    //05：支付宝正扫
    public static final String ALI_PAY_QR = "05";
    //10 : 微信APP支付
    public static final String WX_APP_PAY = "10";
    //11 : 支付宝APP支付（暂时不支持）
    public static final String ALI_APP_PAY = "11";
    //12：支付宝统一下单
    public static final String ALI_ORD_PAY = "12";
    //版本号
    public static final String VERSION_VALUE = "10";
    //成功码
    public static final String TRANS_SUCCESS = "02000002";
    //失败码
    public static final String TRANS_FAIL = "02000003";
    
    // 汇付支付+编号
    public static final String NPAY_MERID = "100001";
}
