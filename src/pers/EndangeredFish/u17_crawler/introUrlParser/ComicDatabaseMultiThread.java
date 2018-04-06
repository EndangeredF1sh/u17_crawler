package pers.EndangeredFish.u17_crawler.introUrlParser;

/**
 * ComicDatabaseMultiThread 数据库多线程主操作类
 * <p>
 *     对mixedComicStack栈中元素逐个弹出插入comicDatabase数据表，支持多线程（我也不知道为什么要支持多线程）
 * </p>
 */
public class ComicDatabaseMultiThread extends Thread {
    @Override
    public void run() {
        IdDatabase comicDatabase = new IdDatabase();
        comicDatabase.connectDatabase();
        while(true){
            if (!IntroMainClass.mixedComicStack.isEmpty()) {
                IntroductionModel singleComic = IntroMainClass.mixedComicStack.pop();
                comicDatabase.insertToDatabase(singleComic.getComicID(), singleComic.getComicName(), singleComic.getIntroUrl());
            }
            else{
                try{
                    Thread.sleep(10000);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
