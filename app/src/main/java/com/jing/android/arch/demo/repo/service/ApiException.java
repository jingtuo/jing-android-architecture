package com.jing.android.arch.demo.repo.service;

/**
 *
 * @author JingTuo
 */
public class ApiException extends Exception {

    private int code;

    public ApiException(String message, int code) {
        super(message);
        this.code = code;
    }
}
