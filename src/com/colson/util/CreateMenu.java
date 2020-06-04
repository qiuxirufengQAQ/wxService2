package com.colson.util;

import com.colson.entity.Button;
import com.colson.entity.ClickButton;
import com.colson.entity.SubButton;
import com.colson.entity.ViewButton;
import com.colson.service.WxService;
import net.sf.json.JSONObject;

public class CreateMenu {
    public static void main(String[] args){

        //菜单对象
        Button button = new Button();
        //第一个一级菜单
        button.getButton().add(new ClickButton("购买门票","1"));

        //第二个一级菜单
        button.getButton().add(new ViewButton("我的门票","https://www.baidu.com"));

        //创建第三个一级菜单
        SubButton subButton = new SubButton("畅游公园");

        //为第三个一级菜单增加子菜单
        subButton.getSub_button().add(new ClickButton("票务公告","ceshi1"));
        subButton.getSub_button().add(new ViewButton("关于畅游公园","https://www.baidu.com"));
        subButton.getSub_button().add(new ViewButton("信用旅游信息公示","https://www.baidu.com"));

        //加入第三个一级菜单
        button.getButton().add(subButton);

        //转为json
        JSONObject jsonObject = JSONObject.fromObject(button);

        //准备url
        String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";

        url = url.replace("ACCESS_TOKEN",WxService.getAccessToken());

        //发送请求
        String result = Util.post(url,jsonObject.toString());
        System.out.println(result);
    }

}
