package pers.EndangeredFish.u17_crawler.introUrlParser;

import java.util.Scanner;
import java.util.Stack;

/**
 * IntroMainClass 漫画介绍信息主操作类
 * <p>
 *     使用一个静态成员mixedComicStack临时保存爬取到的漫画介绍信息(comicID,漫画名,介绍页URL)。
 *     固定使用8个线程进行对所有免费漫画页的爬取，同时将mixedComicStack栈内临时信息写入数据库中。
 *     程序编写早期写成，实际上下一步仅需使用comicID作为关键数据即可。
 *     对多线程使用不熟练，用了很多笨拙手法（固定线程数量，前几个线程固定爬取特定页面，最后一个线程爬到最后一个页面以及对线程池中线程的存活状态判断等）。
 *     有时间会进一步优化代码，包括指定线程数量爬取（线程负载均衡）以及对线程池存活状态判断优化。
 *     请各位看官高抬贵手轻喷。
 * </p>
 * @author EndangeredFish
 * @version 1.0
 */
public class IntroMainClass {
    static Stack<IntroductionModel> mixedComicStack = new Stack<>();
    static int alreadyPage = 0;
    public void run(){
        Scanner in = new Scanner(System.in);
        while(true){
            System.out.print("需要扫描所有免费漫画信息吗？（首次使用必须扫描）[y/n]: ");
            String result = in.nextLine();
            if(result.equals("y") || result.equals("yes")){
                // 获取总页数
                int maxPage  = new IntroUrlParse("http://www.u17.com/comic/ajax.php?mod=comic_list&act=comic_list_new_fun&a=get_comic_list")
                        .getTotalPage();
                ComicDatabaseMultiThread databaseThread = new ComicDatabaseMultiThread();
                IntroUrlMultiThread t1 = new IntroUrlMultiThread(1,40);
                IntroUrlMultiThread t2 = new IntroUrlMultiThread(41,80);
                IntroUrlMultiThread t3 = new IntroUrlMultiThread(81,120);
                IntroUrlMultiThread t4 = new IntroUrlMultiThread(121,161);
                IntroUrlMultiThread t5 = new IntroUrlMultiThread(162,202);
                IntroUrlMultiThread t6 = new IntroUrlMultiThread(203,242);
                IntroUrlMultiThread t7 = new IntroUrlMultiThread(243,283);
                IntroUrlMultiThread t8 = new IntroUrlMultiThread(283,maxPage);
                t1.start();
                t2.start();
                t3.start();
                t4.start();
                t5.start();
                t6.start();
                t7.start();
                t8.start();
                databaseThread.start();
                while(true){
                    if(!t1.isAlive() && !t2.isAlive() && !t3.isAlive() && !t4.isAlive()
                            && !t5.isAlive() && !t6.isAlive() && !t7.isAlive() && !t8.isAlive()){
                        System.out.println("完成最后工作中...");
                        while(true){
                            if(IntroMainClass.mixedComicStack.isEmpty()){
                                System.out.println("数据写入完成，程序退出");
                                System.exit(0);
                            }
                            else {
                                System.out.println("正在插入数据库...");
                                System.out.println("待插入条数："+mixedComicStack.size()+"条");
                            }
                        }
                    }
                    else{
                        try{
                            Thread.sleep(30000);
                            System.out.println("【插入进度】待插入条数："+mixedComicStack.size()+"条");
                            System.out.println("【总进度】"+(alreadyPage*1.0/maxPage)*100+"%");
                        }
                        catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
            else if(result.equals("n")|| result.equals("no")){
                break;
            }
        }
    }
}

