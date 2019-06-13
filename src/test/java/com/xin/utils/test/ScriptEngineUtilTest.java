package com.xin.utils.test;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.InputStream;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: js脚本引擎测试类(用一句话描述该类做什么)
 * @date 2018-06-20 11:15
 * @Copyright (C)2017 , Luchaoxin
 */
public class ScriptEngineUtilTest {
    public static void main(String[] args) throws Exception {
        //获得脚本引擎对象
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine engine = sem.getEngineByName("javascript");

        //定义变量，存储到引擎上下文中:即可以被Java使用，也可以被javascript使用
        engine.put("msg", "world is very big");
        String str = "var user = { name:'zhangsan',age:10,school:['清华','北大']};";
        str += "println(user.school)";//注意不能定义为alert();

        //执行脚本
//        engine.eval(str);
        engine.put("msg", "you are pretty girl");
        System.out.println(engine.get("msg"));
        String path = ScriptEngineUtilTest.class.getClass().getResource("/").getPath();
        System.out.println(path);
        InputStream is = ScriptEngineUtilTest.class.getClassLoader().getResourceAsStream("function.js");


        //定义函数
        engine.eval("function add(a,b){return a+b;}");
        engine.eval("function getDeviceType(videoFlag){return videoFlag.substring(10, 13);}");
        engine.eval("function getGbNodeFlag (videoFlag){if (videoFlag && videoFlag.length != 20) {return -1;}" +
                "var gbNodeFlag = videoFlag.substring(0, 8); " +
                "while(gbNodeFlag.lastIndexOf(\"00\", gbNodeFlag.length) >= 0) {" +
                "  gbNodeFlag = gbNodeFlag.substring(0, gbNodeFlag.length - 2);" +
                "}" +
                "return gbNodeFlag+'_'+gbNodeFlag.lastIndexOf(\"00\", gbNodeFlag.length);" +
                "}");


        //执行js函数
        //取得调用接口
        Invocable jsinvoke = (Invocable) engine;
        //调用方法
        Object result = jsinvoke.invokeFunction("add", new Object[]{1, 2});
        Object gbNodeFlag = jsinvoke.invokeFunction("getGbNodeFlag","44010000002000000001");
        System.out.println(gbNodeFlag);

        System.out.println(result);
        //调用方法
        Object deviceType = jsinvoke.invokeFunction("getDeviceType", new Object[]{"44000000001110000072"});
        System.out.println(deviceType);

////        //执行一个js文件
//        URL url = ScriptEngineUtil.class.getClassLoader().getResource("a.js");
//        FileReader fr = new FileReader(url.getPath());
//        engine.eval(fr);
        //Invocable js = (Invocable)engine;
        //js.invokeFunction("test", null);
//        fr.close();
    }
}
