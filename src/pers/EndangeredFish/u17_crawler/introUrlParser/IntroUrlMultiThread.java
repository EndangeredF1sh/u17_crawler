package pers.EndangeredFish.u17_crawler.introUrlParser;

import java.util.ArrayList;

/**
 * IntroUrlMultiThread 多线程爬取介绍信息类
 * <p>
 *     指定开始和结束页面创建线程
 *     爬取相应页面所有介绍信息压入mixedComicStack栈等待进一步操作
 *     单个线程的模板类，根据此类实现多线程爬取。
 * </p>
 * @author EndangeredFish
 * @version 1.1
 */
public class IntroUrlMultiThread extends Thread{
    private int startPage;
    private int endPage;
    IntroUrlMultiThread(int startPage, int endPage){
        this.startPage = startPage;
        this.endPage = endPage;
    }
    @Override
    public void run(){
        collectAllComicinfo(this.startPage,this.endPage);
    }

    /**
     * 多线程页码扫描
     * 实例化IntroUrlParse类，爬取指定页面获取结果
     * 结果压入mixedComicStack栈中,在主程序中写入数据表
     * @param startPage 起始页码
     * @param targetPage 结束页码
     */
    private void collectAllComicinfo(int startPage, int targetPage){
        IntroUrlParse parser = new IntroUrlParse("http://www.u17.com/comic/ajax.php?mod=comic_list&act=comic_list_new_fun&a=get_comic_list");
        System.out.println("["+Thread.currentThread().getName()+"】"+"正在收集数据...");
        for (int currentPage=startPage;currentPage<=targetPage;currentPage++){
            ArrayList<IntroductionModel> comicList = new ArrayList<>();
            parser.parseInfoListtoUrl(comicList, parser.getCurrentPageComicInfo(currentPage));
            for (IntroductionModel singleComic: comicList) {
                IntroMainClass.mixedComicStack
                        .push(new IntroductionModel(singleComic.getComicID(), singleComic.getComicName(), singleComic.getIntroUrl()));
            }
            IntroMainClass.alreadyPage++;
            System.out.println("【"+Thread.currentThread().getName()+"】"+"第"+Integer.toString(currentPage)+"页数据已全部被压入栈！");
        }
        System.out.println("【"+Thread.currentThread().getName()+"】"+"【信息】收集数据完成！");
        interrupt();
    }
}
