package springJredisCache;

/**
 *  JRedisCache 异常类
 */
public class JRedisCacheException extends RuntimeException {

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
