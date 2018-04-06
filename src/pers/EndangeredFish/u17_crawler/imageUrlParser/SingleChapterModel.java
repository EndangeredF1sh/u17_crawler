package pers.EndangeredFish.u17_crawler.imageUrlParser;

/**
 * SingleChapterModel模板类
 * <p>单个章节的模板类</p>
 * @author EndangeredFish
 * @version 1.0
 */
public class SingleChapterModel {
    private int chapter;
    private int totalPage;
    private String url;

    /**
     * 构造函数
     * <p>对模板类进行初始化</p>
     * @param chapter 章节序号
     * @param totalPage 本章节含有的漫画页面数量
     * @param url 该章节对应的URL
     */
    SingleChapterModel(int chapter, int totalPage, String url){
        this.chapter = chapter;
        this.totalPage = totalPage;
        this.url = url;
    }
    public int getChapter(){
        return this.chapter;
    }
    public int getTotalPage(){
        return this.totalPage;
    }
    public String getUrl(){
        return this.url;
    }

}
