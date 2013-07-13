package com.bjgl.web.config;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 系统全局配置变量
 * @author leiming
 *
 */
public class GlobalConfig {
	private static final Logger logger = LoggerFactory.getLogger(GlobalConfig.class.getName());
	/**
	 * 缓存开奖锁存活时间  300 秒
	 */
	public static int MEMCACHE_LOTTERY_DRAW_LOCK_ALIVE_TIME = 300;
	/**
	 * 缓存派奖锁存活时间  300 秒
	 */
	public static int MEMCACHE_LOTTERY_REWARD_LOCK_ALIVE_TIME = 300;
	
	private GlobalConfig() {
		
	}
	
	static {
		InputStream in = GlobalConfig.class.getClassLoader().getResourceAsStream("bjgl.properties");
		Properties properties = new Properties();
		try {
			properties.load(in);
			MEMCACHE_LOTTERY_DRAW_LOCK_ALIVE_TIME = Integer.parseInt(properties.getProperty("memcache.lottery.draw.lock.alive.time"),10);
			MEMCACHE_LOTTERY_REWARD_LOCK_ALIVE_TIME = Integer.parseInt(properties.getProperty("memcache.lottery.reward.lock.alive.time"),10);
			
			in.close();
		} catch (Exception e) {
			logger.error("读取配置文件admin.properties失败", e);
		}
	}
}
