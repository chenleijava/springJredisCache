package springJredisCache;

import javolution.util.FastMap;
import javolution.util.FastTable;

import java.io.Serializable;
import java.util.ArrayList;

public interface JCache {
    /**
     *
     * @return
     */
    public String info();

    /**
     * @param key
     * @return
     * @
     */
    public Object getList(String key);
    /**
     *
     * @param key
     * @param list
     */
    public void putList(String key,ArrayList<?> list);

    /**
     * Remove an item from the cache
     */
    public void removeList(String key) ;


    /**
     *
     * @param key
     * @return
     */
    public FastTable<?> getFastTable(String key);


    /**
     *
     * @param key
     * @param list
     */
    public void putFastTable(String key, FastTable<?> list);

    /**
     *
     * @param key
     * @return
     * @
     */
    public FastMap<?,?> getFastMap(String key) ;
    /**
     * Remove an item from the cache
     */
    public void removeFastMap(String key) ;
    /**
     *
     * @param key
     * @param fastMap
     */
    public void putFastMap(String key, FastMap<?,?> fastMap);

    /**
     * Get an item from the cache, nontransactionally
     * @param key
     * @return the cached object or <tt>null</tt>
     * @throws JRedisCacheException
     */
    public Serializable getObject(String key) ;

    /**
     * Add an item to the cache, nontransactionally, with
     * failfast semantics
     * @param key
     * @param value
     * @
     */
    public void putObject(String key, Serializable value) ;

    /**
     * Remove an item from the cache
     */
    public void removeObject(String key) ;

    /**
     *
     * @return
     * @throws JRedisCacheException
     */
    public FastTable<String> keys()  ;

    public void destroy() ;

}
