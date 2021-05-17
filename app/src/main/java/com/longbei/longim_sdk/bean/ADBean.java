package com.longbei.longim_sdk.bean;

/**
 * Created by Administrator on 2018/3/24/024.
 */

public class ADBean {
    private String title;
    private String imgUrl;
    private String contentUrl;

    public ADBean() {
    }

    public ADBean(String title, String imgUrl, String contentUrl) {
        this.title = title;
        this.imgUrl = imgUrl;
        this.contentUrl = contentUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }
}
