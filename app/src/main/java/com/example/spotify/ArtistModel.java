package com.example.spotify;

public class ArtistModel {

    String mbid;
    String name;
    String img;
    Boolean favorite;

    public ArtistModel(String mbid, String name, String img, Boolean favorite) {
        this.mbid = mbid;
        this.name = name;
        this.img = img;
        this.favorite = favorite;
    }

    public ArtistModel() {
    }

    public String getMbid() {
        return mbid;
    }

    public void setMbid(String mbid) {
        this.mbid = mbid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Boolean getFavorite() {return favorite;}

    public void setFavorite(Boolean favorite) {this.favorite = favorite;}
}
