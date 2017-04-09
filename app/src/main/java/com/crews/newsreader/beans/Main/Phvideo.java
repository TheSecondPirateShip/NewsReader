package com.crews.newsreader.beans.Main;

import java.io.Serializable;

/**
 * Created by zia on 2017/4/9.
 */

public class Phvideo implements Serializable {
    String channelName;
    String length;

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }
}
