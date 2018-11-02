package com.huifu.npay.master.domain;

import com.huifu.npay.master.util.constant.Constants;
import com.huifu.saturn.cfca.util.StringUtils;

public class CfcaInfoBo {
	// 加签证书
    private String pfxFile;
    // 加签密码
    private String pfxFilePwd;
    // 解签证书
    private String cerFile;
    // 汇付支付+编号
    private String npayMerId;
	public String getPfxFile() {
		return pfxFile;
	}
	public void setPfxFile(String pfxFile) {
		this.pfxFile = pfxFile;
	}
	public String getPfxFilePwd() {
		return pfxFilePwd;
	}
	public void setPfxFilePwd(String pfxFilePwd) {
		this.pfxFilePwd = pfxFilePwd;
	}
	public String getCerFile() {
		return cerFile;
	}
	public void setCerFile(String cerFile) {
		this.cerFile = cerFile;
	}
	public String getNpayMerId() {
		if(StringUtils.isBlank(npayMerId)){
			return Constants.NPAY_MERID;
		}
		return npayMerId;
	}
	public void setNpayMerId(String npayMerId) {
		this.npayMerId = npayMerId;
	}
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("CfcaInfoBo [pfxFile=");
		sb.append(pfxFile);
		sb.append(",pfxFilePwd=");
		sb.append(pfxFilePwd);
		sb.append(",cerFile=");
		sb.append(cerFile);
		sb.append(",npayMerId=");
		sb.append(npayMerId);
		sb.append("]");
		return sb.toString();
	}
	
}
