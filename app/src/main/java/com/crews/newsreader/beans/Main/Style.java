package com.crews.newsreader.beans.Main;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zia on 2017/4/9.
 */

public class Style implements Serializable {
    String type;
    int slideCount;
    List<String> images;

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public int getSlideCount() {
        return slideCount;
    }

    public void setSlideCount(int slideCount) {
        this.slideCount = slideCount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}