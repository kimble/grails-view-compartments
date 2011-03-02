package grails.plugin.viewcompartment

import grails.plugin.spock.UnitSpec

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsClass
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.servlet.ViewResolver

/**
 * 
 * @author Kim A. Betti
 */
class CompartmentAwareViewResolverSpec extends UnitSpec {

    CompartmentAwareViewResolver viewResolver = new CompartmentAwareViewResolver()
    
    def "controller cache"() {
        given: "mocked grails web request context"
        GrailsWebRequest requestAttr = Mock(GrailsWebRequest)
        RequestContextHolder.setRequestAttributes(requestAttr)
        2 * requestAttr.getControllerName() >> "compartmented"
        
        and: "a mocked grails application for artefact lookups"
        viewResolver.grailsApplication = Mock(GrailsApplication)
        
        and: "the original view resolver"
        viewResolver.originalViewResolver = Mock(ViewResolver)
        
        and: "a mocked grails class representing a compartmented controller"
        GrailsClass grailsControllerClass = Mock()
        
        when: "we do the first lookup"
        viewResolver.resolveViewName("/compartmented/index", null)
        
        then: "the cache does not contain the controller name so we have to look up the artefact using grailsApplication"
        1 * viewResolver.grailsApplication.getArtefactByLogicalPropertyName("Controller", "compartmented") >> grailsControllerClass
        
        and: "return a dummy controller with a static compartment property"
        1 * grailsControllerClass.getClazz() >> CompartmentedController
      
        and: "the compartmented view resolve request is delegated to the original jspViewResolver bean"
        1 * viewResolver.originalViewResolver.resolveViewName("/my-compartment/compartmented/index", _);
        
        when: "we do a lookup for the same controller"
        viewResolver.resolveViewName("/compartmented/list", null)
        
        then: "the cache is warm so we don't have to hit grailsApplication"
        0 * viewResolver.grailsApplication.getArtefactByLogicalPropertyName(_, _)
        
        and: "the resolve request is delegated again"
        1 * viewResolver.originalViewResolver.resolveViewName("/my-compartment/compartmented/list", _);
    }
    
}

class CompartmentedController {
    static compartment = "my-compartment"
}
