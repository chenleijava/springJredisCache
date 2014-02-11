package testJRedis;

import springJredisCache.JRedisSerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author 石头哥哥 </br>
 *         dcServer1.7 </br>
 *         Date:14-2-11 </br>
 *         Time:下午3:36 </br>
 *         Package:{@link springJredisCache}</br>
 *         Comment：
 */
public class userBean implements Serializable {

    private  String username;
    private  String password;
    private int age;
    private ArrayList<Integer> list;

    public userBean(){
        list=new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
    }


    public static void main(String[]args){
        userBean bean=new userBean();
        bean.setUsername("xxxxx");
        bean.setPassword("123456");
        bean.setAge(1000000);
        JRedisSerializationUtils.kryo.register(userBean.class);
        System.out.println("序列化 ， 反序列化 对比测试：");
        long time1 = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            JRedisSerializationUtils.jdeserialize(JRedisSerializationUtils.jserialize(bean));
        }
        System.out.println("原生序列化方案[序列化100000次]："
                + (System.currentTimeMillis() - time1));

        long time2 = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            JRedisSerializationUtils.fastDeserialize(JRedisSerializationUtils.fastSerialize(bean));
        }
        System.out.println("fast序列化方案[序列化100000次]："
                + (System.currentTimeMillis() - time2));
    }


//    @Test
//    public void testSerialize(){
//        userBean bean=new userBean();
//        bean.setUsername("xxxxx");
//        bean.setPassword("123456");
//        bean.setAge(1000000);
//        JRedisSerializationUtils.kryo.register(userBean.class);
//        System.out.println("序列化 ， 反序列化 对比测试：");
//        long time1 = System.currentTimeMillis();
////        JRedisSerializationUtils.kryoDeserialize(JRedisSerializationUtils.kryoSerialize(bean));
//        for (int i = 0; i < 100000; i++) {
//            JRedisSerializationUtils.kryoDeserialize(JRedisSerializationUtils.kryoSerialize(bean));
//        }
//        System.out.println("原生序列化方案[序列化100000次]："
//                + (System.currentTimeMillis() - time1));
//
//
//        long time2 = System.currentTimeMillis();
//        for (int i = 0; i < 100000; i++) {
//            JRedisSerializationUtils.fastDeserialize(JRedisSerializationUtils.fastSerialize(bean));
//        }
//        System.out.println("fast序列化方案[序列化100000次]："
//                + (System.currentTimeMillis() - time2));
//    }
//


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

}
