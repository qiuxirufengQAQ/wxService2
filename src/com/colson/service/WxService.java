package com.colson.service;

import com.colson.entity.*;
import com.colson.util.Util;
import com.thoughtworks.xstream.XStream;
import net.sf.json.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class WxService {
    private static final String TOKEN = "wwgzs";

    private static final String GET_TOKEN_URL="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

//    微信公众号
    private static final String APPID = "wx2ee8a0d997f597c2";
    private static final String APPSECRET = "76499c4dd5fc9b7672313f67bbaab470";

    private static AccessToken at;

    /**
     * 获取token
     */
    private static void getToken() {
        String url = GET_TOKEN_URL.replace("APPID", APPID).replace("APPSECRET", APPSECRET);
        String tokenStr = Util.get(url);
        JSONObject jsonObject = JSONObject.fromObject(tokenStr);
        String token = jsonObject.getString("access_token");
        String expireIn = jsonObject.getString("expires_in");
        //创建token对象,并存起来。
        at = new AccessToken(token, expireIn);
    }

    /**
     * 向处暴露的获取token的方法
     * @return
     */
    public static String getAccessToken() {
        if(at==null||at.isExpired()) {
            getToken();
        }
        return at.getAccessToken();
    }

    /**
     * 验证签名
     */
    public static boolean check(String timestamp, String nonce, String signature) {
        //1）将token、timestamp、nonce三个参数进行字典序排序
        String[] strs = new String[]{TOKEN,timestamp,nonce};
        Arrays.sort(strs);
        System.out.println(strs.toString());
        //2）将三个参数字符串拼接成一个字符串进行sha1加密
        String str = strs[0]+strs[1]+strs[2];
        String mysig = sha1(str);
        //3）开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
        return mysig.equalsIgnoreCase(signature);
    }

    /**
     * 进行sha1加密
     */
    private static String sha1(String str) {
        try {
//            获取加密对象
            MessageDigest md = MessageDigest.getInstance("sha1");
        //加密
            byte[] digest = md.digest(str.getBytes());
            char[] chars = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b:digest) {
                stringBuilder.append(chars[(b>>4)&15]);
                stringBuilder.append(chars[b&15]);
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String,String> parseRequest(InputStream inputStream) {
        Map<String,String> map = new HashMap<>();
        SAXReader reader = new SAXReader();
        try {
//            读取输入流，获取文档对象
            Document document = reader.read(inputStream);
//            根据问的那个对象获取根节点
            Element root = document.getRootElement();
//            获取根节点的所有的子节点
            List<Element> elements = root.elements();
            for (Element e:elements) {
                map.put(e.getName(),e.getStringValue());
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static String getResponse(Map<String, String> parameterMap) {
        BaseMessage baseMessage =null;
        String msgType = parameterMap.get("MsgType");
        switch (msgType){
//            处理文本信息
            case "text":
                baseMessage = dealTextMessage(parameterMap);
                break;
            case "image":
//                msg = dealImageMessage(parameterMap);
                break;
            case "voice":
//                msg = dealVoiceMessage(parameterMap);
                break;
            case "video":
//                msg = dealVideoMessage(parameterMap);
                break;
            case "shortvideo":
//                msg = dealShortVideoMessage(parameterMap);
                break;
            case "location":
//                msg = dealLocationMessage(parameterMap);
                break;
            case "link":
//                msg = dealLinkMessage(parameterMap);
                break;
            case "event":
//                msg = dealEventMessage(parameterMap);
                break;
            default:
                break;
        }
//        把消息对象处理为xml数据包
        if(baseMessage!=null){
            return beanToXml(baseMessage);
        }else {
            return "";
        }
    }

    private static String beanToXml(BaseMessage baseMessage) {
        XStream stream = new XStream();
//        设置需要处理XStreamAlias("xml")注释的类
        stream.processAnnotations(TextMessage.class);
        stream.processAnnotations(ImageMessage.class);
        stream.processAnnotations(MusicMessage.class);
        stream.processAnnotations(NewsMessage.class);
        stream.processAnnotations(VideoMessage.class);
        stream.processAnnotations(VoiceMessage.class);
        String xml = stream.toXML(baseMessage);
        return xml;

    }

    private static BaseMessage dealTextMessage(Map<String, String> parameterMap) {
        //用户发来的内容
        String msg = parameterMap.get("Content");
        if(msg.equals("图文")) {
            List<Article> articles = new ArrayList<>();
            articles.add(new Article("this is the picture's title", "this is the picture's content", "http://mmbiz.qpic.cn/mmbiz_jpg/zEMURIMTtLt9pvrZMO1q3h56DAnXIJVrtbiaqq9dsEdbdNibicWrp4funu9gdKf57wexAiaRIoYV737XndFWPvOmog/0", "http://www.baidu.com"));
            NewsMessage nm = new NewsMessage(parameterMap, articles);
            return nm;
        }
        if(msg.equals("登录")) {
            String url="https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxb6777fffdf5b64a4&redirect_uri=http://www.6sdd.com/weixin/GetUserInfo&response_type=code&scope=snsapi_userinfo#wechat_redirect";
            TextMessage tm = new TextMessage(parameterMap, "click<a href=\""+url+"\">hear</a>submit");
            return tm;
        }
        //调用方法返回聊天的内容
        String resp = "I am busy now,please visit later";
        TextMessage tm = new TextMessage(parameterMap, resp);
        return tm;
    }



}
