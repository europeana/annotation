package eu.europeana.annotation.web.dispatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.DispatcherServlet;

public class AnnotationDispatcherServlet extends DispatcherServlet {
	
	private static final long serialVersionUID = 1L;
	Logger logger = Logger.getLogger(getClass());
	
	protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		logger.debug("Annotation dispatcher servlet");
		
		super.doDispatch(request, response);
		
	}
	

}
