//package eu.europeana.annotation.web.context;
//
//import java.io.File;
//import java.net.URL;
//import java.net.URLClassLoader;
//import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
//import org.springframework.context.ApplicationListener;
//
//public abstract class BasePropertyLoaderListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
//
//	protected File getConfigFile(String filename) {
//		ClassLoader c = getClass().getClassLoader();
//		@SuppressWarnings("resource")
//		URLClassLoader urlC = (URLClassLoader) c;
//		URL[] urls = urlC.getURLs();
//		String path = urls[0].getPath();
//		return new File(path + "/config/"
//				+ filename);
//	}
//}
