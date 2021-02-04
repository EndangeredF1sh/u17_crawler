u17_crawler
=================
[![Build Status](https://travis-ci.org/EndangeredF1sh/u17_crawler.svg?branch=master)](https://travis-ci.org/EndangeredF1sh/u17_crawler)

该项目是什么？
-----------------
u17_crawler 

一个由Java编写的,针对u17.com（有妖气原创漫画梦工厂）上的免费漫画所编写的爬虫程序

[项目地址](https://github.com/EndangeredF1sh/u17_crawler) | 
[文档手册](https://EndangeredF1sh.github.io/JavaDoc/)

依赖
-----------------

* MySQL

* json-simple-1.1

* jsoup-1.11.2

* mysql-connector-java

* HttpRequest

安装
------------------
```
git@github.com:EndangeredF1sh/u17_crawler.git

cd u17_crawler

vi src/pers/EndangeredFish/u17_crawler/introUrlParser/IdDatabase.java # 修改mysql数据库用户名和密码
```

快速开始
------------------
![](https://raw.githubusercontent.com/EndangeredF1sh/u17_crawler/master/image1.png)

* 第一次使用本程序时需要扫描所有漫画信息存入数据库（数据量较大，存储后方便其他操作）

* 输入线程的数量，最低1个

* 爬虫开始运行，时间依网络情况而定

* 支持从程序退出断点处恢复工作

* 漫画介绍信息存放在comicDatabase数据库comicDatabase表中（至2018.4，约43w条）

* 单张漫画图片信息comicDatabase数据库imageDatabase表中（至2018.4，约182w条）

To Do List
--------------------
* 增加更多信息（如作者名、上传时间、读者评论等）

* 使用Maven自动构建

* Travis CI 集成

* 优化运行速度

* 增加可视化界面

交流讨论
--------------------
[submit issue](https://github.com/EndangeredF1sh/u17_crawler/issues/new) |
[文档手册](http://github.endangeredf1sh.cn/JavaDoc/) | 
[联系作者](mailto:zwy346545141@gmail.com)

##
如果你看到这里，求个star可好？


