package com.kesierra.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

public class SmsRequestBody implements Serializable {
    @NotBlank(
            message = "acceptor_tel不能为空"
    )
    private String acceptor_tel;
    @JsonSerialize(
            as = Date.class
    )
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private Date timestamp;
    @NotBlank(
            message = "qos不能为空"
    )
    private String qos;
    @NotNull(
            message = "template_param不能为空"
    )
    private Template template_param;

    public SmsRequestBody() {
    }

    public String getAcceptor_tel() {
        return this.acceptor_tel;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public String getQos() {
        return this.qos;
    }

    public Template getTemplate_param() {
        return this.template_param;
    }

    public void setAcceptor_tel(final String acceptor_tel) {
        this.acceptor_tel = acceptor_tel;
    }

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    public void setTimestamp(final Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setQos(final String qos) {
        this.qos = qos;
    }

    public void setTemplate_param(final Template template_param) {
        this.template_param = template_param;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof SmsRequestBody)) {
            return false;
        } else {
            SmsRequestBody other = (SmsRequestBody)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label59: {
                    Object this$acceptor_tel = this.getAcceptor_tel();
                    Object other$acceptor_tel = other.getAcceptor_tel();
                    if (this$acceptor_tel == null) {
                        if (other$acceptor_tel == null) {
                            break label59;
                        }
                    } else if (this$acceptor_tel.equals(other$acceptor_tel)) {
                        break label59;
                    }

                    return false;
                }

                Object this$timestamp = this.getTimestamp();
                Object other$timestamp = other.getTimestamp();
                if (this$timestamp == null) {
                    if (other$timestamp != null) {
                        return false;
                    }
                } else if (!this$timestamp.equals(other$timestamp)) {
                    return false;
                }

                Object this$qos = this.getQos();
                Object other$qos = other.getQos();
                if (this$qos == null) {
                    if (other$qos != null) {
                        return false;
                    }
                } else if (!this$qos.equals(other$qos)) {
                    return false;
                }

                Object this$template_param = this.getTemplate_param();
                Object other$template_param = other.getTemplate_param();
                if (this$template_param == null) {
                    if (other$template_param != null) {
                        return false;
                    }
                } else if (!this$template_param.equals(other$template_param)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof SmsRequestBody;
    }

    public int hashCode() {
        int result = 1;
        Object $acceptor_tel = this.getAcceptor_tel();
        result = result * 59 + ($acceptor_tel == null ? 43 : $acceptor_tel.hashCode());
        Object $timestamp = this.getTimestamp();
        result = result * 59 + ($timestamp == null ? 43 : $timestamp.hashCode());
        Object $qos = this.getQos();
        result = result * 59 + ($qos == null ? 43 : $qos.hashCode());
        Object $template_param = this.getTemplate_param();
        result = result * 59 + ($template_param == null ? 43 : $template_param.hashCode());
        return result;
    }

    public String toString() {
        return "SmsRequestBody(acceptor_tel=" + this.getAcceptor_tel() + ", timestamp=" + this.getTimestamp() + ", qos=" + this.getQos() + ", template_param=" + this.getTemplate_param() + ")";
    }
}
