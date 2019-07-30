package eu.europeana.annotation.web.profiling;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.util.StopWatch;
import org.springframework.util.StopWatch.TaskInfo;
 
/**
 * Profiler to log process time.
 */
public class SimpleProfiler {
	
	Logger logger = LogManager.getLogger(getClass());
     
    /**
     * Profiling method
     **/
    public Object profile(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start(proceedingJoinPoint.toShortString());
        boolean isExceptionThrown = false;
        try {
            return proceedingJoinPoint.proceed();
        } catch (RuntimeException e) {
            isExceptionThrown = true;
            throw e;
        } finally {
            stopWatch.stop();
            TaskInfo taskInfo = stopWatch.getLastTaskInfo();            
            String profileMessage = taskInfo.getTaskName() + ":\t" + taskInfo.getTimeMillis() + "ms" +
                    (isExceptionThrown ? " (thrown Exception)" : "");
            logger.debug("Profiling " + profileMessage);	
        }
    }
     
}