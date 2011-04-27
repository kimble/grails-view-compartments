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
    
    def "Successive lookups for the same controller should be served from cache"() {
        // TODO: Implement me
    }
    
}

class CompartmentedController {
    static compartment = "my-compartment"
}
