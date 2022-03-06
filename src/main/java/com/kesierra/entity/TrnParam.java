package com.kesierra.entity;

import java.io.Serializable;

public class TrnParam implements Serializable {
    private String trnNo;
    private SmsRequestBody smsRequestBody;

    public String getTrnNo() {
        return trnNo;
    }

    public void setTrnNo(String trnNo) {
        this.trnNo = trnNo;
    }

    public SmsRequestBody getSmsRequestBody() {
        return smsRequestBody;
    }

    public void setSmsRequestBody(SmsRequestBody smsRequestBody) {
        this.smsRequestBody = smsRequestBody;
    }
}
