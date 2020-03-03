package com.hxz.example.common;

public class VaildateException extends RuntimeException {

    private String desc;

    public VaildateException(String desc){
        super(desc);
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
