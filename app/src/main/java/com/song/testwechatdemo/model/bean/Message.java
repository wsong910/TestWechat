package com.song.testwechatdemo.model.bean;

import com.google.gson.annotations.SerializedName;
import com.song.testwechatdemo.bases.BaseData;

import java.io.Serializable;
import java.util.List;


/**
 * Created by Administrator on 2017/11/10.
 */


public class Message extends BaseData implements Serializable {
    @SerializedName(value = "contentStr", alternate = {"content", "error", "unKnow"})
    private String contentStr;
    private List<ImageData> images;
    private UserInfo sender;
    private List<Message> comments;
    public transient int dataSize;
    public transient int imageIndex;
    public transient int viewType;

    public String getContentStr() {
        return contentStr;
    }

    public void setContentStr(String contentStr) {
        this.contentStr = contentStr;
    }

    public List<ImageData> getImages() {
        return images;
    }

    public void setImages(List<ImageData> images) {
        this.images = images;
    }

    public UserInfo getSender() {
        return sender;
    }

    public void setSender(UserInfo sender) {
        this.sender = sender;
    }

    public List<Message> getComments() {
        return comments;
    }

    public void setComments(List<Message> comments) {
        this.comments = comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message task = (Message) o;
        return super.equal(sender, task.sender) &&
                super.equal(contentStr, task.contentStr) &&
                super.equal(images, task.images) &&
                super.equal(comments, task.comments);
    }

    @Override
    public int hashCode() {
        return super.hashCode(sender, contentStr, images, comments);
    }
}
