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
    public static final String APP_BACK_END_IP = "fudanse.club";
    public static final String APP_BACK_END_PORT = "80/sc";

    //用户登录1
    public static final String APP_BACK_END_USER_LOGIN_SERVICE = "user/login";

    public static final String APP_BACK_END_USER_TEST = "user/test";

    //保存位置信息
    public static final String APP_BACK_END_USER_SAVE_LOCATION= "user/saveLocation";

    //测试用--获取owls
    public static final String APP_BACK_END_TASKS_GET_OWLS= "task/getOwls";

    //测试用--获取bpmn
    public static final String APP_BACK_END_TASKS_GET_BPMN= "task/getBPMN";

    //保存任务
    public static final String APP_BACK_END_TASKS_SAVE_TASK= "task/saveTask";

    //获取所有任务列表
    public static final String APP_BACK_END_TASKS_GET_ONGOING_TASKS= "task/getOngoingTasks";


    public static String getAppBackEndServiceURL(String  service) {
        String serviceURL = String.format("http://%s:%s/%s", APP_BACK_END_IP, APP_BACK_END_PORT, service);
        return serviceURL;
    }

    /**
     * 本体库平台url
     */
    public static final String ONTOLOGY_IP = "47.100.23.182";//142
    public static final String ONTOLOGY_PORT = "8004";

    //用户登录
    public static final String ONTOLOGY_GET_OWLS = "query/compositeServiceQuery?serviceName=IfRoomEmptyService";

    public static String getOntologyServiceURL(String  service) {
        String serviceURL = String.format("http://%s:%s/%s", ONTOLOGY_IP, ONTOLOGY_PORT, service);
        return serviceURL;
    }

    /**
     * 流程执行引擎url
     */
    public static final String ACTIVITI_IP = "192.168.1.120";//142
    public static final String ACTIVITI_PORT = "8080";

    //用户登录
    public static final String ACTIVITI_RUN_BPMN_ENGINE= "runBpmnEngine";

    public static String getActivitiServiceURL(String  service) {
        String serviceURL = String.format("http://%s:%s/%s", ACTIVITI_IP, ACTIVITI_PORT, service);
        return serviceURL;
    }

    /**
     * 众包平台url
     */


    /**
     * 文件服务器
     */
    public static final String FILE_IP = "127.0.0.1";//142,148
    public static final String FILE_PORT = "8080";

    //上传设备发现裁剪图片
    public static final String FILE_ADD_DEVICE_IMAGE= "file/addDeviceImage";

    public static String getFlieServiceURL(String  service) {
        String serviceURL = String.format("http://%s:%s/%s", FILE_IP, FILE_PORT, service);
        return serviceURL;
    }

}
