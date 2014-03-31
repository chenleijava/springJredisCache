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
        if (binaryJedis!=null){
            returnBrokenResourceObject(binaryJedis);
        }
    }

    public void returnResource(final BinaryJedis resource) {
        if (resource!=null){
            resource.resetState();
            returnResourceObject(resource);
        }
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

        /**
         *
         * @param host
         * @param port
         * @param timeout
         * @param password
         * @param database
         */
        public BinaryJedisFactory(final String host, final int port, final int timeout,
                            final String password, final int database) {
            this(host, port, timeout, password, database, null);
        }

        /**
         *
         * @param host
         * @param port
         * @param timeout
         * @param password
         * @param database
         * @param clientName
         */
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

        /**
         *"激活"对象,当Pool中决定移除一个对象交付给调用者时额外的"激活"操作,
         * 比如可以在activateObject方法中"重置"参数列表让调用者使用时感觉像一个"新创建"的对象一样;如果object是一个线程
         * ,可以在"激活"操作中重置"线程中断标记",或者让线程从阻塞中唤醒等;如果object是一个socket,那么可以在"激活操作"中刷新通道
         * ,或者对socket进行链接重建(假如socket意外关闭)等.
         * @param pooledJedis
         * @throws Exception
         */
        @Override
        public void activateObject(PooledObject<BinaryJedis> pooledJedis)
                throws Exception {

            final BinaryJedis binaryJedis = pooledJedis.getObject();
            if (binaryJedis.getDB() != database) {
                binaryJedis.select(database);
            }

            //TODO always default database
        }


        /**
         * //"钝化"对象,当调用者"归还对象"时,Pool将会"钝化对象";
         * 钝化的言外之意,就是此"对象"暂且需要"休息"一下.
         * 如果object是一个socket,那么可以passivateObject中清除buffer,将socket阻塞;如果object是一个线程
         * ,可以在"钝化"操作中将线程sleep或者将线程中的某个对象wait.需要注意的时,activateObject和passivateObject两个方法需要对应
         * ,避免死锁或者"对象"状态的混乱.
         * @param pooledJedis
         * @throws Exception
         */
        @Override
        public void passivateObject(PooledObject<BinaryJedis> pooledJedis)
                throws Exception {
            // TODO maybe should select db 0? Not sure right now.
        }


        /**
         *  BinaryJedis binaryJedis  was removed pool,and close client   socket
         * 销毁对象,如果对象池中检测到某个"对象"idle的时间超时,或者操作者向对象池"归还对象"时检测到"对象"已经无效,那么此时将会导致"对象销毁";
         *  "销毁对象"的操作设计相差甚远,但是必须明确:当调用此方法时,"对象"的生命周期必须结束.如果object是线程,那么此时线程必须退出;
         *  如果object是socket操作,那么此时socket必须关闭;如果object是文件流操作,那么此时"数据flush"且正常关闭.
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


        /**
         检测对象是否"有效";Pool中不能保存无效的"对象",
         因此"后台检测线程"会周期性的检测Pool中"对象"的有效性,
         如果对象无效则会导致此对象从Pool中移除,并destroy;
         此外在调用者从Pool获取一个"对象"时,也会检测"对象"的有效性,
         确保不能讲"无效"的对象输出给调用者;当调用者使用完毕将"对象归还"到Pool时,
         仍然会检测对象的有效性.所谓有效性,就是此"对象"的状态是否符合预期,
         是否可以对调用者直接使用;如果对象是Socket,那么它的有效性就是socket的通道是否畅通/阻塞是否超时等.
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
