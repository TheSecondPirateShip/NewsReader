package com.crews.newsreader.beans;

import java.util.List;

/**
 * Created by zia on 2017/4/9.
 */

public class Data {
    String listId;
    String type;
    String count;
    List<New> item;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public List<New> getItem() {
        return item;
    }

    public void setItem(List<New> item) {
        this.item = item;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
