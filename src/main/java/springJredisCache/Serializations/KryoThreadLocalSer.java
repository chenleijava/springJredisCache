package springJredisCache.Serializations;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Created by chenlei on 14-10-3.
 */

//Kryo is not thread safe. Each thread should have its own Kryo,
// Input, and Output instances. Also, the byte[] Input uses
// may be modified and then returned to its original state during deserialization,
// so the same byte[] "should not be used concurrently in separate threads.
public class KryoThreadLocalSer implements Serialization {


    private KryoThreadLocalSer() {
    }

    /**
     * a singleton of kryo thread local
     *
     * @return
     */

    public static KryoThreadLocalSer getInstance() {
        return Singleton.kryoThreadLocal;
    }


    /**
     * @param obj
     * @return
     */
    @Override
    public byte[] ObjSerialize(Object obj) {
        try {
            KryoHolder kryoHolder = kryoThreadLocal.get();
            kryoHolder.output.clear();  //clear Output    -->每次调用的时候  重置
            kryoHolder.kryo.writeClassAndObject(kryoHolder.output, obj);
            return kryoHolder.output.toBytes();// 无法避免拷贝  ~~~
        } finally {
            obj = null;
        }

    }

    /**
     * @param bytes
     * @return
     */
    @Override
    public Object ObjDeserialize(byte[] bytes) {
        try {
            KryoHolder kryoHolder = kryoThreadLocal.get();
            kryoHolder.input.setBuffer(bytes, 0, bytes.length);//call it ,and then use input object  ,discard any array
            return kryoHolder.kryo.readClassAndObject(kryoHolder.input);
        } finally {
            bytes = null;       //  for gc
        }

    }


    /**
     * creat a Singleton
     */
    private static class Singleton {
        private static final KryoThreadLocalSer kryoThreadLocal = new KryoThreadLocalSer();
    }


    private final ThreadLocal<KryoHolder> kryoThreadLocal = new ThreadLocal<KryoHolder>() {
        @Override
        protected KryoHolder initialValue() {
            return new KryoHolder(new Kryo());
        }
    };


    private class KryoHolder {
        private Kryo kryo;
        static final int BUFFER_SIZE = 1024;
        private Output output = new Output(BUFFER_SIZE, -1);     //reuse
        private Input input = new Input();

        KryoHolder(Kryo kryo) {
            this.kryo = kryo;
            this.kryo.setReferences(false);
        }

    }


}
