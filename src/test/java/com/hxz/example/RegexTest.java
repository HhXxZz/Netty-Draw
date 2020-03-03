package com.hxz.example;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: hxz
 * @Description:
 * @Date: 2020/2/26 14:39
 */
public class RegexTest {


    public static void main(String[] args) {

        String regex = "^/?token$";

        Pattern p = Pattern.compile(regex);

        //创建一个匹配器，匹配给定的输入与此模式。

        Matcher m = p.matcher("/?token=asas");
        if(m.find()) {
            System.out.println(m.group(0));
        }else{
            System.out.println("未匹配");
        }

    }

}
