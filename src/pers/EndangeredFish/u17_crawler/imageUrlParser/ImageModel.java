package pers.EndangeredFish.u17_crawler.imageUrlParser;
import java.util.Stack;
/**
 * ImageModel模板类
 * <p>漫画图片的模板类</p>
 * @author EndangeredFish
 * @version 1.1
 */
public class ImageModel {
    static Stack<ImageModel> imageStack = new Stack<>();
    private int comicId;
    private int chapter;
    private int page; //页码顺序
    private String imageUrl;
    private int image_id;

    /**
     * 构造函数
     * <p>对类各成员进行初始化</p>
     * @param comicId 对应的漫画ID
     * @param chapter 对应该漫画的章节数
     * @param order 对应该章节的哪一页漫画
     * @param image_id 对应有妖气定义的图片ID
     * @param imageUrl 图片对应的URL
     */
    ImageModel(int comicId, int chapter, int order, int image_id, String imageUrl){
        this.comicId = comicId;
        this.imageUrl = imageUrl;
        this.chapter = chapter;
        this.page = order;
        this.image_id = image_id;
    }
    public int getComicId(){
        return this.comicId;
    }
    public int getChapter(){
        return this.chapter;
    }
    public int getPage(){
        return this.page;
    }
    public int getImage_id(){
        return this.image_id;
    }
    public String getImageUrl(){
        return this.imageUrl;
    }
}
