package com.jing.android.arch.demo.repo.service.model;

import com.google.gson.annotations.SerializedName;

/**
 * 聚合数据的Response
 *
 * @param <T>
 *
 * @author JingTuo
 */
public class JuHeResponse<T> {

    @SerializedName("error_code")
    private int errorCode;

    private String reason;

    private T result;


    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
