package com.crews.newsreader.beans.Main;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zia on 2017/4/8.
 */

public class Item implements Serializable{
    String thumbnail;
    String title;
    String online;
    String source;
    String simId;
    String updateTime;
    String documentId;
    String type;
    String commentsUrl;
    String comments;
    String commentsall;
    boolean hasSlide;
    Style style;
    Link link;
    Phvideo phvideo;
    int isRead;//为和数据库类型同步，用01判断

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public boolean isHasSlide() {
        return hasSlide;
    }

    public void setHasSlide(boolean hasSlide) {
        this.hasSlide = hasSlide;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public Phvideo getPhvideo() {
        return phvideo;
    }

    public void setPhvideo(Phvideo phvideo) {
        this.phvideo = phvideo;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCommentsall() {
        return commentsall;
    }

    public void setCommentsall(String commentsall) {
        this.commentsall = commentsall;
    }

    public String getCommentsUrl() {
        return commentsUrl;
    }

    public void setCommentsUrl(String commentsUrl) {
        this.commentsUrl = commentsUrl;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getSimId() {
        return simId;
    }

    public void setSimId(String simId) {
        this.simId = simId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}






