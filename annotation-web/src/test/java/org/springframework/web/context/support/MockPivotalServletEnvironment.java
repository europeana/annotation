package org.springframework.web.context.support;

public class MockPivotalServletEnvironment extends MockServletEnvironment {

	@Override
	public String getEnvFilePath() {
		return "/generate-config/vcap_services_pivotal.env";
	}

	protected void putMongoServiceName() {
		mockEnv. put("mongolab", "annotation-mongo");
	}

}
