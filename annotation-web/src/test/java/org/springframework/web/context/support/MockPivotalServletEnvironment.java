package org.springframework.web.context.support;

import eu.europeana.annotation.web.context.VcapAnnotationPropertyLoaderListener;

public class MockPivotalServletEnvironment extends MockServletEnvironment {

	@Override
	public String getEnvFilePath() {
		return "/generate-config/vcap_services_pivotal.env";
	}

	protected void putMongoServiceName() {
		mockEnv. put(VcapAnnotationPropertyLoaderListener.MONGO_SERVICE, "annotation-mongo");
	}

}
