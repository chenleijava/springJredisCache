package springJredisCache;

/**
 * JRedisCache 异常类
 */
public class JRedisCacheException extends RuntimeException {

    private static final long serialVersionUID = -2282812710637100053L;

    public JRedisCacheException(String s) {
        super(s);
    }

    public JRedisCacheException(String s, Throwable e) {
        super(s, e);
    }

    public JRedisCacheException(Throwable e) {
        super(e);
    }

}
