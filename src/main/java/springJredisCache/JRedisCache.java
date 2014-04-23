package springJredisCache;

import javolution.util.FastMap;
import javolution.util.FastTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
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
 *         运行时异常，IO异常，销毁jedis对象
 *         spring- data redis中，
 *         open-->>getResource()
 *         向对象池借用 jedis对象；
 *         close--->returnBrokenResource(jedis) or returnResource(jedis)
 *         如果出现异常（runntime ，io），那么将销毁 jedis对象，否则将其归还到对象池；
 */
@Service
public class JRedisCache implements JCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(JRedisCache.class);

    @Resource
    private JedisPool jedisPool;

    @Resource
    private JRedisBinaryPubSub jRedisBinaryPubSub;

    @Resource
    private ShardedJedisPool shardedJedisPool;

    //客户端集群
    private static final boolean redisShared=false;

    /**
     * 运行时异常，IO异常，销毁jedis对象
     *
     * @param ex
     * @param jedisPool
     * @param jedis
     */
    protected void coverException(Exception ex, JedisPool jedisPool, Jedis jedis) {
        if (jedis == null) throw new NullPointerException();
        if (ex instanceof JRedisCacheException || ex instanceof IOException) {
            jedisPool.returnBrokenResource(jedis); //销毁该对象
        }
    }

    /**
     * 运行时异常，IO异常，销毁jedis对象
     *
     * @param ex
     * @param shardedJedisPool
     * @param shardedJedis
     */
    protected void coverShardJedisException(Exception ex, ShardedJedisPool shardedJedisPool, ShardedJedis shardedJedis) {
        if (shardedJedis == null) throw new NullPointerException();
        if (ex instanceof JRedisCacheException || ex instanceof IOException) {
            shardedJedisPool.returnBrokenResource(shardedJedis); //销毁该对象
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
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.publish(channel.getBytes(), message);
        } catch (Exception ex) {
            coverException(ex, jedisPool, jedis);
            return null;
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("close redis connection-{" + jedis.toString() + "}");
                }
            }
        }
    }

    /**
     * 订阅指定的消息        订阅得到信息在JedisPubSub的onMessage(...)方法中进行处理
     *
     * @param channels
     */
    @Override
    public void subscribe(final String... channels) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            final byte[][] ps = new byte[channels.length][];
            for (int i = 0; i < ps.length; i++) {
                ps[i] = SafeEncoder.encode(channels[i]);
            }
            jedis.subscribe(jRedisBinaryPubSub, ps);
        } catch (Exception ex) {
            coverException(ex, jedisPool, jedis);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("close redis connection-{" + jedis.toString() + "}");
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
     * 通常为了适应大多数场景  还是使用这方式订阅吧
     * <p/>
     * 表达式的方式订阅
     * 使用模式匹配的方式设置要订阅的消息            订阅得到信息在JedisPubSub的onMessage(...)方法中进行处理
     *
     * @param patterns
     */
    @Override
    public void psubscribe(final String... patterns) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            final byte[][] ps = new byte[patterns.length][];
            for (int i = 0; i < ps.length; i++) {
                ps[i] = SafeEncoder.encode(patterns[i]);
            }
            jedis.psubscribe(jRedisBinaryPubSub, ps);
        } catch (Exception ex) {
            coverException(ex, jedisPool, jedis);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("close redis connection-{" + jedis.toString() + "}");
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
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            final byte[][] ps = new byte[patterns.length][];
            for (int i = 0; i < ps.length; i++) {
                ps[i] = SafeEncoder.encode(patterns[i]);
            }
            jRedisBinaryPubSub.punsubscribe(ps);
        } catch (Exception ex) {
            coverException(ex, jedisPool, jedis);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("close redis connection-{" + jedis.toString() + "}");
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
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.info();
        } catch (Exception ex) {
            coverException(ex, jedisPool, jedis);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("close redis connection-{" + jedis.toString() + "}");
                }
            }
        }
        return null;
    }

    @Override
    public ArrayList<?> getList(String key, String filed) {
        if (redisShared) {
            ShardedJedis shardedJedis = null;
            try {
                shardedJedis = shardedJedisPool.getResource();
                byte[] objectByte = shardedJedis.get(key.getBytes());
                return (ArrayList<?>) JRedisSerializationUtils.fastDeserialize(objectByte);
            } catch (Exception ex) {
                coverShardJedisException(ex, shardedJedisPool, shardedJedis);
            } finally {
                if (shardedJedisPool != null) {
                    shardedJedisPool.returnResource(shardedJedis);
                }
            }
            return null;
        } else {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                byte[] objectByte = jedis.hget(key.getBytes(), filed.getBytes());
                return (ArrayList<?>) JRedisSerializationUtils.fastDeserialize(objectByte);
            } catch (Exception ex) {
                coverException(ex, jedisPool, jedis);
            } finally {
                if (jedis != null) {
                    jedisPool.returnResource(jedis);
                }

            }
            return null;
        }
    }

    @Override
    public String putList(String key, String filed, ArrayList<?> list) {
        if (redisShared) {
            ShardedJedis shardedJedis = null;
            try {
                shardedJedis = shardedJedisPool.getResource();
                return shardedJedis.set(key.getBytes(), JRedisSerializationUtils.fastSerialize(list));
            } catch (Exception ex) {
                coverShardJedisException(ex, shardedJedisPool, shardedJedis);
                return "failed";
            } finally {
                if (shardedJedis != null) {
                    shardedJedisPool.returnResource(shardedJedis);
                }
            }
        } else {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                return String.valueOf(jedis.hset(key.getBytes(), filed.getBytes(), JRedisSerializationUtils.fastSerialize(list)));
            } catch (Exception ex) {
                coverException(ex, jedisPool, jedis);
                return "failed";
            } finally {
                if (jedis != null) {
                    jedisPool.returnResource(jedis);
                }
            }
        }
    }

    /**
     * @param key
     * @return
     * @throws JRedisCacheException
     */
    @Override
    public ArrayList<?> getList(String key) {
        if (redisShared) {
            ShardedJedis shardedJedis = null;
            try {
                shardedJedis = shardedJedisPool.getResource();
                byte[] objectByte = shardedJedis.get(key.getBytes());
                return (ArrayList<?>) JRedisSerializationUtils.fastDeserialize(objectByte);
            } catch (Exception ex) {
                coverShardJedisException(ex, shardedJedisPool, shardedJedis);
            } finally {
                if (shardedJedisPool != null) {
                    shardedJedisPool.returnResource(shardedJedis);
                }
            }
            return null;
        } else {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                byte[] objectByte = jedis.get(key.getBytes());
                return (ArrayList<?>) JRedisSerializationUtils.fastDeserialize(objectByte);
            } catch (Exception ex) {
                coverException(ex, jedisPool, jedis);
            } finally {
                if (jedis != null) {
                    jedisPool.returnResource(jedis);
                }

            }
            return null;
        }
    }


    /**
     * @param key
     * @param list
     */
    @Override
    public String putList(String key, ArrayList<?> list) {
        if (redisShared) {
            ShardedJedis shardedJedis = null;
            try {
                shardedJedis = shardedJedisPool.getResource();
                return shardedJedis.set(key.getBytes(), JRedisSerializationUtils.fastSerialize(list));
            } catch (Exception ex) {
                coverShardJedisException(ex, shardedJedisPool, shardedJedis);
                return "failed";
            } finally {
                if (shardedJedis != null) {
                    shardedJedisPool.returnResource(shardedJedis);
                }
            }
        } else {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                return jedis.set(key.getBytes(), JRedisSerializationUtils.fastSerialize(list));
            } catch (Exception ex) {
                coverException(ex, jedisPool, jedis);
                return "failed";
            } finally {
                if (jedis != null) {
                    jedisPool.returnResource(jedis);
                }
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
        if (redisShared) {
            ShardedJedis shardedJedis = null;
            try {
                shardedJedis = shardedJedisPool.getResource();
                return (FastTable<?>) JRedisSerializationUtils.fastDeserialize(shardedJedis.get(key.getBytes()));
            } catch (Exception ex) {
                coverShardJedisException(ex, shardedJedisPool, shardedJedis);
            } finally {
                if (shardedJedis != null) {
                    shardedJedisPool.returnResource(shardedJedis);
                }
            }
            return null;
        } else {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                return (FastTable<?>) JRedisSerializationUtils.fastDeserialize(jedis.get(key.getBytes()));
            } catch (Exception ex) {
                coverException(ex, jedisPool, jedis);
            } finally {
                if (jedis != null) {
                    jedisPool.returnResource(jedis);
                }
            }
            return null;
        }
    }


    /**
     * @param key
     * @param list
     */
    @Override
    public void putFastTable(String key, FastTable<?> list) {
        if (redisShared) {
            ShardedJedis shardedJedis = null;
            try {
                shardedJedis = shardedJedisPool.getResource();
                shardedJedis.set(key.getBytes(), JRedisSerializationUtils.fastSerialize(list));
            } catch (Exception ex) {
                coverShardJedisException(ex, shardedJedisPool, shardedJedis);
            } finally {
                if (shardedJedis != null) {
                    shardedJedisPool.returnResource(shardedJedis);
                }
            }
        } else {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                jedis.set(key.getBytes(), JRedisSerializationUtils.fastSerialize(list));
            } catch (Exception ex) {
                coverException(ex, jedisPool, jedis);
            } finally {
                if (jedis != null) {
                    jedisPool.returnResource(jedis);
                }
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
        if (redisShared) {
            ShardedJedis shardedJedis = null;
            try {
                shardedJedis = shardedJedisPool.getResource();
                shardedJedis.del(key.getBytes());
            } catch (Exception ex) {
                coverShardJedisException(ex, shardedJedisPool, shardedJedis);
            } finally {
                if (shardedJedis != null) {
                    shardedJedisPool.returnResource(shardedJedis);
                }
            }
        } else {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                jedis.del(key.getBytes());
            } catch (Exception ex) {
                coverException(ex, jedisPool, jedis);
            } finally {
                if (jedis != null) {
                    jedisPool.returnResource(jedis);
                }
            }
        }
    }


    /**
     * @param key
     * @param fastMap
     */
    @Override
    public void putFastMap(String key, FastMap<?, ?> fastMap) {
        if (redisShared) {
            ShardedJedis shardedJedis = null;
            try {
                shardedJedis = shardedJedisPool.getResource();
                shardedJedis.set(key.getBytes(), JRedisSerializationUtils.fastSerialize(fastMap));
            } catch (Exception ex) {
                coverShardJedisException(ex, shardedJedisPool, shardedJedis);
            } finally {
                if (shardedJedis != null) {
                    shardedJedisPool.returnResource(shardedJedis);
                }
            }
        } else {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                jedis.set(key.getBytes(), JRedisSerializationUtils.fastSerialize(fastMap));
            } catch (Exception ex) {
                coverException(ex, jedisPool, jedis);
            } finally {
                if (jedis != null) {
                    jedisPool.returnResource(jedis);
                }
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
        if (redisShared) {
            ShardedJedis shardedJedis = null;
            try {
                shardedJedis = shardedJedisPool.getResource();
                return (FastMap<?, ?>) JRedisSerializationUtils.fastDeserialize(shardedJedis.get(key.getBytes()));
            } catch (Exception ex) {
                coverShardJedisException(ex, shardedJedisPool, shardedJedis);
            } finally {
                if (shardedJedis != null) {
                    shardedJedisPool.returnResource(shardedJedis);
                }
            }
            return null;
        } else {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                return (FastMap<?, ?>) JRedisSerializationUtils.fastDeserialize(jedis.get(key.getBytes()));
            } catch (Exception ex) {
                coverException(ex, jedisPool, jedis);
            } finally {
                if (jedis != null) {
                    jedisPool.returnResource(jedis);
                }
            }
            return null;
        }
    }


    /**
     * Remove an item from the cache
     *
     * @param key
     */
    @Override
    public void removeFastMap(String key) {
        if (redisShared) {
            ShardedJedis shardedJedis = null;
            try {
                shardedJedis = shardedJedisPool.getResource();
                shardedJedis.del(key.getBytes());
            } catch (Exception ex) {
                coverShardJedisException(ex, shardedJedisPool, shardedJedis);
            } finally {
                if (shardedJedis != null) {
                    shardedJedisPool.returnResource(shardedJedis);
                }
            }
        } else {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                jedis.del(key.getBytes());
            } catch (Exception ex) {
                coverException(ex, jedisPool, jedis);
            } finally {
                if (jedis != null) {
                    jedisPool.returnResource(jedis);
                }
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
        if (redisShared) {
            ShardedJedis shardedJedis = null;
            try {
                shardedJedis = shardedJedisPool.getResource();
                return (Serializable) JRedisSerializationUtils.fastDeserialize(shardedJedis.get(key.getBytes()));
            } catch (Exception ex) {
                coverShardJedisException(ex, shardedJedisPool, shardedJedis);
            } finally {
                if (shardedJedis != null) {
                    shardedJedisPool.returnResource(shardedJedis);
                }
            }
            return null;
        } else {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                return (Serializable) JRedisSerializationUtils.fastDeserialize(jedis.get(key.getBytes()));
            } catch (Exception ex) {
                coverException(ex, jedisPool, jedis);
            } finally {
                if (jedis != null) {
                    jedisPool.returnResource(jedis);
                }
            }
            return null;
        }
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
        if (redisShared) {
            ShardedJedis shardedJedis = null;
            try {
                shardedJedis = shardedJedisPool.getResource();
                shardedJedis.set(key.getBytes(), JRedisSerializationUtils.fastSerialize(value));
            } catch (Exception ex) {
                coverShardJedisException(ex, shardedJedisPool, shardedJedis);
            } finally {
                if (shardedJedis != null) {
                    shardedJedisPool.returnResource(shardedJedis);
                }
            }
        } else {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                jedis.set(key.getBytes(), JRedisSerializationUtils.fastSerialize(value));
            } catch (Exception ex) {
                coverException(ex, jedisPool, jedis);
            } finally {
                if (jedis != null) {
                    jedisPool.returnResource(jedis);
                }
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
        if (redisShared) {
            ShardedJedis shardedJedis = null;
            try {
                shardedJedis = shardedJedisPool.getResource();
                shardedJedis.del(key.getBytes());
            } catch (Exception ex) {
                coverShardJedisException(ex, shardedJedisPool, shardedJedis);
            } finally {
                if (shardedJedis != null) {
                    shardedJedisPool.returnResource(shardedJedis);
                }
            }
        } else {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                jedis.del(key.getBytes());
            } catch (Exception ex) {
                coverException(ex, jedisPool, jedis);
            } finally {
                if (jedis != null) {
                    jedisPool.returnResource(jedis);
                }
            }
        }
    }


    @Override
    public FastTable<String> keys() {
        if (redisShared) {
            ShardedJedis shardedJedis = null;
            try {
                shardedJedis = shardedJedisPool.getResource();
                FastTable<String> keys = new FastTable<String>();
                Set<byte[]> list = shardedJedis.hkeys(String.valueOf("*").getBytes());
                for (byte[] bs : list) {
                    keys.addLast(bs == null ? null : (String) JRedisSerializationUtils.fastDeserialize(bs));
                }
                return keys;
            } catch (Exception ex) {
                coverShardJedisException(ex, shardedJedisPool, shardedJedis);
            } finally {
                if (shardedJedis != null) {
                    shardedJedisPool.returnResource(shardedJedis);
                }
            }
            return null;
        } else {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                FastTable<String> keys = new FastTable<String>();
                Set<byte[]> list = jedis.keys(String.valueOf("*").getBytes());
                for (byte[] bs : list) {
                    keys.addLast(bs == null ? null : (String) JRedisSerializationUtils.fastDeserialize(bs));
                }
                return keys;
            } catch (Exception ex) {
                coverException(ex, jedisPool, jedis);
            } finally {
                if (jedis != null) {
                    jedisPool.returnResource(jedis);
                }
            }
            return null;
        }
    }

    /**
     *
     */
    @Override
    public void destroy() {
        if (redisShared) {
            FastTable<String> keys = keys();
            ShardedJedis shardedJedis = null;
            try {
                shardedJedis = shardedJedisPool.getResource();
                for (String key : keys) {
                    //After the timeout the key will be
                    // automatically deleted by the server.
                    shardedJedis.expire(key.getBytes(), 0);
                }
            } catch (Exception ex) {
                coverShardJedisException(ex, shardedJedisPool, shardedJedis);
            } finally {
                if (shardedJedis != null) {
                    shardedJedisPool.returnResource(shardedJedis);
                }
            }
        } else {
            FastTable<String> keys = keys();
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                for (String key : keys) {
                    //After the timeout the key will be
                    // automatically deleted by the server.
                    jedis.expire(key.getBytes(), 0);
                }
            } catch (Exception ex) {
                coverException(ex, jedisPool, jedis);
            } finally {
                if (jedis != null) {
                    jedisPool.returnResource(jedis);
                }
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
        if (redisShared) {
            ShardedJedis shardedJedis = null;
            try {
                shardedJedis = shardedJedisPool.getResource();
                shardedJedis.lpush(key.getBytes(), JRedisSerializationUtils.fastSerialize(value));
            } catch (Exception ex) {
                coverShardJedisException(ex, shardedJedisPool, shardedJedis);
            } finally {
                if (shardedJedis != null) {
                    shardedJedisPool.returnResource(shardedJedis);
                }
            }
        } else {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                jedis.lpush(key.getBytes(), JRedisSerializationUtils.fastSerialize(value));
            } catch (Exception ex) {
                coverException(ex, jedisPool, jedis);
            } finally {
                if (jedis != null) {
                    jedisPool.returnResource(jedis);
                }
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
        if (redisShared) {
            ShardedJedis shardedJedis = null;
            try {
                shardedJedis = shardedJedisPool.getResource();
                return (Serializable) JRedisSerializationUtils.fastDeserialize(shardedJedis.rpop(key.getBytes()));
            } catch (Exception ex) {
                coverShardJedisException(ex, shardedJedisPool, shardedJedis);
                return null;             // if exception  return null ;
            } finally {
                if (shardedJedis != null) {
                    shardedJedisPool.returnResource(shardedJedis);
                }
            }
        } else {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                return (Serializable) JRedisSerializationUtils.fastDeserialize(jedis.rpop(key.getBytes()));
            } catch (Exception ex) {
                coverException(ex, jedisPool, jedis);
                return null;             // if exception  return null ;
            } finally {
                if (jedis != null) {
                    jedisPool.returnResource(jedis);
                }
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
        if (redisShared) {
            ShardedJedis shardedJedis = null;
            Serializable obj = null;
            try {
                shardedJedis = shardedJedisPool.getResource();
                byte[] keyBytes = key.getBytes();
                obj = (Serializable) JRedisSerializationUtils.fastDeserialize(shardedJedis.rpop(keyBytes));
                //return to queue
                shardedJedis.lpush(keyBytes, JRedisSerializationUtils.fastSerialize(obj));
            } catch (Exception ex) {
                coverShardJedisException(ex, shardedJedisPool, shardedJedis);
            } finally {
                if (shardedJedis != null) {
                    shardedJedisPool.returnResource(shardedJedis);
                }
            }
            return obj;
        } else {
            Jedis jedis = null;
            Serializable obj = null;
            try {
                jedis = jedisPool.getResource();
                byte[] keyBytes = key.getBytes();
                obj = (Serializable) JRedisSerializationUtils.fastDeserialize(jedis.rpop(keyBytes));
                //return to queue
                jedis.lpush(keyBytes, JRedisSerializationUtils.fastSerialize(obj));
            } catch (Exception ex) {
                coverException(ex, jedisPool, jedis);
            } finally {
                if (jedis != null) {
                    jedisPool.returnResource(jedis);
                }
            }
            return obj;
        }
    }
}
