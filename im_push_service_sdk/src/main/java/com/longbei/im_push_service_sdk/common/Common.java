package com.longbei.im_push_service_sdk.common;

/**
 * @author qiujuer
 */

public class Common {
    /**
     * 一些不可变的永恒的参数
     * 通常用于一些配置
     */
    public interface Constance {
        // 手机号的正则,11位手机号
        String REGEX_MOBILE = "[1][3,4,5,7,8][0-9]{9}$";

        // 基础的网络请求地址
        String API_URL = "http://192.168.0.189:8080/api/";

        // 最大的上传图片大小860kb
        long MAX_UPLOAD_IMAGE_LENGTH = 860 * 1024;
    }

    public static boolean isNewPusMsg = false;
    public static String curSessionId ;


    public static final String TYPE = "TYPE"; //取推送数据类型的key
    public static final int TYPE_GET_CLIENTID = 1;//推送数据类型，clientid
    public static final int TYPE_GET_DATA = 2;//推送数据类型，数据
    public static final String DATA = "DATA";//推送数据类型，取数据的key
    public static final String TITLE = "TITLE";
    public static final String KEY_CACHE = "WebResourceInterceptor-Key-Cache";
}
