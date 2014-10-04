/*
 * Copyright (c) 2014.  @石头哥哥
 * THIS SOFTWARE IS PROVIDED BY THE FREEBSD PROJECT ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE FREEBSD PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package testJRedis;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.Before;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Service;
import redis.clients.util.SafeEncoder;
import springJredisCache.JRedisCache;

import javax.annotation.Resource;

/**
 * @author 石头哥哥 </br>
 *         springJredisCache </br>
 *         Date:2014/4/2 </br>
 *         Time:15:03 </br>
 *         Package:{@link testJRedis}</br>
 *         Comment：基于jedis的 发布 订阅  测试
 */
@Service("testPubSub")
@SuppressWarnings("unchecked")
public class TestPubSub {


    @Resource
    private JRedisCache jRedisCache;

    private FileSystemXmlApplicationContext springContext;

    private TestPubSub testPubSub;

    @Before
    public void IntiRes() {
        DOMConfigurator.configure("res/appConfig/log4j.xml");
        System.setProperty("java.net.preferIPv4Stack", "true"); //Disable IPv6 in JVM
        springContext = new FileSystemXmlApplicationContext("res/springConfig/spring-context.xml");
        /**初始化spring容器*/
        testPubSub = (TestPubSub) springContext.getBean("testPubSub");

    }

    @org.junit.Test
    public void publish() {
        for (int num=0;num!=1000;++num ) {
            testPubSub.jRedisCache.publish("xxxxsss", "123".getBytes());
            testPubSub.jRedisCache.publish("fod_2", SafeEncoder.encode("456"));
        }
    }

    @org.junit.Test
    public void psubscribe() throws InterruptedException {
        //订阅 处理 指定的消息
        testPubSub.jRedisCache.psubscribe("xxx*", "fod_*");
        //testPubSub.jRedisCache.subscribe("xxxxsss", "fod_2");
    }

    @After
    public void closeApp() {
        springContext.close();
    }

}
