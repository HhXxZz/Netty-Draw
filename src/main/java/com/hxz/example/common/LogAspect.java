package com.hxz.example.common;


import com.alibaba.fastjson.JSONObject;
import com.hxz.example.handler.MyExceptionHandler;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;

@Aspect
@Component
@EnableAspectJAutoProxy
public class LogAspect {
    private final static Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Autowired
    private MyExceptionHandler exceptionHandler;

    @Pointcut("execution(public * com.hxz.example.controller.*.*.*(..))")//要处理的方法，包名+类名+方法名
    public void log() {}


    @Before("log()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        Map<String,String[]> paramsMap = request.getParameterMap();
        StringBuilder sbParams = new StringBuilder();

        for(String key:paramsMap.keySet()){
            sbParams.append(key).append("=");
            String [] temp = paramsMap.get(key);
            if(temp.length > 0){
                sbParams.append(temp[0]);
            }else{
                sbParams.append("null");
            }
            sbParams.append(",");
        }

        StringBuilder sbHeader = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()){
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            sbHeader.append(headerName).append("=").append(headerValue).append(",");
        }

        // 请求内容
        logger.info("URL:{}\nHTTP_METHOD:{}\nIP:{}\nCLASS_METHOD:{}\nPARAMS:{}\nHEAD:{}" ,
                request.getRequestURL().toString(),
                request.getMethod(),
                request.getRemoteAddr(),
                joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName(),
                sbParams.toString(),sbHeader.toString());
    }


    @Around("log()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        try {
            //每个接口try catch
        }catch (Exception e){
            Result result = exceptionHandler.handler(e);
            logger.info("response={}", JSONObject.toJSON(result));
            return result;
        }

        logger.info("----doAround-----------");

        return proceedingJoinPoint.proceed();
    }


    @After("log()")//无论Controller中调用方法以何种方式结束，都会执行
    public void doAfter(){
        logger.info("----doAfter-----------");
    }

    @AfterReturning(returning = "object", pointcut = "log()")
    public void doAfterReturning(Object object) throws Throwable {
        // 处理完请求，返回内容
        logger.info("response={}", JSONObject.toJSON(object));
    }



}
