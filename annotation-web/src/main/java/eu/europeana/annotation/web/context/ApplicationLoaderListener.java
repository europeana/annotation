//package eu.europeana.annotation.web.context;
//
//import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
//import org.springframework.context.ApplicationListener;
//
//import eu.europeana.api.commons.web.context.BaseApplicationLoaderListener;
//
//public class ApplicationLoaderListener extends BaseApplicationLoaderListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent>{
//
//	public ApplicationLoaderListener(){
//		super();
//		onApplicationEvent(null);
//	}
//	
//	@Override
//	public void onApplicationEvent(ApplicationEnvironmentPreparedEvent arg0) {
//		registerSocksProxy();		
//	}
//
//	@Override
//	protected String getAppConfigFile() {
//		return "config/annotation.properties";
//	}
//
//}
