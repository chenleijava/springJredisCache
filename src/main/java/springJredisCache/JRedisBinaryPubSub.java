package springJredisCache;

import redis.clients.jedis.BinaryJedisPubSub;

/**
 * @author 石头哥哥</br>
 *         Date:14-4-1</br>
 *         Time:下午9:23</br>
 *         Package:springJredisCache</br>
 *         Comment： jedis 实现订阅发布
 */
public class JRedisBinaryPubSub extends BinaryJedisPubSub {

    /**
     *
     * @param channel
     * @param message
     */
    @Override
    public void onMessage(byte[] channel, byte[] message) {

    }

    /**
     * @param pattern
     * @param channel
     * @param message
     */
    @Override
    public void onPMessage(byte[] pattern, byte[] channel, byte[] message) {

    }

    /**
     *
     * @param channel
     * @param subscribedChannels
     */
    @Override
    public void onSubscribe(byte[] channel, int subscribedChannels) {

    }

    /**
     *
     * @param channel
     * @param subscribedChannels
     */
    @Override
    public void onUnsubscribe(byte[] channel, int subscribedChannels) {

    }

    /**
     * @param pattern
     * @param subscribedChannels
     */
    @Override
    public void onPUnsubscribe(byte[] pattern, int subscribedChannels) {

    }

    /**
     * @param pattern
     * @param subscribedChannels
     */
    @Override
    public void onPSubscribe(byte[] pattern, int subscribedChannels) {

    }
}
