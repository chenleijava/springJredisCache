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
    }

    @After
    public void closeApp() {
        springContext.close();
    }

}
