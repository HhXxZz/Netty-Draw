package com.hxz.example.common;

public class GlobalException extends RuntimeException {

    private ResultEnum resultEnum;

    public GlobalException(ResultEnum resultEnum){
        super();
        this.resultEnum = resultEnum;
    }

    public GlobalException(String msg){
        super(msg);
    }

    public ResultEnum getResultEnum(){
        return resultEnum;
    }

}
