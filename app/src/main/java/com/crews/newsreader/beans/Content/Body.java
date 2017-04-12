package com.crews.newsreader.beans.Content;

import java.util.List;

/**
 * Created by zia on 2017/4/9.
 */

public class Body {
    private String title;
    private String author;
    private String text;
    private List<String> itags;
    private String wapurl;
    private String wwwurl;
    private String source;
    private List<Slides> slides;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<String> getItags() {
        return itags;
    }

    public void setItags(List<String> itags) {
        this.itags = itags;
    }

    public List<Slides> getSlides() {
        return slides;
    }

    public void setSlides(List<Slides> slides) {
        this.slides = slides;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWapurl() {
        return wapurl;
    }

    public void setWapurl(String wapurl) {
        this.wapurl = wapurl;
    }

    public String getWwwurl() {
        return wwwurl;
    }

    public void setWwwurl(String wwwurl) {
        this.wwwurl = wwwurl;
    }

    public String getSource(){
        return source;
    }

    private void setSource(String source){
        this.source = source;
    }
}




