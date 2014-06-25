package springJredisCache;

import javolution.util.FastMap;
import javolution.util.FastTable;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * 增加订阅 发布接口
 */
public interface JCache {


    /**
     * 发布 消息
     *
     * @param channel
     * @param message
     * @return
     */
    public Long publish(String channel, byte[] message);

    /**
     * 订阅指定的消息        订阅得到信息在BinaryJedisPubSub的onMessage(...)方法中进行处理
     *
     * @param channels
     */
    public void subscribe(String... channels);

    /**
     * 取消所有订阅的channel
     */
    public void unsubscribe();

    /**
     * 取消订阅的channel
     * @param channels
     */
    public void unsubscribe(String... channels);

    /**
     * 表达式的方式订阅
     * 使用模式匹配的方式设置要订阅的消息            订阅得到信息在BinaryJedisPubSub的onMessage(...)方法中进行处理
     *
     * @param patterns          订阅的消息类型
     */
    public void psubscribe(String... patterns);

    /**
     * 取消所有订阅的channel
     */
    public void punsubscribe();

    /**
     * 取消订阅的channel
     * @param patterns
     */
    public void punsubscribe(String... patterns);


    /**
     * @return
     */
    public String info();


    /**
     *
     * @param key
     * @param filed
     * @return
     */
    public ArrayList<?> getList(String key,String filed);

    /**
     *
     * @param key
     * @param filed
     * @param list
     * @return
     */
    public String putList(String key, String filed ,ArrayList<?> list);


    /**
     * @param key
     * @return
     * @
     */
    public ArrayList<?> getList(String key);

    /**
     * @param key
     * @param list
     */
    public String putList(String key, ArrayList<?> list);

    /**
     * Remove an item from the cache
     */
    public void removeList(String key);


    /**
     * @param key
     * @return
     */
    public FastTable<?> getFastTable(String key);


    /**
     * @param key
     * @param list
     */
    public void putFastTable(String key, FastTable<?> list);

    /**
     * @param key
     * @return
     * @
     */
    public FastMap<?, ?> getFastMap(String key);

    /**
     * Remove an item from the cache
     */
    public void removeFastMap(String key);

    /**
     * @param key
     * @param fastMap
     */
    public void putFastMap(String key, FastMap<?, ?> fastMap);

    /**
     * Get an item from the cache, nontransactionally
     *
     * @param key
     * @return the cached object or <tt>null</tt>
     * @throws JRedisCacheException
     */
    public Serializable getObject(String key);

    /**
     * Add an item to the cache, nontransactionally, with
     * failfast semantics
     *
     * @param key
     * @param value
     * @
     */
    public void putObject(String key, Serializable value);

    /**
     * Remove an item from the cache
     */
    public void removeObject(String key);

    /**
     * @return
     * @throws JRedisCacheException
     */
    public FastTable<String> keys();



    public void destroy();


    /**
     * Queue
     *
     * @param key
     * @param value
     */
    public void addQueue(String key, Serializable value);

    /**
     * poll  from queue
     *
     * @param key
     */
    public Serializable pollFromQueue(String key);


    /**
     * peek  from queue
     *
     * @param key
     */
    public Serializable peekFromQueue(String key);

}
