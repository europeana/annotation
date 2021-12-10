package eu.europeana.api.common.config.swagger;

import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;
import static springfox.documentation.builders.RequestHandlerSelectors.withClassAnnotation;
import static springfox.documentation.builders.RequestHandlerSelectors.withMethodAnnotation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author gsergiu
 */
@EnableSwagger2 // Loads the spring beans required by the framework
@PropertySource("classpath:config/swagger.properties")
public class SwaggerConfig {

	@Value("${springfox.host}")
	private String hostUrl;

	@Value("${springfox.application.title}")
	private String title;

	@Value("${springfox.application.description}")
	private String description;

	@Value("${springfox.application.version}")
	private String version;

	@Value("${springfox.api.hiddenPackages}")
	private String hiddenPackages;
	
	private Docket docketConfig;

	public SwaggerConfig() {
		super();
	}

	@Bean
	public Docket customImplementation() {
		if (docketConfig == null) {
			docketConfig = new Docket(DocumentationType.SWAGGER_2);
			ApiSelectorBuilder selector = docketConfig.select()
					// Selects controllers annotated with @SwaggerSelect
					.apis(withClassAnnotation(SwaggerSelect.class))
					// excludes SwaggerIgnore
					.apis(not(or(withMethodAnnotation(SwaggerIgnore.class), 
							withClassAnnotation(SwaggerIgnore.class))));

			//exclude ignored packages
			if(StringUtils.isNotBlank(hiddenPackages)){
				String[] packages = hiddenPackages.split(",");
				for (int i = 0; i < packages.length; i++) {
					selector = selector.apis(not(basePackage(packages[i].trim()))); 
				}
				
			}
			
			docketConfig = selector.build().host(getHostAndPort()).apiInfo(apiInfo());
			
			//user friendly naming of collections and maps with generics
			docketConfig.forCodeGeneration(true);
			
		}
		return docketConfig;
	}

	@SuppressWarnings("deprecation")
	ApiInfo apiInfo() {

		String appTitle = StringUtils.isNotBlank(title) ? title : "REST API";
		String appDescription = StringUtils.isNotBlank(description) ? description : "Entity Collection";

		return new ApiInfo(appTitle, appDescription, version, 
				"Contact Email", "development-core@europeanalabs.eu",
				"Terms of use", "http://www.europeana.eu/portal/rights/api-terms-of-use.html");
	}

	private String getHostAndPort() {
		String url = StringUtils.isNotBlank(hostUrl) ? hostUrl : "localhost:8080";
		if (url.toLowerCase().indexOf("/api") != -1) {
			return url.substring(0, url.toLowerCase().indexOf("/api"));
		} else {
			return url;
		}

	}
}