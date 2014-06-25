#基于jedis+spring的简易封装 redis缓存。

#1.配置redis客户端操作以及spring，启动redis;
#2.测试用例详见Test测试结果如下;
![eeee](http://git.oschina.net/uploads/images/2014/0214/162636_89b3b797_1052.png)

#序列化方案 ：
1.jdk原生序列化方案;    </br>
2.基于kryo序列化方案 ;   </br>
3.基于 FST序列化方案 ;   </br>
4.基于 protobuffer序列化方案 ;   </br>
5.基于redis的订阅/发布方案；</br>

#序列化测试性能对比 10w次序列化 反序列化：
![QQ截图20140625141711](http://git.oschina.net/uploads/images/2014/0625/141810_8c03a33c_1052.png)
#fst kryo都是不错的选择！
#其中kyro默认采用了UsafeInput 和UsafeOutPut流 ，直接操作内存，提供更快的序列化方案
#测试protobuffer相当的优秀  只是该序列化方案要在特定的环境中 有局限性
#kyro是纯java中应该最快的，但是在序列化无构造函数的时候会抛出空指针！推荐使用fst的序列化方案!
# ps:该组件已经在某手机游戏服务器中得到了验证与应用 请放心使用！