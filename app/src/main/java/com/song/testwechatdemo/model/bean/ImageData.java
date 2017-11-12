package com.song.testwechatdemo.model.bean;

import com.song.testwechatdemo.bases.BaseData;

import java.io.Serializable;


/**
 * Created by Administrator on 2017/11/10.
 */


public class ImageData extends BaseData implements Serializable {
    private static final long serialVersionUID = 58543555030744396L;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageData task = (ImageData) o;
        return super.equal(url, task.url);
    }

    @Override
    public int hashCode() {
        return super.hashCode(url);
    }
}
