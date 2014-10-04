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
import proto.TRoleEqu;
import springJredisCache.JRedisCache;
import springJredisCache.JRedisSerializationUtils;
import springJredisCache.Serializations.KryoThreadLocalSer;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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


        List<String> list = Arrays.asList("");

        List<String> stringList = new ArrayList<String>();


//        JRedisSerializationUtils.fastDeserialize(JRedisSerializationUtils.fastSerialize(list));
//
//        JRedisSerializationUtils.kryoDeserialize(JRedisSerializationUtils.kryoSerialize(list));


        System.out.println
                ((list instanceof ArrayList) ? "list is ArrayList"
                        : "list not ArrayList?");

        System.out.println
                ((stringList instanceof ArrayList) ? "list is ArrayList"
                        : "list not ArrayList?");

        KryoThreadLocalSer.getInstance().ObjDeserialize( KryoThreadLocalSer.getInstance().ObjSerialize(stringList));


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
        String key = "rolename";
        String filed = "role";
        ArrayList<TRoleEqu> equArrayList = new ArrayList<TRoleEqu>();
        for (int i = 0; i != 500000; ++i) {
            equArrayList.add(new TRoleEqu());
        }

        //1024*40                                           500数据        40960
        //1000                  81038           1k数据          81038
        //1024*1024         1048576        1w条数据      810038
        /**
         *
         >>>>>>>>>>>>>>>>>>>>>>>>>>
         serialize:833
         Deserialize:1137
         >>>>>>>>>>>>>>>>>>>>>>>>>>
         >>>>>>>>>>>>>>>>>>>>>>>>>>
         serialize:308
         Deserialize:539
         >>>>>>>>>>>>>>>>>>>>>>>>>>
         >>>>>>>>>>>>>>>>>>>>>>>>>>
         serialize:367
         Deserialize:376
         >>>>>>>>>>>>>>>>>>>>>>>>>>


         >>>>>>>>>>>>>>>>>>>>>>>>>>
         serialize:1317
         Deserialize:347
         >>>>>>>>>>>>>>>>>>>>>>>>>>
         >>>>>>>>>>>>>>>>>>>>>>>>>>
         serialize:406
         Deserialize:2074
         >>>>>>>>>>>>>>>>>>>>>>>>>>
         >>>>>>>>>>>>>>>>>>>>>>>>>>
         serialize:377
         Deserialize:359
         >>>>>>>>>>>>>>>>>>>>>>>>>>


         */


        for (int i = 0; i != 3; ++i) {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>");
            long start1 = System.currentTimeMillis();
            byte[] kryo_bytes = JRedisSerializationUtils.kryoSerialize(equArrayList);

            System.out.println("serialize:" + (System.currentTimeMillis() - start1));

            long start2 = System.currentTimeMillis();
            ArrayList<TRoleEqu> kryo_list = (ArrayList<TRoleEqu>) JRedisSerializationUtils.kryoDeserialize(kryo_bytes);

            System.out.println("Deserialize:" + (System.currentTimeMillis() - start2));
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>");
        }


        test.jRedisCache.putList(key, filed, equArrayList);


        ArrayList<TRoleEqu> equArrayList2 = (ArrayList<TRoleEqu>) test.jRedisCache.getList(key, filed);

//
        equArrayList2.clear();
        equArrayList.clear();
//
//        test.jRedisCache.removeList(key);

//        FastTable<String> roleofferinfos= (FastTable<String>)test. jRedisCache.getFastTable(key);
//        System.out.println("get value from redis:" + roleofferinfos);


    }

    @After
    public void closeApp() {
        springContext.close();
    }


}
