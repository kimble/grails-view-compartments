package grails.plugin.viewcompartment;

import groovy.lang.GroovyObject;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.groovy.grails.commons.GrailsClassUtils;
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest;
import org.codehaus.groovy.grails.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

/**
 * Checks whether the controller has a static compartment property.
 * If it doesn't we just delegate the resolve operation to the original jspViewResolver bean.
 * @author Kim A. Betti
 */
public class CompartmentAwareViewResolver implements ViewResolver {

    private static final Logger log = LoggerFactory.getLogger(CompartmentAwareViewResolver.class);

    public static final String COMPARTMENT = "compartment";
    public static final String NO_COMPARTMENT = "__no_compartment"; // ConcurrentHashMap dosen't like null

    private ConcurrentMap<String, String> controllerCompartmentCache = new ConcurrentHashMap<String, String>();

    private ViewResolver originalViewResolver;

    @Override
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        String compartment = getCompartment(viewName);
        String resolvedViewName = getViewNameWithCompartment(viewName, compartment);
        
        return originalViewResolver.resolveViewName(resolvedViewName, locale);
    }
    
    public void cleanCompartmentCache() {
        log.debug("Clearing compartment cache");
        controllerCompartmentCache.clear();
    }

    protected String getCompartment(String viewName) {
        GrailsWebRequest webRequest = WebUtils.retrieveGrailsWebRequest();
        HttpServletRequest request = webRequest.getCurrentRequest();
        GroovyObject controller = webRequest.getAttributes().getController(request);
        return controller == null ? viewName : getAndUpdateCompartmentFromCache(controller);
    }

    protected String getAndUpdateCompartmentFromCache(GroovyObject controller) {
        String compartment = NO_COMPARTMENT;
        String key = controller.getClass().getName();
        if (controllerCompartmentCache.containsKey(key)) {
            compartment = controllerCompartmentCache.get(controller);
        } else {
            compartment = getCompartmentFromController(controller);
            controllerCompartmentCache.put(key, compartment);
        }

        return compartment;
    }

    protected String getCompartmentFromController(GroovyObject controller) {
        Class<?> controllerClass = controller.getClass();
        String compartment = (String) GrailsClassUtils.getStaticPropertyValue(controllerClass, COMPARTMENT);
        compartment = (compartment != null) ? compartment : NO_COMPARTMENT;

        log.debug("Found compartment {} for controller {}", compartment, controllerClass.getSimpleName());
        return compartment;
    }

    protected String getViewNameWithCompartment(String viewName, String compartment) {
        return compartment == NO_COMPARTMENT ? viewName : '/' + compartment + viewName;
    }

    public void setOriginalViewResolver(ViewResolver originalViewResolver) {
        this.originalViewResolver = originalViewResolver;
    }
    
}
