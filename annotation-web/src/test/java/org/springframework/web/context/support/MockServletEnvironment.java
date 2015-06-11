package org.springframework.web.context.support;

import java.util.HashMap;
import java.util.Map;

import eu.europeana.annotation.web.context.VcapAnnotationPropertyLoaderListener;

public class MockServletEnvironment extends StandardServletEnvironment {

	Map<String, Object> mockEnv;
	
	@Override
	public String getProperty(String key) {

		String mongoDb = getSystemEnvironment().get("mongo_service").toString();
		String userNameKey = VcapAnnotationPropertyLoaderListener.VCAP + mongoDb + VcapAnnotationPropertyLoaderListener.USERNAME; 
		if (userNameKey.equals(key)) {
			return "europeana";
		}

		String passKey = VcapAnnotationPropertyLoaderListener.VCAP + mongoDb + VcapAnnotationPropertyLoaderListener.PASSWORD; 
		if (passKey.equals(key)) {
			return "culture";
		}
		
		String hostsKey = VcapAnnotationPropertyLoaderListener.VCAP + mongoDb + VcapAnnotationPropertyLoaderListener.HOSTS; 
		if (hostsKey.equals(key)) {
			return "localhost:27017";
		}
		
		// 
		return super.getProperty(key);
	}

	@Override
	public Map<String, Object> getSystemEnvironment() {
		if(mockEnv == null){
			Map<String, Object> sysEnv = super.getSystemEnvironment();
			mockEnv = new HashMap<String, Object>();
			mockEnv.putAll(sysEnv);
			mockEnv. put("mongo_service", "mongo_annotation");
		}
		return mockEnv;

	}

}
