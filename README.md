#基于jedis+spring的简易封装 redis缓存。
#序列化方案 ：
1. jdk原生序列化方案；
2. 基于kryo序列化方案 ;
3.基于 FST序列化方案 ;

#序列化测试性能对比 10w次序列化 反序列化：
![test](http://git.oschina.net/uploads/images/2014/0214/102416_f5ac080e_1052.png)




