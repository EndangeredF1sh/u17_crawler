package pers.EndangeredFish.u17_crawler.introUrlParser;
import java.util.*;

import com.github.kevinsawicki.http.HttpRequest;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

/**
 * IntroUrlParse 漫画介绍信息爬取方法类
 * <p>
 *     提供爬取单页漫画信息的各种方法
 * </p>
 * @author EndangeredFish
 * @version 1.0
 */
public class IntroUrlParse {
    private String baseUrl;
    private int totalPage;
    public int getTotalPage(){return this.totalPage;}

    /**
     * 向特定ajax页面发送结构信息，返回JSON格式信息
     * @param page 指定的页面
     * @return JSONObject格式的JSON信息
     */
    public JSONObject getInfoInObject(int page){
        Map<String, String> data = new HashMap<String, String>();
        JSONParser parser = new JSONParser();
        data.put("data[group_id]","no");
        data.put("data[theme_id]","no");
        data.put("data[is_vip]","30");
        data.put("data[accredit]","no");
        data.put("data[color]","no");
        data.put("data[comic_type]","no");
        data.put("data[series_status]","no");
        data.put("data[order]","2");
        data.put("data[page_num]",Integer.toString(page));
        data.put("data[read_mode]","no");
        String returnJson = HttpRequest.post(this.baseUrl).form(data).body();
        try{
            Object obj = parser.parse(returnJson);
            JSONObject JObject= (JSONObject) obj;
            return JObject;
        }
        catch (ParseException pe){
            pe.printStackTrace();
        }
        return null;
    }

    /**
     * 构造函数，确定特定ajax页面地址和免费漫画总页数
     * @param url 特定ajax页面地址
     */
    IntroUrlParse(String url){
        this.baseUrl = url;
        this.totalPage = Integer.parseInt(getInfoInObject(1).get("page_total").toString());
    }

    /**
     * 爬取指定单页面所有介绍信息
     * @param currentPage 想要获取漫画介绍信息的页面
     * @return ArrayList 形式返回的，最多60个漫画介绍信息。
     */
    public ArrayList getCurrentPageComicInfo(int currentPage){
        JSONArray array = (JSONArray) getInfoInObject(currentPage).get("comic_list");
        ArrayList<Map<String,String>> infolist = new ArrayList<Map<String, String>>();
        for(java.util.Iterator iter= array.iterator();iter.hasNext();){
            Map<String,String> map = new HashMap<String,String>();
            JSONObject job = (JSONObject) iter.next();
            map.put("comic_id", job.get("comic_id").toString());
            map.put("url","http://www.u17.com/comic/"+job.get("comic_id").toString()+".html");
            map.put("name",job.get("name").toString());
            infolist.add(map);
        }
//        System.out.println(infolist);
        return infolist;
    }

    /**
     * 把getCurrentPageComicInfo方法返回的ArrayList格式列表转换成ArrayList<IntroductionModel>格式列表并传给targetList
     * @param targetList 接受结果列表地址
     * @param infoList 将被转换的ArrayList格式列表
     */
    public void parseInfoListtoUrl(ArrayList<IntroductionModel> targetList, ArrayList infoList){
        Iterator<Map<String,String>> iter = infoList.iterator();
        for(;iter.hasNext();){
            IntroductionModel temp = new IntroductionModel();
            Map<String,String> map2 = iter.next();
            temp.setComicID(Integer.parseInt(map2.get("comic_id")));
            temp.setComicName(map2.get("name"));
            temp.setIntroUrl(map2.get("url"));
            targetList.add(temp);
        }
    }
}