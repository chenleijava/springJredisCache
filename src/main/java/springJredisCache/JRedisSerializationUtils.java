/*
 * Copyright (c) 2013.
 *  游戏服务器核心代码编写人陈磊拥有使用权
 *  联系方式：E-mail:13638363871@163.com ;qq:502959937
 *  个人博客主页：http://my.oschina.net/chenleijava
 */

package springJredisCache;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.UnsafeInput;
import com.esotericsoftware.kryo.io.UnsafeOutput;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import de.ruedigermoeller.serialization.FSTObjectInput;
import de.ruedigermoeller.serialization.FSTObjectOutput;
import javolution.util.FastTable;

import java.io.*;

/**
 * @author 石头哥哥 </br>
 *         dcserver1.3 </br>
 *         Date:14-1-9 </br>
 *         Time:下午4:00 </br>
 *         Package:{@link springJredisCache}</br>
 *         Comment： 对象序列化工具类    序列化方案基于 FST - Fast Serialization
 *         https://github.com/flapdoodle-oss/de.flapdoodle.fast-serialization
 *         <p/>
 */
public class JRedisSerializationUtils {


    public JRedisSerializationUtils() {
    }

//    private static final Kryo kryo = new Kryo();
//
//    static {
//        kryo.setRegistrationRequired(false);
//        //http://hi.baidu.com/macrohuang/item/70d84a6f9f1b11147ddecc90
// //       kryo.setInstantiatorStrategy(new SerializingInstantiatorStrategy());
//    }


    // Serialize
    //-----------------------------------------------------------------------

    /**
     * <p>Serializes an <code>Object</code> to a byte array for
     * storage/serialization.</p>
     *
     * @param obj the object to serialize to bytes
     * @return a byte[] with the converted Serializable
     * @throws JRedisCacheException (runtime) if the serialization fails
     */
    public static byte[] fastSerialize(Object obj) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        FSTObjectOutput out = null;
        try {
            // stream closed in the finally
            byteArrayOutputStream = new ByteArrayOutputStream(512);
            out = new FSTObjectOutput(byteArrayOutputStream);  //32000  buffer size
            out.writeObject(obj);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException ex) {
            throw new JRedisCacheException(ex);
        } finally {
            try {
                obj = null;
                if (out != null) {
                    out.close();    //call flush byte buffer
                    out = null;
                }
                if (byteArrayOutputStream != null) {

                    byteArrayOutputStream.close();
                    byteArrayOutputStream = null;
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }


    // Deserialize
    //-----------------------------------------------------------------------

    /**
     * <p>Deserializes a single <code>Object</code> from an array of bytes.</p>
     *
     * @param objectData the serialized object, must not be null
     * @return the deserialized object
     * @throws IllegalArgumentException if <code>objectData</code> is <code>null</code>
     * @throws JRedisCacheException     (runtime) if the serialization fails
     */
    public static Object fastDeserialize(byte[] objectData) {
        ByteArrayInputStream byteArrayInputStream = null;
        FSTObjectInput in = null;
        try {
            // stream closed in the finally
            byteArrayInputStream = new ByteArrayInputStream(objectData);
            in = new FSTObjectInput(byteArrayInputStream);
            return in.readObject();
        } catch (ClassNotFoundException ex) {
            throw new JRedisCacheException(ex);
        } catch (IOException ex) {
            throw new JRedisCacheException(ex);
        } finally {
            try {
                objectData = null;
                if (in != null) {
                    in.close();
                    in = null;
                }
                if (byteArrayInputStream != null) {
                    byteArrayInputStream.close();
                    byteArrayInputStream = null;
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }


    interface KryoPool {


        /**
         * get o kryo object
         *
         * @return
         */
        Kryo get();


        /**
         * return object
         *
         * @param kryo
         */
        void offer(Kryo kryo);


    }

    //基于kryo序列换方案
    private static class KryoPoolImpl implements KryoPool {
        /**
         * default is 3000
         * online server limit 3K
         */
        private static int DEFAULT_MAX_KRYO_SIZE = 3000;

        /**
         * thread safe list
         */
      private final FastTable<Kryo> kryoFastTable = new FastTable<Kryo>();


        private KryoPoolImpl() {
        }



        /**
         * @return
         */
        public static KryoPool getInstance() {
            return Singleton.pool;
        }

        /**
         * get o kryo object
         *
         * @return
         */
        @Override
        public Kryo get() {
            Kryo kryo = kryoFastTable.pollFirst();       // Retrieves and removes the head of the queue represented by this table
            return kryo == null ? creatInstnce() : kryo;
        }

        /**
         * create a new kryo object to application use
         *
         * @return
         */
        private Kryo creatInstnce() {
            Kryo kryo = new Kryo();
            kryo.setReferences(false);//
            return kryo;
        }

        /**
         * return object
         * over limit ,will to GC handle
         *
         * @param kryo
         */
        @Override
        public void offer(Kryo kryo) {
            if (kryo != null) {
                if (kryoFastTable.size() < DEFAULT_MAX_KRYO_SIZE) {
                    kryoFastTable.addLast(kryo);          //Inserts the specified element at the tail of this queue.
                } else {
                    kryo = null;//to GC
                }
            }
        }

        /**
         * creat a Singleton
         */
        private static class Singleton {
            private static final KryoPool pool = new KryoPoolImpl();
        }
    }


    /**
     * 将对象序列化为字节数组
     *
     * @param obj
     * @return 字节数组
     * @throws JRedisCacheException
     */
    public static byte[] kryoSerialize(Object obj) throws JRedisCacheException {
        Kryo kryo = null;
        if (obj == null) throw new JRedisCacheException("obj can not be null");
        UnsafeOutput output = null;
        try {
            kryo = KryoPoolImpl.getInstance().get();
            output = new UnsafeOutput(512, -1);    //buffersize =40960  防止数据过大 ，多次拷贝
            kryo.writeClassAndObject(output, obj);
            return output.toBytes();
        } catch (JRedisCacheException e) {
            throw new JRedisCacheException("Serialize obj exception");
        } finally {
            KryoPoolImpl.getInstance().offer(kryo);
            obj = null;
            if (output != null) {
                output.close();   /** Writes the buffered bytes to the underlying OutputStream, if any .flush();. */
                output = null;
            }
        }
    }


    /**
     * 将字节数组反序列化为对象
     *
     * @param bytes 字节数组
     * @return object
     * @throws JRedisCacheException
     */
    public static Object kryoDeserialize(byte[] bytes) throws JRedisCacheException {
        UnsafeInput input = null;
        Kryo kryo = null;
        if (bytes == null) throw new JRedisCacheException("bytes can not be null");
        try {
            kryo = KryoPoolImpl.getInstance().get();
            input = new UnsafeInput(bytes);
            return kryo.readClassAndObject(input);
        } catch (JRedisCacheException e) {
            throw new JRedisCacheException("Deserialize bytes exception");
        } finally {
            KryoPoolImpl.getInstance().offer(kryo);
            bytes = null;
            if (input != null) {
                input.close();
                input = null;
            }
        }
    }


    //jdk原生序列换方案

    /**
     * @param obj
     * @return
     */
    public static byte[] jserialize(Object obj) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new JRedisCacheException(e);
        } finally {
            if (oos != null)
                try {
                    oos.close();
                    baos.close();
                } catch (IOException e) {
                }
        }
    }

    /**
     * @param bits
     * @return
     */
    public static Object jdeserialize(byte[] bits) {
        ObjectInputStream ois = null;
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(bits);
            ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            throw new JRedisCacheException(e);
        } finally {
            if (ois != null)
                try {
                    ois.close();
                    bais.close();
                } catch (IOException e) {
                }
        }
    }


    // 基于protobuffer的序列化方案

    /**
     * @param bytes       字节数据
     * @param messageLite 序列化对应的类型
     * @return
     * @throws JRedisCacheException
     */
    public static MessageLite protoDeserialize(byte[] bytes, MessageLite messageLite) throws JRedisCacheException {
        assert (bytes != null && messageLite != null);
        try {
            return messageLite.getParserForType().parsePartialFrom(CodedInputStream.newInstance(bytes), ExtensionRegistryLite.getEmptyRegistry());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param messageLite 序列化对应的类型
     * @return
     * @throws JRedisCacheException
     */
    public static byte[] protoSerialize(MessageLite messageLite) throws JRedisCacheException {
        assert (messageLite != null);
        return messageLite.toByteArray();
    }


}
