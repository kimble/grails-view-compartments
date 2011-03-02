View compartments
=================

Add support for grouping views. 
See [GRAILS-1243][http://jira.codehaus.org/browse/GRAILS-1243] for background information.

Example
-------
    class UserAdminController {
        
        static compartment = "admin"

        // The view should be placed in grails-app/views/admin/userAdmin/index.gsp        
        def index = {
            // ...
        }
        
    }
    
Known issues
------------

Reload support has not been implemented. Controller compartment names are cached so you have to restart your application if you change a compartment name. 