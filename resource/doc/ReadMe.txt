=========依赖jar包=========
druid-1.0.5.jar
fastjson-1.1.40.jar
fel.jar
google-guava.jar
JZMQ.jar
log4j-1.2.16.jar
mysql-connection-java-5.1.31-bin.jar
netty-all-4.0.32.Final.jar

=========配置文件==========
cache.json 缓存池的配置
npfsdatabase.properties druid配置
log4j.properties 日志配置

===========cache.json配置==========================
约束条件：
	1.size为handler中所有blockSize的公倍数
	2.size为下一级(child.blockInterval_in_ms/blockInterval_in_ms)的倍数
适用范围：1second--1week
