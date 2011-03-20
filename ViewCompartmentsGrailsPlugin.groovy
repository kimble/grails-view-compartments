import grails.plugin.viewcompartment.ViewResolverPostProcessor
import grails.plugin.viewcompartment.CompartmentAwareViewResolver
import org.codehaus.groovy.grails.commons.ControllerArtefactHandler

class ViewCompartmentsGrailsPlugin {

    def version = "0.4"
    def grailsVersion = "1.3.0 > *"
    def dependsOn = [:]

    def observe = [ "controllers" ]
    
    def pluginExcludes = [
            "grails-app/views/**",
            "grails-app/controllers/**"
    ]

    def author = "Kim A. Betti"
    def authorEmail = "kim@developer-b.com"
    def title = "View compartments (GRAILS-1243)"
    def description = "See http://jira.codehaus.org/browse/GRAILS-1243 for background information"

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/view-compartments"

    def doWithSpring = {
        compartmentAwareViewResolver(CompartmentAwareViewResolver) {
            grailsApplication = ref("grailsApplication")
        }
        
        viewResolverPostProcessor(ViewResolverPostProcessor) {
            compartmentAwareViewResolver = ref("compartmentAwareViewResolver")
        }
    }
    
    def onChange = { event -> 
        if (application.isArtefactOfType(ControllerArtefactHandler.TYPE, event.source)) {
            application.mainContext.compartmentAwareViewResolver.cleanCompartmentCache();
        }
    }

    def doWithWebDescriptor = { xml -> }
    def doWithDynamicMethods = { ctx -> }
    def doWithApplicationContext = { applicationContext -> }
    def onConfigChange = { event -> }
    
}
