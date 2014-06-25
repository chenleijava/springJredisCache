package testJRedis;

import proto.RoleVo;
import springJredisCache.JRedisSerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author 石头哥哥 </br>
 *         dcServer1.7 </br>
 *         Date:14-2-11 </br>
 *         Time:下午3:36 </br>
 *         Package:{@link springJredisCache}</br>
 *         Comment：    序列化 反序列化 测试
 */
public class TestD_S implements Serializable {

    private String username;
    private String password;
    private int age;
    private ArrayList<Integer> list;

    public TestD_S() {
        list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
    }


    @org.junit.Test
    public void testSerialize() {
        RoleVo.Builder builder = RoleVo.newBuilder();
        builder.setRoleID(1);
        builder.setRoleName("石头哥哥");
        builder.setRoleSex(1);
        RoleVo vo = builder.build();

        System.out.println("序列化 ， 反序列化 对比测试：");

        for (int j = 0; j != 50; ++j) {
           long time1 = System.currentTimeMillis();
           for (int i = 0; i < 1000000; i++) {
               JRedisSerializationUtils.kryoDeserialize(JRedisSerializationUtils.kryoSerialize(vo)) ;
           }
           System.out.println("kryo序列化方案[序列化1000000次]："
                   + (System.currentTimeMillis() - time1));

            long time2 = System.currentTimeMillis();
            for (int i = 0; i < 1000000; i++) {
                JRedisSerializationUtils.fastDeserialize(JRedisSerializationUtils.fastSerialize(vo));
            }
            System.out.println("fast序列化方案[序列化1000000次]："
                    + (System.currentTimeMillis() - time2));

            long time3 = System.currentTimeMillis();
            for (int i = 0; i < 1000000; i++) {
                JRedisSerializationUtils.jdeserialize(JRedisSerializationUtils.jserialize(vo));
            }
            System.out.println("jdk序列化方案[序列化1000000次]："
                    + (System.currentTimeMillis() - time3));


            long time4 = System.currentTimeMillis();
            for (int i = 0; i < 1000000; i++) {
                JRedisSerializationUtils.protoDeserialize(JRedisSerializationUtils.protoSerialize(vo), RoleVo.getDefaultInstance());
            }
            System.out.println("protoBuffer序列化方案[序列化1000000次]："
                    + (System.currentTimeMillis() - time4));

            System.out.println("------------------------------------------------------------------------------");
        }
    }


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
