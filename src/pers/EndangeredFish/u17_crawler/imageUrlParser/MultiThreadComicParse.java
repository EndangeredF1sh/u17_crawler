package pers.EndangeredFish.u17_crawler.imageUrlParser;
import pers.EndangeredFish.u17_crawler.introUrlParser.IdDatabase;

import java.io.File;
import java.util.ArrayList;

/**
 * MultiThreadComicParse多线程爬取类
 * <p>
 *     该类对SingleComicParse类进行多线程的进一步实现
 * </p>
 * @author EndangeredFish
 * @version 1.1
 */
public class MultiThreadComicParse extends Thread{
    public static ArrayList<Integer> comicList = IdDatabase.readDatabase();
    private static int size = comicList.size();
    private static int nextIndex;
    MultiThreadComicParse(){
        ;
    }

    /**
     * 首次使用或出错后的初始化处理，初始化当前爬取对象为数据库首条记录
     * @param b 若为真，则从comicDatabase数据库中从第一条开始爬取数据
     */
    MultiThreadComicParse(boolean b){
        if(b) nextIndex=0;
    }
    /**
     * 对爬取中断后的恢复初始化工作
     * <p>
     *     通过传入的startID寻找对应索引下标，视为已经爬取完成的最后一条漫画记录进行初始化工作。
     *     初始化完成后游标指向第一条未爬取的漫画记录。
     * </p>
     * @param startId 已经爬取完成的最后一个comicID
     */
    MultiThreadComicParse(int startId){
        int cursor = comicList.indexOf(startId);
        if(cursor != -1) nextIndex = cursor+1;
        else{
            System.out.println("未找到匹配的漫画ID！请重新开始程序！");
            File file  = new File(ImageMainClass.getPropertiesDir());
            if(!file.exists()){
                System.out.println("配置文件不存在！");
            }
            else{
                if(file.delete()) System.out.println("配置文件删除成功！");
                System.out.println("程序退出...");
            }
            System.exit(-1);
        }
    }

    /**
     * 对Thread类的run()方法进行重写
     * <p>
     *     对SingleComicParse类实现多线程支持。
     *     每一个线程均创建一个SingleComicParse类对象进行爬取工作。
     *     多个线程共同读取nextIndex成员变量以确定当前爬取的漫画对象而不产生冲突。
     *     当nextIndex大于已读入的漫画数据库最大条数时，结束该线程。
     * </p>
     */
    @Override
    public void run(){
        try{
            while(nextIndex<size){
                System.out.println("【"+Thread.currentThread().getName()+"】"+"正在爬取id="+comicList.get(nextIndex)+"的所有内容");
                SingleComicParse s = new SingleComicParse(comicList.get(nextIndex));
                nextIndex++;
            }
        }
        catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
        finally {
            System.out.println("【"+Thread.currentThread().getName()+"】"+"任务完成！");
            try{
                ImageMainClass.currentComicID = comicList.get(nextIndex-1);
            }
            catch (IndexOutOfBoundsException e){
                interrupt();
            }
            ImageMainClass.endedThread++;
        }
    }
}
