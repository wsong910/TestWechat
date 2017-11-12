package com.song.testwechatdemo.model.bean;

import com.google.gson.annotations.SerializedName;
import com.song.testwechatdemo.bases.BaseData;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/10.
 * <p>
 * "profile-image": "http://img2.findthebest.com/sites/default/files/688/media/images/Mingle_159902_i0.png",
 * "avatar": "http://info.thoughtworks.com/rs/thoughtworks2/images/glyph_badge.png",
 * "nick": "John Smith",
 * "username": "jsmith"
 */

public class UserInfo extends BaseData implements Serializable {
    private static final long serialVersionUID = 8087796390417483082L;
    @SerializedName("profile-image")
    private String profileImage;
    private String avatar;
    private String nick;
    private String username;

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInfo task = (UserInfo) o;
        return super.equal(username, task.username) &&
                super.equal(nick, task.nick) &&
                super.equal(profileImage, task.profileImage) &&
                super.equal(avatar, task.avatar);
    }

    @Override
    public int hashCode() {
        return super.hashCode(avatar, nick, username, profileImage);
    }

}
