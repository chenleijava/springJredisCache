package springJredisCache;


import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.Protocol;
import redis.clients.util.Pool;

import java.net.URI;

/**
 * @author 石头哥哥</br>
 *         Date:1/7/14</br>
 *         Time:9:55 PM</br>
 *         Package:com.dc.gameserver.extComponents.jredisCache</br>
 *         Comment：    JRedisCache 对象池    ，池化对象 BinaryJedis
 *         BinaryJedis 持有socket对象
 */
public class JRedisPool extends Pool<BinaryJedis> {

    public JRedisPool(final GenericObjectPoolConfig poolConfig, final String host) {
        this(poolConfig, host, Protocol.DEFAULT_PORT, Protocol.DEFAULT_TIMEOUT,
                null, Protocol.DEFAULT_DATABASE, null);
    }

    public JRedisPool(String host, int port) {
        this(new GenericObjectPoolConfig(), host, port,
                Protocol.DEFAULT_TIMEOUT, null, Protocol.DEFAULT_DATABASE, null);
    }

    public JRedisPool(final String host) {
        URI uri = URI.create(host);
        if (uri.getScheme() != null && uri.getScheme().equals("redis")) {
            String h = uri.getHost();
            int port = uri.getPort();
            String password = uri.getUserInfo().split(":", 2)[1];
            int database = Integer.parseInt(uri.getPath().split("/", 2)[1]);
            this.internalPool = new GenericObjectPool<BinaryJedis>(
                    new BinaryJedisFactory(h, port, Protocol.DEFAULT_TIMEOUT,
                            password, database, null),
                    new GenericObjectPoolConfig());
        } else {
            this.internalPool = new GenericObjectPool<BinaryJedis>(new BinaryJedisFactory(
                    host, Protocol.DEFAULT_PORT, Protocol.DEFAULT_TIMEOUT,
                    null, Protocol.DEFAULT_DATABASE, null),
                    new GenericObjectPoolConfig());
        }
    }

    public JRedisPool(final URI uri) {
        String h = uri.getHost();
        int port = uri.getPort();
        String password = uri.getUserInfo().split(":", 2)[1];
        int database = Integer.parseInt(uri.getPath().split("/", 2)[1]);
        this.internalPool = new GenericObjectPool<BinaryJedis>(new BinaryJedisFactory(h,
                port, Protocol.DEFAULT_TIMEOUT, password, database, null),
                new GenericObjectPoolConfig());
    }

    public JRedisPool(final GenericObjectPoolConfig poolConfig,
                     final String host, int port, int timeout, final String password) {
        this(poolConfig, host, port, timeout, password,
                Protocol.DEFAULT_DATABASE, null);
    }

    public JRedisPool(final GenericObjectPoolConfig poolConfig,
                     final String host, final int port) {
        this(poolConfig, host, port, Protocol.DEFAULT_TIMEOUT, null,
                Protocol.DEFAULT_DATABASE, null);
    }

    public JRedisPool(final GenericObjectPoolConfig poolConfig,
                     final String host, final int port, final int timeout) {
        this(poolConfig, host, port, timeout, null, Protocol.DEFAULT_DATABASE,
                null);
    }

    public JRedisPool(final GenericObjectPoolConfig poolConfig,
                     final String host, int port, int timeout, final String password,
                     final int database) {
        this(poolConfig, host, port, timeout, password, database, null);
    }

    public JRedisPool(final GenericObjectPoolConfig poolConfig,
                     final String host, int port, int timeout, final String password,
                     final int database, final String clientName) {
        super(poolConfig, new BinaryJedisFactory(host, port, timeout, password,
                database, clientName));
    }

    public void returnBrokenResource(final BinaryJedis binaryJedis) {
        returnBrokenResourceObject(binaryJedis);
    }

    public void returnResource(final BinaryJedis resource) {
        resource.resetState();
        returnResourceObject(resource);
    }

    /**
     * 将对象 BinaryJedis 池化
     */
    private static class BinaryJedisFactory implements PooledObjectFactory<BinaryJedis> {

        private final String host;
        private final int port;
        private final int timeout;
        private final String password;
        private final int database;
        private final String clientName;

        public BinaryJedisFactory(final String host, final int port, final int timeout,
                            final String password, final int database) {
            this(host, port, timeout, password, database, null);
        }
        public BinaryJedisFactory(final String host, final int port, final int timeout,
                            final String password, final int database, final String clientName) {
            super();
            this.host = host;
            this.port = port;
            this.timeout = timeout;
            this.password = password;
            this.database = database;
            this.clientName = clientName;
        }

        @Override
        public void activateObject(PooledObject<BinaryJedis> pooledJedis)
                throws Exception {
            final BinaryJedis binaryJedis = pooledJedis.getObject();
            if (binaryJedis.getDB() != database) {
                binaryJedis.select(database);
            }
        }

        /**
         *  BinaryJedis binaryJedis  was removed pool,and close client   socket
         * @param pooledJedis
         * @throws Exception
         */
        @Override
        public void destroyObject(PooledObject<BinaryJedis> pooledJedis) throws Exception {
            final BinaryJedis binaryJedis = pooledJedis.getObject();
            if (binaryJedis.isConnected()) {
                try {
                    try {
                        binaryJedis.quit();
                    } catch (Exception e) {
                        //It seems like server has closed the connection
                    }
                    binaryJedis.disconnect();
                } catch (Exception e) {
                    //IOException
                }
            }
        }


        /**
         * 创建连接对象 并将该对象池化
         * @return
         * @throws Exception
         */
        @Override
        public PooledObject<BinaryJedis> makeObject() throws Exception {
            final BinaryJedis binaryJedis = new BinaryJedis(this.host, this.port, this.timeout);
            binaryJedis.connect();
            if (null != this.password) {
                binaryJedis.auth(this.password);
            }
            if (database != 0) {
                binaryJedis.select(database);
            }
            if (clientName != null) {
                binaryJedis.clientSetname(clientName.getBytes());
            }
            return new DefaultPooledObject<BinaryJedis>(binaryJedis);
        }

        @Override
        public void passivateObject(PooledObject<BinaryJedis> pooledJedis)
                throws Exception {
            // TODO maybe should select db 0? Not sure right now.
        }

        /**
         * 验证对象是否有效
         * @param pooledJedis
         * @return
         */
        @Override
        public boolean validateObject(PooledObject<BinaryJedis> pooledJedis) {
            final BinaryJedis binaryJedis = pooledJedis.getObject();
            try {
                return binaryJedis.isConnected() && binaryJedis.ping().equals("PONG");
            } catch (final Exception e) {
                return false;
            }
        }
    }

}
