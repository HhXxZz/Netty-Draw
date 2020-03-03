package com.hxz.example.handler;


import com.alibaba.fastjson.JSONObject;
import com.hxz.example.common.GlobalException;
import com.hxz.example.common.Result;
import com.hxz.example.common.ResultEnum;
import com.hxz.example.common.VaildateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@ResponseBody
public class MyExceptionHandler {
    private final static Logger logger = LoggerFactory.getLogger(MyExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    public Result handler(Exception e){
        Result result;

        if(e instanceof GlobalException){
            //e.printStackTrace();
            GlobalException globalException = (GlobalException) e;
            result = Result.error(globalException.getResultEnum());
            logger.info("response={}", JSONObject.toJSON(result));
            return result;
        }else if(e instanceof MissingServletRequestParameterException){
            MissingServletRequestParameterException exception = (MissingServletRequestParameterException) e;
            String paramName = exception.getParameterName();
            logger.error("缺少参数:{}",paramName);
            String desc = "缺少参数:"+paramName;
            return Result.error(ResultEnum.REQUEST_PARAMETER_ERROR,desc);
        }else if (e instanceof HttpRequestMethodNotSupportedException){
            HttpRequestMethodNotSupportedException exception = (HttpRequestMethodNotSupportedException) e;
            logger.error("请求方法错误");
            return Result.error(ResultEnum.REQUEST_METHOD_ERROR);
        }else if(e instanceof VaildateException){
            VaildateException exception = (VaildateException) e;
            String desc = exception.getDesc();
            logger.error("参数错误:{}",desc);
            return Result.error(ResultEnum.REQUEST_PARAMETER_ERROR,desc);
        }
        result = Result.error(ResultEnum.SYSTEM_ERROR);
        logger.error("response="+JSONObject.toJSON(result),e);

        return result;
    }

}
