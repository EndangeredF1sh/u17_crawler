package pers.EndangeredFish.u17_crawler.imageUrlParser;

import java.io.*;
import java.util.EmptyStackException;
import java.util.Properties;
import java.util.Scanner;
import java.util.Date;

/**
 * ImageMainClass图片爬取主操作类
 * <p>
 *     综合图片模板类、爬取类和数据库类进行综合爬取
 * </p>
 * @author EndangeredFish
 * @version 1.4
 */
public class ImageMainClass{
    /* 初始化各公共变量 */
    static int endedThread = 0;
    static int currentComicID = 0;
    private Properties properties = new Properties();
    private OutputStream output = null;
    private InputStream input = null;
    private Scanner in = new Scanner(System.in);
    static private String propertiesDir = "";
    static public String getPropertiesDir(){
        return propertiesDir;
    }
    /**
     * 运行爬取部分
     * <p>
     *     函数代码内含详细注释
     * </p>
     * @throws EmptyStackException 操作栈中暂时不存在元素（一般是数据库插入速度比爬取速度快导致）
     */
    public void run(){
        int startComicId = 0;
        File directory = new File("");//设定为当前文件夹
        try{
            propertiesDir = directory.getAbsolutePath()+"/config.properties";
//            System.out.println(propertiesDir);//获取绝对路径
        }catch(Exception exc){
            System.out.println("配置文件路径设置错误，本程序暂不支持Windows系统，若非Windows系统，请自行检查代码.");
            exc.printStackTrace();
        }
        MultiThreadComicParse mtcp = null;
        /* 读配置文件 */
        try {
            input = new FileInputStream(propertiesDir);
            properties.load(input);
            startComicId = Integer.parseInt(String.valueOf(properties.get("id")));
            mtcp = new MultiThreadComicParse(startComicId);
            System.out.println("【初始化】扫描将由id="+startComicId+"开始");
        }
        catch (IOException ex){
            System.out.println("【警告】读写配置文件错误，将从头开始扫描并生成新配置文件...");
            try{
                File file = new File(propertiesDir);
                if(file.createNewFile()){
                    System.out.println("配置文件生成成功！");
                }
                else {
                    System.out.println("配置文件已存在！");
                }
            }
            catch (IOException e){
                System.out.println("文件创建失败！");
                e.printStackTrace();
            }
            mtcp = new MultiThreadComicParse(true);
        }
        finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        /* 读配置文件结束 */

        /* 创建多个爬取线程，最低1个*/
        System.out.print("请输入线程数，开的线程越多，越容易被服务器屏蔽: ");
        int t = in.nextInt();
        mtcp.start();
        for(int i=0;i<t;i++){
            new MultiThreadComicParse().start();
        }

        /* 创建数据库对象，循环检测活线程数量和栈中是否存在元素，将数据插入数据库并给出进度提示 */
        ImageDatabase db = new ImageDatabase();
        db.connectDatabase();
        while(true){
            if(endedThread < t){
                try{
                    db.insertToDatabase(ImageModel.imageStack.pop());
                }
                catch (EmptyStackException e){
                    try {
                        Thread.sleep(10000);
                        System.out.println("【插入进度】待插入条数："+ImageModel.imageStack.size());
                        this.writeConfig();
                    }
                    catch (InterruptedException ie){
                        ie.printStackTrace();
                    }
                }
            }
            if(endedThread >= t){
                if(ImageModel.imageStack.isEmpty()){
                    this.writeConfig();
                    System.out.println("【总进度】所有数据已被插入数据库.");
                    System.out.println("【总进度】程序结束.");
                    break;
                }
                else{
                    db.insertToDatabase(ImageModel.imageStack.pop());
                }
            }
        }
    }

    /**
     * 对配置文件进行写操作
     */
    private void writeConfig(){
        /* 写配置文件 */
        try{
            output = new FileOutputStream(propertiesDir);
            properties.setProperty("id",Integer.toString(currentComicID));
            properties.store(output, new Date().toString());
        }
        catch (IOException ie){
            ie.printStackTrace();
        }
        finally {
            if(output!=null){
                try{
                    output.close();
                }
                catch (IOException ioexception){
                    ioexception.printStackTrace();
                }
            }
        }
        /* 写配置文件结束 */
    }
}
