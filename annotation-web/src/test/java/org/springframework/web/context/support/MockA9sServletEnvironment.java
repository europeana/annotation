package org.springframework.web.context.support;

public class MockA9sServletEnvironment extends MockServletEnvironment {

	@Override
	public String getEnvFilePath() {
		return "/generate-config/vcap_services_a9s.env";
	}
	
	protected void putMongoServiceName() {
		mockEnv.put("mongo_service", "annotation-mongo");
	}

}
