package springJredisCache.Serializations;

/**
 * Created by chenlei on 14-10-4.
 * PROJECT_NAME: springJredisCache
 * PACKAGE_NAME: springJredisCache.Serializations
 */
public interface Serialization {


    /**
     *
     * @param obj
     * @return
     */
    public  byte[] ObjSerialize(Object obj);

    /**
     *
     * @param bytes
     * @return
     */

    public  Object ObjDeserialize(byte[] bytes);

}
