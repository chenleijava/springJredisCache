/*
 * Copyright (c) 2013.
 *  游戏服务器核心代码编写人陈磊拥有使用权
 *  联系方式：E-mail:13638363871@163.com ;qq:502959937
 *  个人博客主页：http://my.oschina.net/chenleijava
 */

package springJredisCache;


import com.esotericsoftware.kryo.Kryo;
import de.ruedigermoeller.serialization.FSTObjectInput;
import de.ruedigermoeller.serialization.FSTObjectOutput;

import java.io.*;

/**
 * @author 石头哥哥 </br>
 *         dcserver1.3 </br>
 *         Date:14-1-9 </br>
 *         Time:下午4:00 </br>
 *         Package:{@link springJredisCache}</br>
 *         Comment： 对象序列化工具类    序列化方案基于 FST - Fast Serialization
 *         https://github.com/flapdoodle-oss/de.flapdoodle.fast-serialization
 */
public class JRedisSerializationUtils {


    public JRedisSerializationUtils(){}
    public static Kryo kryo = new Kryo();


    // Serialize
    //-----------------------------------------------------------------------
    /**
     * <p>Serializes an <code>Object</code> to a byte array for
     * storage/serialization.</p>
     *
     * @param obj  the object to serialize to bytes
     * @return a byte[] with the converted Serializable
     * @throws JRedisCacheException (runtime) if the serialization fails
     */
    public static byte[] fastSerialize(Object obj) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(512);
        serialize(obj, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * @param obj  the object to serialize to bytes, may be null
     * @param byteArrayOutputStream  the stream to write to, must not be null
     * @throws IllegalArgumentException if <code>outputStream</code> is <code>null</code>
     * @throws org.apache.commons.lang.SerializationException (runtime) if the serialization fails
     */
    private static void serialize(Object obj, ByteArrayOutputStream byteArrayOutputStream) {
        if (byteArrayOutputStream == null) {
            throw new IllegalArgumentException("The OutputStream must not be null");
        }
        FSTObjectOutput out = null;
        try {
            // stream closed in the finally
            out = new FSTObjectOutput(byteArrayOutputStream);
            out.writeObject(obj);
            out.flush();
        } catch (IOException ex) {
            throw new JRedisCacheException(ex);
        } finally {
            try {
                if (out != null) {
                    out.close();
                    out=null;
                    byteArrayOutputStream.close();
                    byteArrayOutputStream=null;
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
     * @param objectData  the serialized object, must not be null
     * @return the deserialized object
     * @throws IllegalArgumentException if <code>objectData</code> is <code>null</code>
     * @throws JRedisCacheException (runtime) if the serialization fails
     */
    public static Object fastDeserialize(byte[] objectData) {
        if (objectData == null) {
            throw new IllegalArgumentException("The byte[] must not be null");
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(objectData);
        return deserialize(byteArrayInputStream);
    }
    /**
     * <p>Deserializes an <code>Object</code> from the specified stream.</p>
     *
     * <p>The stream will be closed once the object is written. This
     * avoids the need for a finally clause, and maybe also exception
     * handling, in the application code.</p>
     *
     * <p>The stream passed in is not buffered internally within this method.
     * This is the responsibility of your application if desired.</p>
     *
     * @param byteArrayInputStream  the serialized object input stream, must not be null
     * @return the deserialized object
     * @throws IllegalArgumentException if <code>inputStream</code> is <code>null</code>
     * @throws JRedisCacheException (runtime) if the serialization fails
     */
    private static Object deserialize(ByteArrayInputStream byteArrayInputStream) {
        if (byteArrayInputStream == null) {
            throw new IllegalArgumentException("The InputStream must not be null");
        }
        FSTObjectInput in = null;
        try {
            // stream closed in the finally
            in = new FSTObjectInput(byteArrayInputStream);
            return in.readObject();
        } catch (ClassNotFoundException ex) {
            throw new JRedisCacheException(ex);
        } catch (IOException ex) {
            throw new JRedisCacheException(ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                    in=null;
                    byteArrayInputStream.close();
                    byteArrayInputStream=null;
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }



    //jdk原生序列换方案
    public static byte[] jserialize(Object obj) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos =null;
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

    public static Object jdeserialize(byte[] bits) {
        ObjectInputStream ois = null;
        ByteArrayInputStream bais =null;
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



}
