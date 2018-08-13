package com.fdse.scontroller.constant;


/**
 * <pre>
 *     author : shenbiao
 *     e-mail : 1105125966@qq.com
 *     time   : 2018/08/11
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class UrlConstant {

    /**
     * App后端url
     */
    public static final String APP_BACK_END_IP = "192.168.1.142";//142
    public static final String APP_BACK_END_PORT = "8080";

    //用户登录
    public static final String APP_BACK_END_USER_LOGIN_SERVICE = "user/login";

    public static String getAppBackEndServiceURL(String  service) {
        String serviceURL = String.format("http://%s:%s/%s", APP_BACK_END_IP, APP_BACK_END_PORT, service);
        return serviceURL;
    }

    /**
     * 众包平台url
     */

}
