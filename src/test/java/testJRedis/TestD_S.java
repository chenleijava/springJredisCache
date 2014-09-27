package testJRedis;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import proto.TRoleEqu;
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


    public static void main(String[] args) throws Exception {

        final TRoleEqu vo = new TRoleEqu();
        vo.setOwnerid(1);
        vo.setEquid(1);


        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("序列化 ， 反序列化 100W 次对比测试：");
                for (int j = 0; j != 50; ++j) {
//                    long time2 = System.currentTimeMillis();
//                    for (int i = 0; i < 100000; i++) {
//                        try {
//                            JRedisSerializationUtils.fastDeserialize(JRedisSerializationUtils.fastSerialize(vo));
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    System.out.println("1>>>fast序列化方案[序列化10W次]："
//                            + (System.currentTimeMillis() - time2));

                    long time1 = System.currentTimeMillis();
                    for (int i = 0; i < 1000000; i++) {
                        JRedisSerializationUtils.kryoDeserialize(JRedisSerializationUtils.kryoSerialize(vo));
                    }
                    System.out.println("池化kryo处理方案1>>>kryo序列化方案[序列化100W次]："
                            + (System.currentTimeMillis() - time1));

                    System.out.println("------------------------------------------------------------------------------");
                }
            }
        });


        final TRoleEqu vo2 = new TRoleEqu();
        vo2.setOwnerid(1);
        vo2.setEquid(1);


        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("序列化 ， 反序列化 100W 次对比测试：");
                for (int j = 0; j != 50; ++j) {

                    long time1 = System.currentTimeMillis();
                    for (int i = 0; i < 1000000; i++) {
                        JRedisSerializationUtils.kryoDeserialize(JRedisSerializationUtils.kryoSerialize(vo2));
                    }
                    System.out.println("池化kryo处理方案测试2>>>kryo序列化方案[序列化100W次]："
                            + (System.currentTimeMillis() - time1));

//                    long time2 = System.currentTimeMillis();
//                    for (int i = 0; i < 100000; i++) {
//                        try {
//                            JRedisSerializationUtils.fastDeserialize(JRedisSerializationUtils.fastSerialize(vo2));
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    System.out.println("2>>>fast序列化方案[序列化10W次]："
//                            + (System.currentTimeMillis() - time2));

                    System.out.println("------------------------------------------------------------------------------");
                }
            }
        });



        final TRoleEqu vo3 = new TRoleEqu();
        vo3.setOwnerid(1);
        vo3.setEquid(1);
        System.out.println("序列化 ， 反序列化 100W 次对比测试：");

        Thread t3=new Thread(new Runnable() {
            @Override
            public void run() {
                for (int j = 0; j != 50; ++j) {
                    long time1 = System.currentTimeMillis();
                    for (int i = 0; i < 1000000; i++) {
                        Kryo kryo=new Kryo();
                        Output output = new Output(1024, -1) ;
                        kryo.writeClassAndObject(output,vo3);
                        Input input=new Input();
                        input.setBuffer(output.toBytes());
                        kryo.readClassAndObject(input);
                        //JRedisSerializationUtils.kryoDeserialize(JRedisSerializationUtils.kryoSerialize(vo2));
                    }
                    System.out.println("每次new一个Kyro实例>>>kryo序列化方案[序列化100W次]："
                            + (System.currentTimeMillis() - time1));

//            long time2 = System.currentTimeMillis();
//            for (int i = 0; i < 100000; i++) {
//                try {
//                    JRedisSerializationUtils.fastDeserialize(JRedisSerializationUtils.fastSerialize(vo2));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            System.out.println("2--only >>fast序列化方案[序列化10W次]："
//                    + (System.currentTimeMillis() - time2));

                    System.out.println("------------------------------------------------------------------------------");
                }
            }
        });




        t1.start();
        t2.start();
        t3.start();





    }

//    @org.junit.Test
//    public void testSerialize() throws Exception {
//
//        TRoleEqu vo = new TRoleEqu();
//        vo.setOwnerid(1);
//        vo.setEquid(1);
//
////        RoleVo.Builder builder = RoleVo.newBuilder();
////        builder.setRoleName("123");
////        builder.setRoleID(1);
////        RoleVo vo = builder.build();
//
//        System.out.println("序列化 ， 反序列化 10W 次对比测试：");
//
//        for (int j = 0; j != 50; ++j) {
//            long time1 = System.currentTimeMillis();
//            for (int i = 0; i < 10000; i++) {
//                JRedisSerializationUtils.kryoDeserialize(JRedisSerializationUtils.kryoSerialize(vo));
//            }
//            System.out.println("kryo序列化方案[序列化10000次]："
//                    + (System.currentTimeMillis() - time1));
//
//            long time2 = System.currentTimeMillis();
//            for (int i = 0; i < 10000; i++) {
//                JRedisSerializationUtils.fastDeserialize(JRedisSerializationUtils.fastSerialize(vo));
//            }
//            System.out.println("fast序列化方案[序列化10000次]："
//                    + (System.currentTimeMillis() - time2));
//
////            long time3 = System.currentTimeMillis();
////            for (int i = 0; i < 1000000; i++) {
////                JRedisSerializationUtils.jdeserialize(JRedisSerializationUtils.jserialize(vo));
////            }
////            System.out.println("jdk序列化方案[序列化1000000次]："
////                    + (System.currentTimeMillis() - time3));
//
//
////            long time4 = System.currentTimeMillis();
////            for (int i = 0; i < 100000; i++) {
////                JRedisSerializationUtils.protoDeserialize(JRedisSerializationUtils.protoSerialize(vo), RoleVo.getDefaultInstance());
////            }
////            System.out.println("protoBuffer序列化方案[序列化100000次]："
////                    + (System.currentTimeMillis() - time4));
//
//            System.out.println("------------------------------------------------------------------------------");
//        }
//    }


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
