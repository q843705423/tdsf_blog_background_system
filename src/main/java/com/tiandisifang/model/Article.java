package com.tiandisifang.model;

public class Article {
    private String articleId;
    private String articleName;
    private String articleContent;
    private String articleType;
    private String articleCreateTime;
    private String articleLastChangeTime;
    private String articleLabel;
    private Integer articleViewNumber;
    private Integer articleCommentsNumber;


    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getArticleName() {
        return articleName;
    }

    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    public String getArticleContent() {
        return articleContent;
    }

    public void setArticleContent(String articleContent) {
        this.articleContent = articleContent;
    }

    public String getArticleType() {
        return articleType;
    }

    public void setArticleType(String articleType) {
        this.articleType = articleType;
    }


    public String getArticleCreateTime() {
        return articleCreateTime;
    }

    public void setArticleCreateTime(String articleCreateTime) {
        this.articleCreateTime = articleCreateTime;
    }

    public String getArticleLastChangeTime() {
        return articleLastChangeTime;
    }

    public void setArticleLastChangeTime(String articleLastChangeTime) {
        this.articleLastChangeTime = articleLastChangeTime;
    }

    public String getArticleLabel() {
        return articleLabel;
    }

    public void setArticleLabel(String articleLabel) {
        this.articleLabel = articleLabel;
    }
//                /article_ view_number
    public Integer getArticleViewNumber() {
        return articleViewNumber;
    }

    public void setArticleViewNumber(Integer articleViewNumber) {
        this.articleViewNumber = articleViewNumber;
    }

    public Integer getArticleCommentsNumber() {
        return articleCommentsNumber;
    }

    public void setArticleCommentsNumber(Integer articleCommentsNumber) {
        this.articleCommentsNumber = articleCommentsNumber;
    }
}
