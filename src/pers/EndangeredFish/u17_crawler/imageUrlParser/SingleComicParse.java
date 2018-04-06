package pers.EndangeredFish.u17_crawler.imageUrlParser;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
/**
 * SingleComicParse漫画分析类
 * <p>该类对单个漫画页面进行详细分析和爬取</p>
 * @author EndangeredFish
 * @version 1.3
 * @see MultiThreadComicParse 是本类的多线程实现
 */
public class SingleComicParse {
    private int retry = 0;
    private int chapter = 1;
    Stack<SingleChapterModel> currentComicStack = new Stack<>();
    private int comicId;
    private String introUrl;

    /**
     * 根据传入的comicID进行类初始化，分析和爬取操作
     * @param comicId 漫画ID
     */
    SingleComicParse(int comicId){
        this.comicId = comicId;
        this.introUrl = "http://www.u17.com/comic/"+Integer.toString(this.comicId)+".html";
        this.parse();
        this.craw();
    }

    /**
     * parse爬取方法的简单描述
     * <p>
     *     根据当前对象的comicID进行url构造和分析，以获取单个漫画所有的章节信息
     *     得到每个章节的信息后打包成SingleChapterModel类加入栈currentComicStack中
     * </p>
     * @throws NullPointerException 出现漫画页面不存在的时候抛出该异常，表示漫画已经不存在。
     */
    private void parse(){
        try{
            Document firstDocument = Jsoup.connect(this.introUrl).get();
            // 正则表达式构造
            String re1=".*?";	// Non-greedy match on filler
            String re2="(\\d+)";	// Integer Number 1
            Pattern p = Pattern.compile(re1+re2,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            Elements elements = firstDocument.select("[class=wrap cf]")
                    .select("[class=main]")
                    .select("[class=chapterlist]").select("[class=chapterlist_box]")
                    .select("ul[class=cf][id=chapter]")
                    .select("li");
            for(Element single:elements){
                String chapterUrl = single.select("a").first().attr("abs:href");
                //正则提取url
                Matcher m = p.matcher(single.childNode(2).toString());
                if (m.find()) {
                    int totalPage=Integer.parseInt(m.group(1));
                    currentComicStack.add(new SingleChapterModel(chapter,totalPage,chapterUrl));
                    chapter++;
                }
            }
            System.out.println("【当前进度】漫画id="+this.comicId+"所有章节已被压入栈");
            chapter=1;
        }
        catch (java.net.SocketTimeoutException ste){
            System.out.println("【警告】"+"【"+Thread.currentThread().getName()+"】"+"网络已断开...尝试重连...");
        }
        catch (IOException e){
            e.printStackTrace();
        }
        catch (NullPointerException ne){
            System.out.println("【警告】本漫画因未通过审核或被作者删除，id="+this.comicId);
        }
    }
    /**
     * craw核心爬取方法的简单描述
     * <p>
     *     本方法为核心爬取部分，遍历栈中的所有章节类，分析该章节url，获取所有图片，随后压入imageStack栈中等待数据库写入。
     *     可工作，但不能获取类似<a href="http://www.u17.com/comic/116263.html">我的男友不是人</a>
     *     形式的长页漫画。
     *     通过页面的特殊标志"<!-- v113 -->"鉴别，通过isLong标志区分长夜漫画和普通漫画，分别调用不同的爬取方式。
     *     由于长页漫画页面采用ajax动态加载漫画图片，jsoup只能分析静态页面，所以该版本代码无法获取长页漫画的图片url。
     *     =================================================================================================
     *     if(!isLong)部分为原版爬取代码，以分析html页面的方式获取图片url。
     *     本方法对于单页型漫画可以正常爬取，由于需要分析html页面，效率比新版低得多。但经测试本方法不会由于爬取速度过快而被服务器ban。
     *     由于不会被ban，则可以实现10线程以上的爬取，且无需增大单次爬取间隔防止被ban。
     *     故作为爬取主方法（因为大部分的漫画也是单页型漫画）。
     *     爬取完单张图片后将图片URL加入imageStack中，循环爬取直到数量达到单个chapter标明的最大页面数量(p数)。
     *     然后对配置文件中的漫画ID进行及时更新，防止中断后无法正确恢复进度。
     *     已自动丢弃有妖气的广告图片(URL以news.u17i.com开始的图片)
     *
     *     else部分调用新版爬取代码
     *     调用crawLong()方法作为对长页型漫画进行补充爬取（仅有极少部分的漫画为长页型漫画）。
     *     由于有妖气暂时仅有以上两种不同漫画格式，故该方法对不同格式的漫画均可以爬取。
     *     欲获得第二版，请转crawLong()方法描述作进一步了解。
     * </p>
     * @throws StringIndexOutOfBoundsException 截取URL的时候由于其不存在出现越界错误，即页面不存在图片时抛出该异常
     */
    private void craw(){
        // 遍历每个章节
        for(SingleChapterModel single : currentComicStack){
            boolean isLong = false;
            JSONParser parser = new JSONParser();
            String url = single.getUrl();
            try{
                Document document = Jsoup.connect(url).get();
                if(!document.toString().contains("<!-- v113 -->")) isLong = true; //区分两种漫画加载方式
                if(!isLong) {
                    Elements e = document.getElementsByTag("script").eq(19);
                    /*循环遍历script下面的JS变量*/
                    for (Element element : e) {
                        String[] data = element.data().split("var");
                        for (String variable : data) {
                            if (variable.contains("=") && variable.contains("image_config")) {
                                try{
                                    int i = variable.indexOf("image_list");
                                    int j = variable.indexOf("image_pages");
                                    String json = variable.substring(i+12,j-7);
                                    Object obj = parser.parse(json);
                                    JSONObject JObject= (JSONObject) obj;
                                    for(Object v:JObject.values()){
                                        JSONObject o = (JSONObject) v;
                                        String base64 = o.get("src").toString();
                                        String decodedUrl = new String(Base64.getDecoder().decode(base64),"utf-8");
                                        if(decodedUrl.contains("news.u17i.com")) continue;
                                        int order = Integer.parseInt(o.get("page").toString());
                                        int image_id = Integer.parseInt(o.get("image_id").toString());
                                        ImageModel.imageStack.add(new ImageModel(comicId,single.getChapter(),order,image_id,decodedUrl));
                                        ImageMainClass.currentComicID = comicId;
                                    }
                                }
                                catch (ParseException pe){
                                    pe.printStackTrace();
                                }
                                catch (StringIndexOutOfBoundsException se){
                                    System.out.println("【警告】未能找到图片，可能是漫画已被下架");
                                }
                            }
                        }
                    }
                }
                else{
                    crawLong(single);
                }
            }
            catch (java.net.SocketTimeoutException se){
                if(retry<5){
                    System.out.println("【警告】网络已断开，正在重连...");
                    this.craw();
                    retry++;
                }
                else{
                    System.out.println("【警告】重试五次失败，跳过本漫画ID="+comicId);
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 对特殊类型（长页）漫画爬取方法的简单描述
     * <p>
     *     本方法经实践可以爬取任何一种形式的漫画。
     *     但有妖气API服务器采用反爬机制，每次爬取都需要休眠一段时间作为间隔，效率实际上并不高。
     *     故该方法作为craw()方法的一个子方法，仅对特殊类型的漫画进行爬取，提高业务效率。
     *     对页面js动态加载代码分析，得到"get_chapter_v5"API，作为本方法突破口。
     *     API URL为"http://www.u17.com/comic/ajax.php?mod=chapter&act=get_chapter_v5&chapter_id="+chapterID
     *     对有妖气内部API进行请求，返回JSON格式的章节信息，参数不限于本方法所采用各参数，请读者自行请求和分析。
     *     对JSON格式的请求结果进行分析，得到每单张图片的信息。
     *     之后与craw()主方法处理方式相同，不再赘述。
     * </p>
     * @param single 从currentComicStack栈中弹出的模板对象
     */
    private void crawLong(SingleChapterModel single){
        JSONParser parser = new JSONParser();
        // 正则提取章节ID
        String url = single.getUrl();
        String re1=".*?";	// Non-greedy match on filler
        String re2="\\d+";	// Uninteresting: int
        String re3=".*?";	// Non-greedy match on filler
        String re4="(\\d+)";	// Integer Number 1
        Pattern p = Pattern.compile(re1+re2+re3+re4,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(url);
        String apiUrl;
        if (m.find()) {
            String chapterID=m.group(1);
            apiUrl = "http://www.u17.com/comic/ajax.php?mod=chapter&act=get_chapter_v5&chapter_id="+chapterID;
        }
        else{
            System.out.println("【警告】未能找到该章节图片...");
            return;
        }
        try{
            String json = Jsoup.connect(apiUrl).get().body().text();
            Object obj = parser.parse(json);
            JSONObject JObject = (JSONObject) obj;
            int order = 1;
            Object object = JObject.get("image_list");
            JSONArray arr = (JSONArray) object;
            for(Object v : arr){
                JSONObject o = (JSONObject) v;
                String imgSrc = o.get("src").toString();
                int image_id = Integer.parseInt(o.get("image_id").toString());
                ImageModel.imageStack.add(new ImageModel(comicId,single.getChapter(),order,image_id,imgSrc));
                order++;
                ImageMainClass.currentComicID = comicId;
            }
        }
        catch (ParseException | IOException pe){
            pe.printStackTrace();
        }
    }
}
