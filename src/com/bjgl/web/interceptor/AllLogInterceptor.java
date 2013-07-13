package com.bjgl.web.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AllLogInterceptor {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public Object doAround(ProceedingJoinPoint pjp) throws Throwable{
		logger.info("1 Enter Class:{}", pjp.getTarget().getClass());
		logger.info("2 Execute Method:{}", pjp.getSignature().getName());
		Object result = pjp.proceed();		
		logger.info("3 Return Result:{}", result);
		logger.info("4 Exit Class:{}", pjp.getTarget().getClass());
		return result;
	}
}
