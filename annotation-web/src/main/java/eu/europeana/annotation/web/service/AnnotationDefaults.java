package eu.europeana.annotation.web.service;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;

public class AnnotationDefaults {

    private String provider;
    private Agent generator;
    private Agent user;

    public static class Builder {
        private String provider;
        private Agent generator;
        private Agent user;
        public Builder() {
        }
        public Builder setProvider(String provider) {
            this.provider = provider;
            return this;
        }
        public Builder setUser(Agent user) {
            this.user = user;
            return this;
        }
        public Builder setGenerator(Agent generator) {
            this.generator = generator;
            return this;
        }
        public AnnotationDefaults build() {
        	if(provider == null || StringUtils.isEmpty(provider))
				provider = WebAnnotationFields.DEFAULT_PROVIDER;	
            return new AnnotationDefaults(provider, generator, user);
        }
    }
    
    public void putAnnotationDefaultValues(Annotation anno) {
    	if (anno.getGenerator() == null)
			anno.setGenerator(this.getGenerator());
		if (anno.getCreator() == null)
			anno.setCreator(this.getUser());
		anno.setCreated(new Date());
		anno.getAnnotationId().setHttpUrl(anno.getAnnotationId().toHttpUrl());
    }
    
    public AnnotationDefaults(String provider, Agent app, Agent user) {
        this.provider = provider;
        this.generator = app;
        this.user = user;
    }
	public String getProvider() {
		return provider;
	}
	public Agent getGenerator() {
		return generator;
	}
	public Agent getUser() {
		return user;
	}
    
}