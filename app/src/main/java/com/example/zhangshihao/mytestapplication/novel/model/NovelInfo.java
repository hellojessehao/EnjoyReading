package com.example.zhangshihao.mytestapplication.novel.model;

/**
 * Created by zhangshihao on 2017/9/12.
 */

public class NovelInfo {
    public int id ;
    public String url;
    public String title;
    public String img;
    public String intro;
    public String author;
    public String category;
    public String updateTime;
    public int novelId;

    public void setId(int id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public void setNovelId(int novelId) {
        this.novelId = novelId;
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getImg() {
        return img;
    }

    public String getIntro() {
        return intro;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public int getNovelId() {
        return novelId;
    }

    @Override
    public String toString() {
        return "img = "+img+"\n url = "+url+"\n novelId = "+
                String.valueOf(novelId)+"\n title = "+title+" \n intro = "+
                intro+"\n author = "+author+"\n category = "+category+"\n updateTime = "+
                updateTime;
    }
}
