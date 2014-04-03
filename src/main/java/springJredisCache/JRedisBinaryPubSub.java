package springJredisCache;

import org.springframework.stereotype.Service;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.util.SafeEncoder;

/**
 * @author 石头哥哥 </br>
 *         springJredisCache </br>
 *         Date:2014/4/2 </br>
 *         Time:14:37 </br>
 *         Package:{@link springJredisCache}</br>
 *         Comment：  处理订阅的消息
 *         subsribe(一般模式设置频道)和psubsribe(使用模式匹配来设置频道)。不管是那种模式都可以设置个数不定的频道
 *
 *
 *         订阅的监听类  ，订阅某个事件  那么会一直在
 *         {@link redis.clients.jedis.BinaryJedisPubSub#process(redis.clients.jedis.Client)} }轮询
 *         直到有订阅的消息发生（注册事件---这里  主要是只channel （或者通配符的表达式）） ，
 *         当然可以订阅 也可以取消  在   onUnsubscribe(byte[] channel, int subscribedChannels) 或则  onPUnsubscribe(byte[] pattern, int subscribedChannels)
 *         中处理取消订阅    ,  当取消一个订阅的时候 subscribedChannels 订阅的计数会减一  直到<=0   ----注意是来子redis的数据哈
 *         {@link redis.clients.jedis.BinaryJedisPubSub#isSubscribed()} }返回false 停止轮询  ！
 *          其中 channel订阅的等价 redis存储的 key
 *          message 等价redis存储的value
 *          为什么叫channel  或许是形象罢了        自己体会 订阅/发布
 */
@Service
public class JRedisBinaryPubSub extends BinaryJedisPubSub {

    /**
     * 处理订阅的消息
     *
     * @param channel
     * @param message
     */
    @Override
    public void onMessage(byte[] channel, byte[] message) {
        System.out.println( SafeEncoder.encode(channel) + "=" +SafeEncoder.encode(message));
    }

    /**
     * 处理按照  表达式的方式订阅的消息后的处理
     *
     * @param pattern 订阅匹配的     pattern
     * @param channel key
     * @param message value
     *                hello*=hello_1=123
     *
     *                可以将相应的message反序列化为相应的数据类型
     */
    @Override
    public void onPMessage(byte[] pattern, byte[] channel, byte[] message) {
        System.out.println(SafeEncoder.encode(pattern) + "=" + SafeEncoder.encode(channel) + "=" +SafeEncoder.encode(message));
    }




    /**
     * 取消订阅（注销）
     *
     * @param channel          key
     * @param subscribedChannels             当前订阅的数量
     */
    @Override
    public void onUnsubscribe(byte[] channel, int subscribedChannels) {
        System.out.println("取消订阅channel："+SafeEncoder.encode(channel) + "subscribedChannels=" + subscribedChannels);
    }


    /**
     * 取消订阅   按表达式的方式订阅的消息    （注销）
     *
     * @param pattern
     * @param subscribedChannels           当前订阅的数量
     */
    @Override
    public void onPUnsubscribe(byte[] pattern, int subscribedChannels) {
        System.out.println("取消订阅pattern："+SafeEncoder.encode(pattern) + "subscribedChannels=" + subscribedChannels);
    }


    /**
     *
     * 订阅初始化 在{@link springJredisCache.JCache#subscribe(String...)}
     *
     * 保留接口 不做处理
     * 初始化订阅
     *
     * @param channel
     * @param subscribedChannels      当前订阅的数量
     */
    @Override
    public void onSubscribe(byte[] channel, int subscribedChannels) {
    }

    /**
     * 订阅初始化 在{@link springJredisCache.JCache#publish(String, byte[])} }
     * 保留接口 不做处理
     * 初始化按表达式的方式订阅的消息
     *
     * @param pattern      订阅的消息类型
     * @param subscribedChannels            订阅的channel数量   from redis
     */
    @Override
    public void onPSubscribe(byte[] pattern, int subscribedChannels) {

    }
}
