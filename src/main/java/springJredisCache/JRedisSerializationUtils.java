/*
 * Copyright (c) 2014.  @石头哥哥
 * THIS SOFTWARE IS PROVIDED BY THE FREEBSD PROJECT ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE FREEBSD PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package springJredisCache;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import javolution.util.FastTable;
import org.msgpack.MessagePack;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import springJredisCache.Serializations.KryoThreadLocalSer;

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
 *
 *          cache utils
 *
 */

public class JRedisSerializationUtils {


    public JRedisSerializationUtils() {
    }


    protected static  boolean useKryoPool=false;

    // Serialize
    //-----------------------------------------------------------------------

    //    In order to optimize object reuse and thread safety,
    // FSTConfiguration provides 2 simple factory methods to
    // obtain input/outputstream instances (they are stored thread local):
    //! reuse this Object, it caches metadata. Performance degrades massively
    //using createDefaultConfiguration()        FSTConfiguration is singleton

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
            out.flush();
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
    public static Object fastDeserialize(byte[] objectData) throws Exception {
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

    /**
     * Kryo 的包装
     */
    private static class KryoHolder {
        private Kryo kryo;
        static final int BUFFER_SIZE = 1024;
        private Output output = new Output(BUFFER_SIZE, -1);     //reuse
        private Input input = new Input();

        KryoHolder(Kryo kryo) {
            this.kryo = kryo;
        }

        /**
         * @param kryo
         * @param clazz
         */
        private static void checkRegiterNeeded(Kryo kryo, Class<?> clazz) {
            kryo.register(clazz);
        }
    }


    interface KryoPool {

        /**
         * get o kryo object
         *
         * @return
         */
        KryoHolder get();

        /**
         * return object
         *
         * @param kryo
         */
        void offer(KryoHolder kryo);
    }


    //基于kryo序列换方案

    /**
     * 由于kryo创建的代价相对较高 ，这里使用空间换时间
     * 对KryoHolder对象进行重用
     * KryoHolder会出现峰值，应该不会造成内存泄漏哦
     */
    public static class KryoPoolImpl implements KryoPool {
        /**
         * default is 1500
         * online server limit 3K
         */
        // private static int DEFAULT_MAX_KRYO_SIZE = 1500;

        /**
         * thread safe list
         */
        private final FastTable<KryoHolder> kryoFastTable = new FastTable<KryoHolder>().atomic();


        /**
         *
         */
        private KryoPoolImpl() {

        }

        /**
         * @return
         */
        public static KryoPool getInstance() {
            return Singleton.pool;
        }

        /**
         * get o KryoHolder object
         *
         * @return
         */
        @Override
        public KryoHolder get() {
            KryoHolder kryoHolder = kryoFastTable.pollFirst();       // Retrieves and removes the head of the queue represented by this table
            return kryoHolder == null ? creatInstnce() : kryoHolder;
        }

        /**
         * create a new kryo object to application use
         *
         * @return
         */
        public KryoHolder creatInstnce() {
            Kryo kryo = new Kryo();
            kryo.setReferences(false);//
            return new KryoHolder(kryo);
        }

        /**
         * return object
         * Inserts the specified element at the tail of this queue.
         *
         * @param kryoHolder
         */
        @Override
        public void offer(KryoHolder kryoHolder) {
            kryoFastTable.addLast(kryoHolder);
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

        if (useKryoPool){
            KryoHolder kryoHolder = null;
            if (obj == null) throw new JRedisCacheException("obj can not be null");
            try {
                kryoHolder = KryoPoolImpl.getInstance().get();
                kryoHolder.output.clear();  //clear Output    -->每次调用的时候  重置
                kryoHolder.kryo.writeClassAndObject(kryoHolder.output, obj);
                return kryoHolder.output.toBytes();// 无法避免拷贝  ~~~
            } catch (JRedisCacheException e) {
                throw new JRedisCacheException("Serialize obj exception");
            } finally {
                KryoPoolImpl.getInstance().offer(kryoHolder);
                obj = null; //GC
            }
        }else {
            return KryoThreadLocalSer.getInstance().ObjSerialize(obj);
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
        if (useKryoPool){
            KryoHolder kryoHolder = null;
            if (bytes == null) throw new JRedisCacheException("bytes can not be null");
            try {
                kryoHolder = KryoPoolImpl.getInstance().get();
                kryoHolder.input.setBuffer(bytes, 0, bytes.length);//call it ,and then use input object  ,discard any array
                return kryoHolder.kryo.readClassAndObject(kryoHolder.input);
            } catch (JRedisCacheException e) {
                throw new JRedisCacheException("Deserialize bytes exception");
            } finally {
                KryoPoolImpl.getInstance().offer(kryoHolder);
                bytes = null;       //  for gc
            }
        }else {
            return KryoThreadLocalSer.getInstance().ObjDeserialize(bytes);
        }

    }


    /**
     * 将字节数组反序列化为对象
     *
     * @param bytes 字节数组
     * @return object
     * @throws JRedisCacheException
     */
    public static Object kryoDeserialize(byte[] bytes,int length) throws JRedisCacheException {
        KryoHolder kryoHolder = null;
        if (bytes == null) throw new JRedisCacheException("bytes can not be null");
        try {
            kryoHolder = KryoPoolImpl.getInstance().get();
            kryoHolder.input.setBuffer(bytes, 0, length);//call it ,and then use input object  ,discard any array
            return kryoHolder.kryo.readClassAndObject(kryoHolder.input);
        } catch (JRedisCacheException e) {
            throw new JRedisCacheException("Deserialize bytes exception");
        } finally {
            KryoPoolImpl.getInstance().offer(kryoHolder);
            bytes = null;       //  for gc
        }
    }

    private static final class MessagePackHolder {
        private MessagePack messagePack;
        static final int BUFFER_SIZE = 1024;
        private Output output = new Output(BUFFER_SIZE, -1);     //reuse
        private Input in = new Input();

        public MessagePackHolder(MessagePack messagePack) {
            this.messagePack = messagePack;
        }
    }

    //基于messagePack的序列化方案
    interface MessagePackPool {

        /**
         * @return
         */
        MessagePackHolder get();

        /**
         * @param messagePack
         */
        void offer(MessagePackHolder messagePack);


    }


    private static final class MessagePackPoolImpl implements MessagePackPool {

        /**
         * atomic , thread safe list
         */
        private final FastTable<MessagePackHolder> messagePackFastTable = new FastTable<MessagePackHolder>().atomic();

        /**
         * @return
         */
        public static MessagePackPool getInstance() {
            return Singleton.pool;
        }

        private MessagePackPoolImpl() {
        }

        /**
         * @return
         */
        @Override
        public MessagePackHolder get() {
            MessagePackHolder messagePackHolder = messagePackFastTable.pollFirst();
            return messagePackHolder == null ? creatInstnce() : messagePackHolder;
        }

        /**
         * create a new kryo object to application use
         *
         * @return
         */
        private MessagePackHolder creatInstnce() {
            return new MessagePackHolder(new MessagePack());
        }

        /**
         * @param messagePackHolder
         */
        @Override
        public void offer(MessagePackHolder messagePackHolder) {
            if (messagePackHolder != null) messagePackFastTable.addLast(messagePackHolder);
        }

        /**
         * creat a Singleton
         */
        private static class Singleton {
            private static final MessagePackPool pool = new MessagePackPoolImpl();
        }
    }


    /**
     * 将对象序列化为字节数组
     *
     * @param obj
     * @return 字节数组
     * @throws JRedisCacheException
     */
    public static byte[] messagePackSerialize(Object obj) throws JRedisCacheException {
        MessagePackHolder messagePackHolder = null;
        if (obj == null) throw new JRedisCacheException("obj can not be null");
        try {
            messagePackHolder = MessagePackPoolImpl.getInstance().get();
            messagePackHolder.output.clear();
            messagePackHolder.messagePack.write(messagePackHolder.output, obj);
            return messagePackHolder.output.toBytes();
        } catch (JRedisCacheException e) {
            MessagePackPoolImpl.getInstance().offer(messagePackHolder);
            throw new JRedisCacheException("Serialize obj exception");
        } catch (IOException e) {
            MessagePackPoolImpl.getInstance().offer(messagePackHolder);
            return null;
        } finally {
            MessagePackPoolImpl.getInstance().offer(messagePackHolder);
            obj = null; //GC
        }
    }


    /**
     * 将字节数组反序列化为对象
     *
     * @param bytes 字节数组
     * @return object
     * @throws JRedisCacheException
     */
    public static Object messagePackDeserialize(byte[] bytes) throws JRedisCacheException {
        MessagePackHolder messagePackHolder = null;
        if (bytes == null) throw new JRedisCacheException("bytes can not be null");
        try {
            messagePackHolder = MessagePackPoolImpl.getInstance().get();
            messagePackHolder.in.setBuffer(bytes);
            return messagePackHolder.messagePack.read(messagePackHolder.in);
        } catch (JRedisCacheException e) {
            throw new JRedisCacheException("Deserialize bytes exception");
        } catch (IOException e) {
            MessagePackPoolImpl.getInstance().offer(messagePackHolder);
            return null;
        } finally {
            MessagePackPoolImpl.getInstance().offer(messagePackHolder);
            bytes = null;
        }
    }


    //jdk原生序列换方案
    /**
     * @param obj
     * @return
     */
    @Deprecated
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
    @Deprecated
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
