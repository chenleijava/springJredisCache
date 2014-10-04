/*
 * Copyright (c) 2014.  @石头哥哥
 * THIS SOFTWARE IS PROVIDED BY THE FREEBSD PROJECT ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE FREEBSD PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package springJredisCache;

import org.springframework.stereotype.Service;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.util.SafeEncoder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 石头哥哥 </br>
 *         springJredisCache </br>
 *         Date:2014/4/2 </br>
 *         Time:14:37 </br>
 *         Package:{@link springJredisCache}</br>
 *         Comment：  处理订阅的消息
 *         subsribe(一般模式设置频道)和psubsribe(使用模式匹配来设置频道)。不管是那种模式都可以设置个数不定的频道
 *         <p/>
 *         <p/>
 *         订阅的监听类  ，订阅某个事件
 */
@Service
public class JRedisBinaryPubSub extends BinaryJedisPubSub {

    // 处理订阅消息
    private static final ExecutorService handleSubscribe =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()
                    , new PriorityThreadFactory("@+订阅消息处理线程池+@", Thread.NORM_PRIORITY));


    /**
     * 处理订阅的消息
     *
     * @param channel
     * @param message
     */
    @Override
    public void onMessage(final byte[] channel, final byte[] message) {
        handleSubscribe.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println(SafeEncoder.encode(channel) + "=" + SafeEncoder.encode(message));
            }
        });
    }

    /**
     * 处理按照  表达式的方式订阅的消息后的处理
     *
     * @param pattern 订阅匹配的     pattern
     * @param channel key
     * @param message value
     *                hello*=hello_1=123
     *                <p/>
     *                可以将相应的message反序列化为相应的数据类型
     */
    @Override
    public void onPMessage(final byte[] pattern, final byte[] channel, final byte[] message) {
        handleSubscribe.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println(SafeEncoder.encode(pattern) + "=" + SafeEncoder.encode(channel) + "=" + SafeEncoder.encode(message));
            }
        });
    }


    /**
     * 取消订阅（注销）
     *
     * @param channel            key
     * @param subscribedChannels 当前订阅的数量
     */
    @Override
    public void onUnsubscribe(byte[] channel, int subscribedChannels) {
        System.out.println("取消订阅channel：" + SafeEncoder.encode(channel) + "subscribedChannels=" + subscribedChannels);
    }


    /**
     * 取消订阅   按表达式的方式订阅的消息    （注销）
     *
     * @param pattern
     * @param subscribedChannels 当前订阅的数量
     */
    @Override
    public void onPUnsubscribe(byte[] pattern, int subscribedChannels) {
        System.out.println("取消订阅pattern：" + SafeEncoder.encode(pattern) + "subscribedChannels=" + subscribedChannels);
    }


    /**
     * 订阅初始化 在{@link springJredisCache.JCache#subscribe(String...)}
     * <p/>
     * 保留接口 不做处理
     * 初始化订阅
     *
     * @param channel
     * @param subscribedChannels 当前订阅的数量
     */
    @Override
    public void onSubscribe(byte[] channel, int subscribedChannels) {
    }

    /**
     * 订阅初始化 在{@link springJredisCache.JCache#publish(String, byte[])} }
     * 保留接口 不做处理
     * 初始化按表达式的方式订阅的消息
     *
     * @param pattern            订阅的消息类型
     * @param subscribedChannels 订阅的channel数量   from redis
     */
    @Override
    public void onPSubscribe(byte[] pattern, int subscribedChannels) {

    }


    /**
     * 线程池工厂
     */
    private static class PriorityThreadFactory implements ThreadFactory {
        private final int _prio;
        private final String _name;
        private final AtomicInteger _threadNumber = new AtomicInteger(1);
        private final ThreadGroup _group;

        public PriorityThreadFactory(String name, int prio) {
            _prio = prio;
            _name = name;
            _group = new ThreadGroup(_name);
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(_group, r, _name + "-" + _threadNumber.getAndIncrement());
            t.setPriority(_prio);
            return t;
        }

        public ThreadGroup getGroup() {
            return _group;
        }
    }
}
