#基于jedis+spring的简易封装 redis缓存。

#1.配置redis客户端操作以及spring，启动redis;
#2.测试用例详见Test测试结果如下;
![eeee](http://git.oschina.net/uploads/images/2014/0214/162636_89b3b797_1052.png)

#序列化方案 ：
1.jdk原生序列化方案;    </br>
2.基于kryo序列化方案 ;   </br>
3.基于 FST序列化方案 ;   </br>
4.基于 protobuffer序列化方案 ;   </br>

#序列化测试性能对比 10w次序列化 反序列化：
![test](http://git.oschina.net/uploads/images/2014/0214/102416_f5ac080e_1052.png)
#fst kryo都是不错的选择！
#测试protobuffer相当的优秀  只是该序列化方案要在特定的环境中 有局限性
#在生产环境中推荐使用FST ，kryo在生产中会出现一些怪异的问题 （反序列化数据过长会失败！）
# ps:该组件已经在某手机游戏服务器中得到了验证与应用 请放心使用！