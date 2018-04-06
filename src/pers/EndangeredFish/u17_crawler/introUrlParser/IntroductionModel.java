package pers.EndangeredFish.u17_crawler.introUrlParser;
/**
 * Model漫画介绍页模板类
 * <p>漫画介绍页的模板类</p>
 * @author EndangeredFish
 * @version 1.0
 */
public class IntroductionModel {
    private int comicID;
    private String comicName;
    private String introUrl;
    public void setComicID(int comicID){
        this.comicID = comicID;
    }
    public int getComicID(){
        return this.comicID;
    }
    public void setComicName(String comicName){
        this.comicName = comicName;
    }
    public String getComicName(){
        return this.comicName;
    }
    public void setIntroUrl(String introUrl){
        this.introUrl = introUrl;
    }
    public String getIntroUrl(){
        return this.introUrl;
    }

    /**
     * 构造函数
     * <p>对类各成员进行初始化</p>
     * @param comicID 漫画ID
     * @param comicName 漫画名称
     * @param url 漫画介绍页的URL
     */
    IntroductionModel(int comicID, String comicName, String url){
        this.introUrl = url;
        this.comicName = comicName;
        this.comicID = comicID;
    }
    IntroductionModel(){;}
}
