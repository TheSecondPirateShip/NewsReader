package com.crews.newsreader.beans.Main;

import java.io.Serializable;

/**
 * Created by zia on 2017/4/9.
 */

public class Link implements Serializable {
    String type;
    String url;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}