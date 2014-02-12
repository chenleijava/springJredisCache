/*
 * Copyright (c) 2013.
 *  游戏服务器核心代码编写人陈磊拥有使用权
 *  联系方式：E-mail:13638363871@163.com ;qq:502959937
 *  个人博客主页：http://my.oschina.net/chenleijava
 */

package springJredisCache;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
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
    private static final Kryo kryo = new Kryo();

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
        ByteArrayOutputStream byteArrayOutputStream = null;
        FSTObjectOutput out = null;
        try {
            // stream closed in the finally
            byteArrayOutputStream = new ByteArrayOutputStream(512);
            out = new FSTObjectOutput(byteArrayOutputStream);
            out.writeObject(obj);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException ex) {
            throw new JRedisCacheException(ex);
        } finally {
            try {
                obj=null;
                if (out != null) {
                    out.close();    //call flush byte buffer
                    out=null;
                }
                if (byteArrayOutputStream!=null){
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
                objectData=null;
                if (in != null) {
                    in.close();
                    in=null;
                }
                if (byteArrayInputStream!=null){
                    byteArrayInputStream.close();
                    byteArrayInputStream=null;
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    //基于kryo序列换方案
    /**
     * 将对象序列化为字节数组
     * @param obj
     * @return   字节数组
     * @throws JRedisCacheException
     */
    public static byte[] kryoSerialize(Object obj) throws JRedisCacheException {
        if (obj==null) throw new JRedisCacheException("obj can not be null");
        ByteArrayOutputStream byteArrayOutputStream=null;
        Output output =null;
        try {
            byteArrayOutputStream=new ByteArrayOutputStream();
            output = new Output(byteArrayOutputStream);
            kryo.writeClassAndObject(output, obj);
            return output.toBytes();
        }catch (JRedisCacheException e){
            try {
                if (byteArrayOutputStream!=null){
                    byteArrayOutputStream.close();
                    byteArrayOutputStream=null;
                }
                if (output!=null){
                    output.close();
                    output=null;
                }
            } catch (IOException ee) {
                ee.printStackTrace();
            }
        }finally {
            try {
                obj=null;
                if (byteArrayOutputStream!=null){
                    byteArrayOutputStream.close();
                    byteArrayOutputStream=null;
                }
                if (output!=null){
                    output.close();   /** Writes the buffered bytes to the underlying OutputStream, if any .flush();. */
                    output=null;
                }
            } catch (IOException ee) {
                ee.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 将字节数组反序列化为对象
     * @param bytes    字节数组
     * @return           object
     * @throws JRedisCacheException
     */
    public static Object kryoDeserialize(byte[] bytes) throws JRedisCacheException {
        if (bytes==null) throw new JRedisCacheException("bytes can not be null");
        Input input = null;
        try {
            input = new Input(bytes);
            return kryo.readClassAndObject(input);
        }catch (JRedisCacheException e){
            if (input!=null){
                input.close();
                input=null;
            }
        }finally {
            bytes=null;
            if (input!=null){
                input.close();
                input=null;
            }
        }
        return null;
    }



    //jdk原生序列换方案
    /**
     *
     * @param obj
     * @return
     */
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

    /**
     *
     * @param bits
     * @return
     */
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
