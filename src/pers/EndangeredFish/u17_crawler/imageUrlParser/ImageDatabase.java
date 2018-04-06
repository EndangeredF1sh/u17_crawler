package pers.EndangeredFish.u17_crawler.imageUrlParser;

import pers.EndangeredFish.u17_crawler.introUrlParser.*;
import java.sql.*;

/**
 * ImageDatabase 数据库操作类
 * <p>
 *     可近似认为imageDatabase表的DAO类，使用SQL语句进行数据库连接/创建/插入操作
 * </p>
 * @author EndangeredFish
 * @version 1.0
 */
class ImageDatabase extends IdDatabase{
    ImageDatabase(){
        super();
    }
    /**
     * 连接数据库，若表不存在则创建imageDatabase表
     */
    @Override
    public void connectDatabase(){
        try{
            // 判断数据表存在情况
            sql = "SELECT count(*) FROM information_schema.TABLES WHERE TABLE_NAME ='imageDatabase'";
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
                sql = "CREATE TABLE `imageDatabase` " +
                        "(`image_id` int(11) NOT NULL AUTO_INCREMENT," +
                        "`comic_id` int(11) NOT NULL," +
                        "`chapter` int(5) NOT NULL," +
                        "`page` int(11) NOT NULL," +
                        "`url` varchar(255) NOT NULL DEFAULT '',PRIMARY KEY " +
                        "(`image_id`)) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;";
                res = statement.executeUpdate(sql);// executeUpdate语句会返回一个受影响的行数，如果返回-1就没有成功
                if(res != -1) System.out.println("[imageDatabase】imageDatabase数据表创建成功");
            }
            else{
                System.out.println("【imageDatabase】imageDatabase数据表已存在");
            }
            System.out.println("【imageDatabase】数据库已连接...");
        }
        catch(SQLException se){
            se.printStackTrace();
        }
    }

    /**
     * 将图片模板类分解插入数据库
     * @param i 图片模板类对象
     * @return 插入状态
     */
    public boolean insertToDatabase(ImageModel i){
        try{
            sql = "INSERT INTO imageDatabase(image_id, comic_id, chapter, page, url) VALUES ('"+Integer.toString(i.getImage_id())+"','"+
                    Integer.toString(i.getComicId())+"','"+
                    Integer.toString(i.getChapter())+"','"+
                    Integer.toString(i.getPage())+"','"+
                    i.getImageUrl()+"')";
            res = statement.executeUpdate(sql);
            if(res!= -1){
                return true;
            }
            else {
                return false;
            }
        }
        catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException me ){
            System.out.println("【重复】找到重复图片id, id="+i.getImage_id());
        }
        catch (SQLException se){
            se.printStackTrace();
        }
        return false;
    }
}
