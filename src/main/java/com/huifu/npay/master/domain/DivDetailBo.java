package com.huifu.npay.master.domain;

import java.io.Serializable;

/**
 * 分账明细对象
 * 
 * Date : 2016-4-18 上午10:32:36
 * 
 * @author huming.wang
 * @version v1.0.0 
 * @since JDK 1.7
 */
public class DivDetailBo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5837808080972516529L;
	String divCustId;
    String divAcctId;
    String divAmt;
    String divFreezeFg;

    public String getDivCustId() {
        return divCustId;
    }

    public void setDivCustId(String divCustId) {
        this.divCustId = divCustId;
    }
    
    public String getDivFreezeFg() {
        return divFreezeFg;
    }

    public void setDivFreezeFg(String divFreezeFg) {
        this.divFreezeFg = divFreezeFg;
    }

    public String getDivAcctId() {
        return divAcctId;
    }

    public void setDivAcctId(String divAcctId) {
        this.divAcctId = divAcctId;
    }

    public String getDivAmt() {
        return divAmt;
    }

    public void setDivAmt(String divAmt) {
        this.divAmt = divAmt;
    }
}