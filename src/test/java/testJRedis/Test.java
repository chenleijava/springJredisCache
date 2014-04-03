package testJRedis;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.Before;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Service;
import proto.TRoleEqu;
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

    private FileSystemXmlApplicationContext springContext;

    @Before
    public void IntiRes() {
        DOMConfigurator.configure("res/appConfig/log4j.xml");
        System.setProperty("java.net.preferIPv4Stack", "true"); //Disable IPv6 in JVM
        springContext = new FileSystemXmlApplicationContext("res/springConfig/spring-context.xml");
    }


    @org.junit.Test
    public void testJRedis() throws InterruptedException {
        /**初始化spring容器*/
        Test test = (Test) springContext.getBean("test");


//        RoleVo.Builder builder=RoleVo.newBuilder();
//        builder.setRoleID(1);
//        builder.setRoleName("石头哥哥");
//        builder.setRoleSex(1);
//        RoleVo vo=builder.build();
//
//
//
//        ArrayList<Integer> list=new ArrayList<Integer>();
//        list.add(1);
//        list.add(2);
//        list.add(3);
//
//        //set
//        test.jRedisCache.putList("list", list);
//        for(int i=0;i!=10000;++i){
//            // get
//            list= (ArrayList<Integer>)test. jRedisCache.getList("list");
//
//            for(int value:list){
//                System.out.println("get value from redis:" + value);
//            }
//        }
        String key = "t_equ100028";
        ArrayList<TRoleEqu> equArrayList = new ArrayList<TRoleEqu>();
        for (int i = 0; i != 1000; ++i) {
            equArrayList.add(new TRoleEqu());
        }
        Thread.sleep(1000);

        String status = test.jRedisCache.putList(key, equArrayList);
        ArrayList<TRoleEqu> equArrayList2 = (ArrayList<TRoleEqu>) test.jRedisCache.getList(key);

        System.out.println("get value from redis:" + equArrayList2);

        test.jRedisCache.removeList(key);




    }

    @After
    public void closeApp() {
        springContext.close();
    }

}
