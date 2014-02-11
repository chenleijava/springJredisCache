package springJredisCache;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.Protocol;
import redis.clients.util.Pool;


/**
 * @author 石头哥哥</br>
 *         Date:1/7/14</br>
 *         Time:9:55 PM</br>
 *         Package:com.dc.gameserver.extComponents.jredisCache</br>
 *         Comment：    JRedisCache 对象池    ，池化对象 BinaryJedis
 *         BinaryJedis 持有socket对象
 */
public class JRedisPool  extends Pool<BinaryJedis> {

    public JRedisPool(String host, int port) {
        this(new GenericObjectPool.Config(), host, port, Protocol.DEFAULT_TIMEOUT, null, Protocol.DEFAULT_DATABASE);
    }

    public JRedisPool(final String host) {
        this(host, Protocol.DEFAULT_PORT);
    }

    public JRedisPool(final GenericObjectPool.Config poolConfig, final String host) {
        this(poolConfig, host, Protocol.DEFAULT_PORT, Protocol.DEFAULT_TIMEOUT, null, Protocol.DEFAULT_DATABASE);
    }

    public JRedisPool(final GenericObjectPool.Config poolConfig, final String host, int port,
                     int timeout, final String password) {
        this(poolConfig, host, port, timeout, password, Protocol.DEFAULT_DATABASE);
    }

    public JRedisPool(final GenericObjectPool.Config poolConfig, final String host, final int port) {
        this(poolConfig, host, port, Protocol.DEFAULT_TIMEOUT, null, Protocol.DEFAULT_DATABASE);
    }

    public JRedisPool(final GenericObjectPool.Config poolConfig, final String host, final int port, final int timeout) {
        this(poolConfig, host, port, timeout, null, Protocol.DEFAULT_DATABASE);
    }

    public JRedisPool(final GenericObjectPool.Config poolConfig, final String host, int port, int timeout, final String password,
                     final int database) {
        super(poolConfig, new BinaryJedisFactory(host, port, timeout, password, database));
    }


    public void returnBrokenResource(final BinaryJedis resource) {
        returnBrokenResourceObject(resource);
    }

    public void returnResource(final BinaryJedis resource) {
        returnResourceObject(resource);
    }


    /**
     * 将对象 BinaryJedis 池化
     */
    private static class BinaryJedisFactory extends BasePoolableObjectFactory {
        private final String host;
        private final int port;
        private final int timeout;
        private final String password;
        private final int database;

        /**
         *
         * @param host
         * @param port
         * @param timeout      socket超时时间
         * @param password
         * @param database
         */
        public BinaryJedisFactory(final String host, final int port,
                            final int timeout, final String password, final int database) {
            super();
            this.host = host;
            this.port = port;
            this.timeout = timeout;
            this.password = password;
            this.database = database;
        }

        /**
         * 对象池中不存在 BinaryJedis，那么就会创建一个对象公app使用，归还的时候将返回给池
         * @return
         * @throws Exception
         */
        public Object makeObject() throws Exception {
            final BinaryJedis binaryJedis = new BinaryJedis(this.host, this.port, this.timeout);

            binaryJedis.connect();
            if (null != this.password) {
                binaryJedis.auth(this.password);
            }
            if( database != 0 ) {
                binaryJedis.select(database);
            }
            return binaryJedis;
        }

        /**
         * 销毁BinaryJedis对象同时关闭socket链接资源
         * @param obj
         * @throws Exception
         */
        public void destroyObject(final Object obj) throws Exception {
            if (obj instanceof BinaryJedis) {
                final BinaryJedis binaryJedis = (BinaryJedis) obj;
                if (binaryJedis.isConnected()) {
                    try {
                        try {
                            binaryJedis.quit();
                        } catch (Exception e) {
                        }
                        binaryJedis.disconnect();
                    } catch (Exception e) {

                    }
                }
            }
        }

        /**
         * 验证 BinaryJedis是否有效
         * @param obj
         * @return
         */
        public boolean validateObject(final Object obj) {
            if (obj instanceof BinaryJedis) {
                final BinaryJedis binaryJedis = (BinaryJedis) obj;
                try {
                    return binaryJedis.isConnected() && binaryJedis.ping().equals("PONG");
                } catch (final Exception e) {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

}
