package com.hxz.example.common;

import lombok.Data;

@Data
public class Result {
    private int code;
    private String msg;
    private String desc;
    private Object data;


    public static Result success(Object object){
        ResultEnum resultEnum = ResultEnum.SUCCESS;
        return getResult(resultEnum,object);
    }


    public static Result error(ResultEnum resultEnum){
        return getResult(resultEnum);
    }

    public static Result error(ResultEnum resultEnum,String desc){
        Result result = new Result();
        result.setCode(resultEnum.getCode());
        result.setMsg(resultEnum.getMsg());
        result.setDesc(desc);
        result.setData("{}");
        return result;
    }


    private static Result getResult(ResultEnum resultEnum,Object object){
        Result result = new Result();
        result.setCode(resultEnum.getCode());
        result.setMsg(resultEnum.getMsg());
        result.setDesc(resultEnum.getDesc());
        result.setData(object);
        return result;
    }

    private static Result getResult(ResultEnum resultEnum){
        Result result = new Result();
        result.setCode(resultEnum.getCode());
        result.setMsg(resultEnum.getMsg());
        result.setDesc(resultEnum.getDesc());
        result.setData("{}");
        return result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
