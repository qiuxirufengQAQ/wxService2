package com.colson.controller;

import com.colson.request.WxRequest;
import com.colson.service.WxService;
import jdk.internal.util.xml.impl.Input;
import org.springframework.http.HttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.io.*;
import java.util.Map;

@Controller
public class BaseController {

    @RequestMapping(value = "/wx",method = RequestMethod.GET)
    @ResponseBody
    public void wxGet(@RequestParam("signature") String signature, @RequestParam("timestamp") String timestamp, @RequestParam("nonce") String nonce, @RequestParam("echostr") String echostr, Writer out){
        /**
         * signature	微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
         timestamp	时间戳
         nonce	随机数
         echostr	随机字符串
         */
//        校验签名
        if (WxService.check(timestamp,nonce,signature)){
            System.out.println("success");
            try {
                out.write(echostr);
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            System.out.println("fail");
        }
    }

    @RequestMapping(value = "/wx",method = RequestMethod.POST)
    @ResponseBody
    public void wxPost(InputStream inputStream,Writer writer){

        System.out.println("parameter in");
        Map<String,String> parameterMap = WxService.parseRequest(inputStream);

        System.out.println(parameterMap.toString());

        String respXml = WxService.getResponse(parameterMap);
        try {
            writer.write(respXml);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
