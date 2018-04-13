package pers.EndangeredFish.u17_crawler.introUrlParser;

import java.sql.*;
import java.util.ArrayList;

/**
 * ImageDatabase 数据库操作类
 * <p>
 *     可近似认为comicDatabase表的DAO类，使用SQL语句进行数据库连接/创建/插入操作
 * </p>
 * @author EndangeredFish
 * @version 1.1
 */
public class IdDatabase {
    protected IdDatabase(){
        try{
            // 动态加载JDBC驱动
            System.out.println("加载MySQL驱动程序成功");
            com.mysql.jdbc.Driver driver = new com.mysql.jdbc.Driver();
            // 连接数据库
            connection = DriverManager.getConnection(url);
            // 创建statement 实现增删改查
            statement = connection.createStatement();
        }
        catch (SQLException se){
            se.printStackTrace();
        }
    }
    protected Connection connection = null;
    protected ResultSet rs = null;
    protected String sql;
    protected Statement statement;
    protected int res = 0;

    /**
     * 硬编码JDBC URL
     * 构造方式：jdbc:mysql://主机名称：连接端口/数据库的名称?参数=值
     * 为保证中文显示正常，需指定useUnicode=true 和 characterEncoding=UTF8
     * 执行数据库操作前应现在MySQL数据库中创建名为'comicDatabase'的数据库
     */
    protected static final String username = "root";
    protected static final String password = "root";
    protected static final String url = "jdbc:mysql://localhost:3306/comicDatabase?"
            + "user="+username+"&password="+password+"&useUnicode=true&characterEncoding=UTF8&useSSL=true";
    protected boolean existFlag = false;
    /**
     * 连接数据库，若表不存在则创建comicDatabase表
     */
    public void connectDatabase(){
        try{
            // 判断数据表存在情况
            sql = "SELECT count(*) FROM information_schema.TABLES WHERE TABLE_NAME ='comicDatabase'";
            rs = statement.executeQuery(sql);
            rs.next();
            if(Integer.parseInt(rs.getString(1)) >= 1){
                this.existFlag = true;
            }
            else {
                this.existFlag = false;
            }

            // 数据表不存在时 创建表操作
            if(!this.existFlag){
                sql = "CREATE TABLE `comicDatabase`" +
                        " (`comic_id` int(11) NOT NULL AUTO_INCREMENT," +
                        "`name` char(20) NOT NULL DEFAULT '' COMMENT '漫画名称'," +
                        "`url` varchar(255) NOT NULL DEFAULT ''," +
                        "PRIMARY KEY (`comic_id`)) " +
                        "ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8" +
                        "";
                res = statement.executeUpdate(sql);// executeUpdate语句会返回一个受影响的行数，如果返回-1就没有成功
                if(res != -1) System.out.println("comicDatabase数据表创建成功");
            }
            else{
                System.out.println("comicDatabase数据表已存在");
            }
            System.out.println("数据库已连接...");
        }
        catch(SQLException se){
            se.printStackTrace();
        }
    }

    /**
     * 读取整个comicDatabase数据表，其comicID是imageUrlParser部分的解析基础
     * @return ArrayList形式的comicID数组，包含所有漫画的comicID
     */
    public static ArrayList<Integer> readDatabase(){
        try{
            // 动态加载JDBC驱动
            System.out.println("【comicDatabase】加载MySQL驱动程序成功");
            com.mysql.jdbc.Driver driver = new com.mysql.jdbc.Driver();
            // 连接数据库
            Connection connection = DriverManager.getConnection(url);
            // 创建statement 实现增删改查
            Statement statement = connection.createStatement();
            String sql = "SELECT comic_id, url FROM comicDatabase";
            ResultSet rs = statement.executeQuery(sql);
            ArrayList<Integer> lists = new ArrayList<>();
            while(rs.next()){
                lists.add(rs.getInt("comic_id"));
            }
            return lists;
        }
        catch (SQLException se){
            se.printStackTrace();
        }
        return null;
    }

    /**
     * 根据爬取的数据将各项特征信息插入数据库
     * @param comic_id comicID 单个漫画的唯一标识(主键)
     * @param name 漫画名称
     * @param url 漫画介绍页URL
     * @return 插入状态信息
     */
    public boolean insertToDatabase(int comic_id, String name, String url){
        try{
            sql = "INSERT INTO comicDatabase(comic_id, name, url) VALUES ('"+Integer.toString(comic_id)+"','"+
                    name+"','"+url+"')";
            res = statement.executeUpdate(sql);
            if(res!= -1){
                return true;
            }
            else {
                return false;
            }
        }
        catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException me ){
            System.out.println("【重复】找到重复漫画id, id="+comic_id);
        }
        catch (SQLException se){
            se.printStackTrace();
        }
        return false;
    }
}
