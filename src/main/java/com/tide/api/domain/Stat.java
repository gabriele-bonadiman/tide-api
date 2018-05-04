package com.tide.api.domain;


import java.io.Serializable;

public class Stat implements Serializable{

    private long jhi_like;
    private long jhi_dislikes;

    public Stat() { }

    public Stat(long jhi_like, long jhi_dislikes) {
        this.jhi_like = jhi_like;
        this.jhi_dislikes = jhi_dislikes;
    }

    public long getLikes() {
        return jhi_like;
    }

    public void setLikes(long likes) {
        this.jhi_like = likes;
    }

    public long getDislikes() {
        return jhi_dislikes;
    }

    public void setDislikes(long dislikes) {
        this.jhi_dislikes = dislikes;
    }
}
