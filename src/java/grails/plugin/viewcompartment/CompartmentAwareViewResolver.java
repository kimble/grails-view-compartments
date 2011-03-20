package grails.plugin.viewcompartment;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.codehaus.groovy.grails.commons.ControllerArtefactHandler;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.commons.GrailsClass;
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

    private GrailsApplication grailsApplication;
    private ViewResolver originalViewResolver;

    @Override
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        String compartment = getCompartment(viewName);
        viewName = getViewNameWithCompartment(viewName, compartment);
        return originalViewResolver.resolveViewName(viewName, locale);
    }

    protected String getCompartment(String viewName) {
        GrailsWebRequest webRequest = WebUtils.retrieveGrailsWebRequest();
        String controllerName = webRequest.getControllerName();
        return controllerName == null ? viewName : getAndUpdateCompartmentFromCache(controllerName);
    }

    protected String getAndUpdateCompartmentFromCache(String controllerName) {
        String compartment = NO_COMPARTMENT;
        if (controllerCompartmentCache.containsKey(controllerName)) {
            compartment = controllerCompartmentCache.get(controllerName);
        } else {
            compartment = getCompartmentFromControllerName(controllerName);
            controllerCompartmentCache.putIfAbsent(controllerName, compartment);
        }

        return compartment;
    }

    protected String getCompartmentFromControllerName(String controllerName) {
        String type = ControllerArtefactHandler.TYPE;
        String compartment = NO_COMPARTMENT;
        GrailsClass controller =  grailsApplication.getArtefactByLogicalPropertyName(type, controllerName);
        if (controller != null) {
            Class<?> controllerClass = controller.getClazz();
            compartment = getCompartmentFromControllerClass(controllerClass);
        } else {
            log.debug("Unable to find controller for {}", controllerName);
        }

        return compartment;
    }

    protected String getCompartmentFromControllerClass(Class<?> controllerClass) {
        String compartment = (String) GrailsClassUtils.getStaticPropertyValue(controllerClass, COMPARTMENT);
        compartment = (compartment != null) ? compartment : NO_COMPARTMENT;

        log.debug("Found compartment {} for controller {}", compartment, controllerClass.getName());
        return compartment;
    }

    protected String getViewNameWithCompartment(String viewName, String compartment) {
        return compartment == NO_COMPARTMENT ? viewName : '/' + compartment + viewName;
    }

    public void setGrailsApplication(GrailsApplication grailsApplication) {
        this.grailsApplication = grailsApplication;
    }

    public void setOriginalViewResolver(ViewResolver originalViewResolver) {
        this.originalViewResolver = originalViewResolver;
    }

}
