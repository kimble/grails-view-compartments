import grails.plugin.viewcompartment.ViewResolverPostProcessor
import grails.plugin.viewcompartment.CompartmentAwareViewResolver

class ViewCompartmentsGrailsPlugin {

    def version = "0.1"
    def grailsVersion = "1.3.0 > *"
    def dependsOn = [:]

    def pluginExcludes = [
            "grails-app/views/**",
            "grails-app/controllers/**"
    ]

    def author = "Kim A. Betti"
    def authorEmail = "kim@developer-b.com"
    def title = "View compartments"
    def description = '''\\
Brief description of the plugin.
'''

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

    def doWithWebDescriptor = { xml -> }
    def doWithDynamicMethods = { ctx -> }
    def doWithApplicationContext = { applicationContext -> }
    def onChange = { event -> }
    def onConfigChange = { event -> }
    
}
