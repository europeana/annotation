package org.springframework.web.context.support;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

public abstract class MockServletEnvironment extends StandardServletEnvironment {

	Map<String, Object> mockEnv;
	
	@Override
	public String getProperty(String key) {

//		String mongoDb = getSystemEnvironment().get("mongo_service").toString();
		
		if(mockEnv.containsKey(key))
			return ""+mockEnv.get(key);
		else
			return super.getProperty(key);
	}

	@Override
	public Map<String, Object> getSystemEnvironment() {
		//System-Provided:
		if(mockEnv == null){
			Map<String, Object> sysEnv = super.getSystemEnvironment();
			mockEnv = new HashMap<String, Object>();
			mockEnv.putAll(sysEnv);
			putMongoServiceName();
			String location = getClass().getResource(getEnvFilePath()).getFile();
			String services;
			try {
				services = FileUtils.readFileToString(new File(location));
				mockEnv.put("VCAP_SERVICES", services);		
			} catch (IOException e) {
				throw new RuntimeException("cannot ready resource " + location , e);
			}
			
			
		}
		return mockEnv;

	}

	protected abstract void putMongoServiceName();
	
	@Override
	public boolean containsProperty(String key) {
		return mockEnv.containsKey(key);
	}




	public abstract String getEnvFilePath();
	

}
