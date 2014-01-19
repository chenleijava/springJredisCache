package testJRedis;

import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Service;
import springJredisCache.JRedisCache;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * @author 石头哥哥</br>
 *         Date:1/19/14</br>
 *         Time:8:40 PM</br>
 *         Package:testJRedis</br>
 *         Comment：
 */
@Service("test")
@SuppressWarnings("unchecked")
public class Test {
    @Resource
    private JRedisCache jRedisCache;


    @org.junit.Test
    public void  testJRedis(){
        DOMConfigurator.configure("res/appConfig/log4j.xml");

        System.setProperty("java.net.preferIPv4Stack", "true"); //Disable IPv6 in JVM
        /**初始化spring容器*/
        BeanFactory springContext = new FileSystemXmlApplicationContext("res/springConfig/spring-context.xml");
        Test test= (Test) springContext.getBean("test");
        ArrayList<Integer> list=new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);

        //set
        test.jRedisCache.putList("list", list);

        // get
        list= (ArrayList<Integer>) test.jRedisCache.getList("list");

        for(int value:list){
            System.out.println("get value from redis:" + value);
        }
    }


}
