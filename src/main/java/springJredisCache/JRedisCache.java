package springJredisCache;

import javolution.util.FastMap;
import javolution.util.FastTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.BinaryJedis;
import redis.clients.util.SafeEncoder;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;

/**
 * @author 石头哥哥 </br>
 *         Date:1/7/14</br>
 *         Time:9:47 PM</br>
 *         Package:com.dc.gameserver.extComponents.jredisCache</br>
 *         Comment： 基于 spring + jedis封装的 redis 缓存操作接口
 *         运行时异常，IO异常，销毁binaryJedis对象
 *         spring- data redis中，
 *         open-->>getResource()
 *         向对象池借用 binaryJedis对象；
 *         close--->returnBrokenResource(binaryJedis) or returnResource(binaryJedis)
 *         如果出现异常（runntime ，io），那么将销毁 binaryJedis对象，否则将其归还到对象池；
 *
 *
 */
@Service
public class JRedisCache implements JCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(JRedisCache.class);

    @Resource
    private JRedisPool jRedisPool;

    @Resource
    private JRedisBinaryPubSub jRedisBinaryPubSub;

//    private static final ExecutorService handleSubscribe= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());  // 处理订阅消息


    /**
     * 运行时异常，IO异常，销毁binaryJedis对象
     *
     * @param ex
     * @param jRedisPool
     * @param binaryJedis
     */
    protected void coverException(Exception ex, JRedisPool jRedisPool, BinaryJedis binaryJedis) {
        if (binaryJedis == null) throw new NullPointerException();
        if (ex instanceof JRedisCacheException || ex instanceof IOException) {
            jRedisPool.returnBrokenResource(binaryJedis); //销毁该对象
        }
    }

    /**
     * 发布 消息
     *
     * @param channel
     * @param message
     * @return
     */
    @Override
    public Long publish(String channel, byte[] message) {
        BinaryJedis binaryJedis = null;
        try {
            binaryJedis = jRedisPool.getResource();
            return binaryJedis.publish(channel.getBytes(), message);
        } catch (Exception ex) {
            coverException(ex, jRedisPool, binaryJedis);
            return null;
        } finally {
            if (binaryJedis != null) {
                jRedisPool.returnResource(binaryJedis);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("close redis connection-{" + binaryJedis.toString() + "}");
                }
            }
        }
    }

    /**
     * 订阅指定的消息        订阅得到信息在BinaryJedisPubSub的onMessage(...)方法中进行处理
     *
     * @param channels
     */
    @Override
    public void subscribe(final  String... channels) {
        BinaryJedis binaryJedis = null;
        try {
            binaryJedis = jRedisPool.getResource();
            final byte[][] ps = new byte[channels.length][];
            for (int i = 0; i < ps.length; i++) {
                ps[i] = SafeEncoder.encode(channels[i]);
            }
            binaryJedis.subscribe(jRedisBinaryPubSub,ps);
        } catch (Exception ex) {
            coverException(ex, jRedisPool, binaryJedis);
        } finally {
            if (binaryJedis != null) {
                jRedisPool.returnResource(binaryJedis);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("close redis connection-{" + binaryJedis.toString() + "}");
                }
            }
        }
    }

    /**
     * 取消所有订阅的channel
     */
    @Override
    public void unsubscribe() {
        jRedisBinaryPubSub.unsubscribe();
    }

    /**
     * 取消订阅的channel
     *
     * @param channels
     */
    @Override
    public void unsubscribe(String... channels) {
        final byte[][] ps = new byte[channels.length][];
        for (int i = 0; i < ps.length; i++) {
            ps[i] = SafeEncoder.encode(channels[i]);
        }
        jRedisBinaryPubSub.unsubscribe(ps);
    }


    /**
     *
     * 通常为了适应大多数场景  还是使用这方式订阅吧
     *
     * 表达式的方式订阅
     * 使用模式匹配的方式设置要订阅的消息            订阅得到信息在BinaryJedisPubSub的onMessage(...)方法中进行处理
     *
     * @param patterns
     */
    @Override
    public void psubscribe(final  String... patterns) {
        BinaryJedis binaryJedis = null;
        try {
            binaryJedis = jRedisPool.getResource();
            final byte[][] ps = new byte[patterns.length][];
            for (int i = 0; i < ps.length; i++) {
                ps[i] = SafeEncoder.encode(patterns[i]);
            }
            binaryJedis.psubscribe(jRedisBinaryPubSub,ps);
        } catch (Exception ex) {
            coverException(ex, jRedisPool, binaryJedis);
        } finally {
            if (binaryJedis != null) {
                jRedisPool.returnResource(binaryJedis);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("close redis connection-{" + binaryJedis.toString() + "}");
                }
            }
        }
    }

    /**
     * 取消所有订阅的channel
     */
    @Override
    public void punsubscribe() {
        jRedisBinaryPubSub.punsubscribe();
    }


    /**
     * 取消订阅的channel
     *
     * @param patterns
     */
    @Override
    public void punsubscribe(String... patterns) {
        BinaryJedis binaryJedis = null;
        try {
            binaryJedis = jRedisPool.getResource();
            final byte[][] ps = new byte[patterns.length][];
            for (int i = 0; i < ps.length; i++) {
                ps[i] = SafeEncoder.encode(patterns[i]);
            }
            jRedisBinaryPubSub.punsubscribe(ps);
        } catch (Exception ex) {
            coverException(ex, jRedisPool, binaryJedis);
        } finally {
            if (binaryJedis != null) {
                jRedisPool.returnResource(binaryJedis);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("close redis connection-{" + binaryJedis.toString() + "}");
                }
            }
        }
    }


    /**
     * 获取 redis information
     *
     * @return
     */
    @Override
    public String info() {
        BinaryJedis binaryJedis = null;
        try {
            binaryJedis = jRedisPool.getResource();
            LOGGER.info(binaryJedis.toString());
            return binaryJedis.info();
        } catch (Exception ex) {
            coverException(ex, jRedisPool, binaryJedis);
        } finally {
            if (binaryJedis != null) {
                jRedisPool.returnResource(binaryJedis);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("close redis connection-{" + binaryJedis.toString() + "}");
                }
            }
        }
        return null;
    }

//    /**
//     * @param key
//     * @return
//     * @throws JRedisCacheException
//     */
//    @Override
//    public ArrayList<?> getList(String key)  {
//        BinaryJedis binaryJedis=null;
//        try {
//            binaryJedis=jRedisPool.getResource();
//            byte[] objectByte=binaryJedis.get(key.getBytes());
//            return (ArrayList<?>) JRedisSerializationUtils.kryoDeserialize(objectByte);
//        }catch (Exception ex){
//            coverException(ex,jRedisPool,binaryJedis);
//        }finally {
//            if (binaryJedis!=null){
//                    jRedisPool.returnResource(binaryJedis);
//            }
//
//        }
//        return null;
//    }

    /**
     * @param key
     * @return
     * @throws JRedisCacheException
     */
    @Override
    public ArrayList<?> getList(String key) {
        BinaryJedis binaryJedis = null;
        try {
            binaryJedis = jRedisPool.getResource();
            byte[] objectByte = binaryJedis.get(key.getBytes());
            return (ArrayList<?>) JRedisSerializationUtils.fastDeserialize(objectByte);
        } catch (Exception ex) {
            coverException(ex, jRedisPool, binaryJedis);
        } finally {
            if (binaryJedis != null) {
                jRedisPool.returnResource(binaryJedis);
            }

        }
        return null;
    }


//    /**
//     * @param key
//     * @param list
//     */
//    @Override
//    public void putList(String key, ArrayList<?> list)  {
//        BinaryJedis binaryJedis=null;
//        try {
//            binaryJedis=jRedisPool.getResource();
//            binaryJedis.set(key.getBytes(), JRedisSerializationUtils.kryoSerialize(list));
//        }catch (Exception ex){
//            coverException(ex,jRedisPool,binaryJedis);
//        } finally {
//            if (binaryJedis!=null){
//                jRedisPool.returnResource(binaryJedis);
//            }
//        }
//    }


    /**
     * @param key
     * @param list
     */
    @Override
    public String putList(String key, ArrayList<?> list) {
        BinaryJedis binaryJedis = null;
        try {
            binaryJedis = jRedisPool.getResource();
            return binaryJedis.set(key.getBytes(), JRedisSerializationUtils.fastSerialize(list));
        } catch (Exception ex) {
            coverException(ex, jRedisPool, binaryJedis);
            return "failed";
        } finally {
            if (binaryJedis != null) {
                jRedisPool.returnResource(binaryJedis);
            }
        }
    }


    /**
     * @param key
     * @return
     * @throws JRedisCacheException
     */
    @Override
    public FastTable<?> getFastTable(String key) {
        BinaryJedis binaryJedis = null;
        try {
            binaryJedis = jRedisPool.getResource();
            return (FastTable<?>) JRedisSerializationUtils.kryoDeserialize(binaryJedis.get(key.getBytes()));
        } catch (Exception ex) {
            coverException(ex, jRedisPool, binaryJedis);
        } finally {
            if (binaryJedis != null) {
                jRedisPool.returnResource(binaryJedis);
            }
        }
        return null;
    }


    /**
     * @param key
     * @param list
     */
    @Override
    public void putFastTable(String key, FastTable<?> list) {
        BinaryJedis binaryJedis = null;
        try {
            binaryJedis = jRedisPool.getResource();
            binaryJedis.set(key.getBytes(), JRedisSerializationUtils.kryoSerialize(list));
        } catch (Exception ex) {
            coverException(ex, jRedisPool, binaryJedis);
        } finally {
            if (binaryJedis != null) {
                jRedisPool.returnResource(binaryJedis);
            }
        }
    }


    /**
     * Remove an item from the cache
     *
     * @param key
     */
    @Override
    public void removeList(String key) {
        BinaryJedis binaryJedis = null;
        try {
            binaryJedis = jRedisPool.getResource();
            binaryJedis.del(key.getBytes());
        } catch (Exception ex) {
            coverException(ex, jRedisPool, binaryJedis);
        } finally {
            if (binaryJedis != null) {
                jRedisPool.returnResource(binaryJedis);
            }
        }
    }


    /**
     * @param key
     * @param fastMap
     */
    @Override
    public void putFastMap(String key, FastMap<?, ?> fastMap) {
        BinaryJedis binaryJedis = null;
        try {
            binaryJedis = jRedisPool.getResource();
            binaryJedis.set(key.getBytes(), JRedisSerializationUtils.kryoSerialize(fastMap));
        } catch (Exception ex) {
            coverException(ex, jRedisPool, binaryJedis);
        } finally {
            if (binaryJedis != null) {
                jRedisPool.returnResource(binaryJedis);
            }
        }
    }


    /**
     * @param key
     * @return
     * @throws JRedisCacheException
     */
    @Override
    public FastMap<?, ?> getFastMap(String key) {
        BinaryJedis binaryJedis = null;
        try {
            binaryJedis = jRedisPool.getResource();
            return (FastMap<?, ?>) JRedisSerializationUtils.kryoDeserialize(binaryJedis.get(key.getBytes()));
        } catch (Exception ex) {
            coverException(ex, jRedisPool, binaryJedis);
        } finally {
            if (binaryJedis != null) {
                jRedisPool.returnResource(binaryJedis);
            }
        }
        return null;
    }

    /**
     * Remove an item from the cache
     *
     * @param key
     */
    @Override
    public void removeFastMap(String key) {
        BinaryJedis binaryJedis = null;
        try {
            binaryJedis = jRedisPool.getResource();
            binaryJedis.del(key.getBytes());
        } catch (Exception ex) {
            coverException(ex, jRedisPool, binaryJedis);
        } finally {
            if (binaryJedis != null) {
                jRedisPool.returnResource(binaryJedis);
            }
        }
    }

    /**
     * Get an item from the cache, nontransactionally
     *
     * @param key
     * @return the cached object or <tt>null</tt>
     * @throws JRedisCacheException
     */
    @Override
    public Serializable getObject(String key) {
        BinaryJedis binaryJedis = null;
        try {
            binaryJedis = jRedisPool.getResource();
            return (Serializable) JRedisSerializationUtils.kryoDeserialize(binaryJedis.get(key.getBytes()));
        } catch (Exception ex) {
            coverException(ex, jRedisPool, binaryJedis);
        } finally {
            if (binaryJedis != null) {
                jRedisPool.returnResource(binaryJedis);
            }
        }
        return null;
    }

    /**
     * Add an item to the cache, nontransactionally, with
     * failfast semantics
     *
     * @param key
     * @param value
     * @throws JRedisCacheException
     */
    @Override
    public void putObject(String key, Serializable value) {
        BinaryJedis binaryJedis = null;
        try {
            binaryJedis = jRedisPool.getResource();
            binaryJedis.set(key.getBytes(), JRedisSerializationUtils.kryoSerialize(value));
        } catch (Exception ex) {
            coverException(ex, jRedisPool, binaryJedis);
        } finally {
            if (binaryJedis != null) {
                jRedisPool.returnResource(binaryJedis);
            }
        }
    }

    /**
     * Remove an item from the cache
     *
     * @param key
     */
    @Override
    public void removeObject(String key) {
        BinaryJedis binaryJedis = null;
        try {
            binaryJedis = jRedisPool.getResource();
            binaryJedis.del(key.getBytes());
        } catch (Exception ex) {
            coverException(ex, jRedisPool, binaryJedis);
        } finally {
            if (binaryJedis != null) {
                jRedisPool.returnResource(binaryJedis);
            }
        }
    }

    @Override
    public FastTable<String> keys() {
        BinaryJedis binaryJedis = null;
        try {
            binaryJedis = jRedisPool.getResource();
            FastTable<String> keys = new FastTable<String>();
            Set<byte[]> list = binaryJedis.keys(String.valueOf("*").getBytes());
            for (byte[] bs : list) {
                keys.addLast(bs == null ? null : (String) JRedisSerializationUtils.kryoDeserialize(bs));
            }
            return keys;
        } catch (Exception ex) {
            coverException(ex, jRedisPool, binaryJedis);
        } finally {
            if (binaryJedis != null) {
                jRedisPool.returnResource(binaryJedis);
            }
        }
        return null;
    }

    /**
     *
     */
    @Override
    public void destroy() {
        FastTable<String> keys = keys();
        BinaryJedis binaryJedis = null;
        try {
            binaryJedis = jRedisPool.getResource();
            for (String key : keys) {
                //After the timeout the key will be
                // automatically deleted by the server.
                binaryJedis.expire(key.getBytes(), 0);
            }
        } catch (Exception ex) {
            coverException(ex, jRedisPool, binaryJedis);
        } finally {
            if (binaryJedis != null) {
                jRedisPool.returnResource(binaryJedis);
            }
        }
    }

    /**
     * Queue
     *
     * @param key
     * @param value
     */
    @Override
    public void addQueue(String key, Serializable value) {
        BinaryJedis binaryJedis = null;
        try {
            binaryJedis = jRedisPool.getResource();
            binaryJedis.lpush(key.getBytes(), JRedisSerializationUtils.kryoSerialize(value));
        } catch (Exception ex) {
            coverException(ex, jRedisPool, binaryJedis);
        } finally {
            if (binaryJedis != null) {
                jRedisPool.returnResource(binaryJedis);
            }
        }
    }

    /**
     * poll  from queue
     *
     * @param key
     */
    @Override
    public Serializable pollFromQueue(String key) {
        BinaryJedis binaryJedis = null;
        try {
            binaryJedis = jRedisPool.getResource();
            return (Serializable) JRedisSerializationUtils.kryoDeserialize(binaryJedis.rpop(key.getBytes()));
        } catch (Exception ex) {
            coverException(ex, jRedisPool, binaryJedis);
            return null;             // if exception  return null ;
        } finally {
            if (binaryJedis != null) {
                jRedisPool.returnResource(binaryJedis);
            }
        }
    }

    /**
     * peek  from queue
     *
     * @param key
     */
    @Override
    public Serializable peekFromQueue(String key) {
        BinaryJedis binaryJedis = null;
        Serializable obj = null;
        try {
            binaryJedis = jRedisPool.getResource();
            byte[] keyBytes = key.getBytes();
            obj = (Serializable) JRedisSerializationUtils.kryoDeserialize(binaryJedis.rpop(keyBytes));
            //return to queue
            binaryJedis.lpush(keyBytes, JRedisSerializationUtils.kryoSerialize(obj));
        } catch (Exception ex) {
            coverException(ex, jRedisPool, binaryJedis);
        } finally {
            if (binaryJedis != null) {
                jRedisPool.returnResource(binaryJedis);
            }
        }
        return obj;
    }
}
