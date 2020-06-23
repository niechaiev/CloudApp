package com.example.cloudapp;


import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.io.Serializable;
import java.util.Date;

public class Note implements Serializable {
    private String name;
    private String description;
    private Importance importance;
    private Date date;
    private int picResource;
    private String uri;
    private String mime;

    Note(String name, String description, Importance importance, Date date, int picResource) {
        this.name = name;
        this.description = description;
        this.importance = importance;
        this.date = date;
        this.picResource = picResource;
    }

    Note(String name, String description, Importance importance, Date date, Uri uri, String mime) {
        this.name = name;
        this.description = description;
        this.importance = importance;
        this.date = date;
        this.uri = String.valueOf(uri);
        this.mime = mime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    Importance getImportance() {
        return importance;
    }

    public void setImportance(Importance importance) {
        this.importance = importance;
    }

    Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    int getPicResource() {
        return picResource;
    }

    public void setPicResource(int picResource) {
        this.picResource = picResource;
    }


    public Uri getUri() {
        if(uri==null) return null;
        return Uri.parse(uri);
    }

    public void setUri(Uri uri) {
        if(uri==null) this.uri=null;
        this.uri = String.valueOf(uri);
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }
}


