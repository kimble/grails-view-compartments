package grails.plugin.viewcompartment;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.codehaus.groovy.grails.commons.ControllerArtefactHandler;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.commons.GrailsClass;
import org.codehaus.groovy.grails.commons.GrailsClassUtils;
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest;
import org.codehaus.groovy.grails.web.util.WebUtils;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

/**
 * Checks whether the controller has a static compartment property.
 * If it doesn't we just delegate the resolve operation to the original jspViewResolver bean.
 * @author Kim A. Betti
 */
public class CompartmentAwareViewResolver implements ViewResolver {

    public static final String COMPARTMENT = "compartment";

    private Map<String, String> controllerCompartmentCache = new HashMap<String, String>();

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
        String compartment = null;
        if (controllerCompartmentCache.containsKey(controllerName)) {
            compartment = controllerCompartmentCache.get(controllerName);
        } else {
            compartment = getCompartmentFromController(controllerName);
            controllerCompartmentCache.put(controllerName, compartment);
        }

        return compartment;
    }

    protected String getCompartmentFromController(String controllerName) {
        String type = ControllerArtefactHandler.TYPE;
        GrailsClass controller =  grailsApplication.getArtefactByLogicalPropertyName(type, controllerName);
        return (String) GrailsClassUtils.getStaticPropertyValue(controller.getClazz(), COMPARTMENT);
    }

    protected String getViewNameWithCompartment(String viewName, String compartment) {
        return compartment == null ? viewName : '/' + compartment + viewName;
    }

    public void setGrailsApplication(GrailsApplication grailsApplication) {
        this.grailsApplication = grailsApplication;
    }

    public void setOriginalViewResolver(ViewResolver originalViewResolver) {
        this.originalViewResolver = originalViewResolver;
    }

}
