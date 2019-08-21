package eu.europeana.api.common.config.swagger;


import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate methods to be excluded in the Swagger output
 * @author gsergiu
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={TYPE, METHOD})
@Documented
public @interface SwaggerIgnore {
    String value() default "";
}