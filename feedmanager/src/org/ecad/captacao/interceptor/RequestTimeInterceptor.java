package org.ecad.captacao.interceptor;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.jboss.logging.Logger;

public class RequestTimeInterceptor {

	@AroundInvoke
	public Object checkTime(InvocationContext invocationContext) throws Exception {
		Logger logger = Logger.getLogger(invocationContext.getTarget().getClass());
		
		Long time = System.currentTimeMillis();
		
		Object result = invocationContext.proceed();
		
		time = System.currentTimeMillis() - time;
		logger.info(String.format("%s() - %sms", invocationContext.getMethod().getName(), time));
		
		return result;
	}
}
