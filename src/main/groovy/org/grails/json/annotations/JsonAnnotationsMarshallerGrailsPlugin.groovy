package org.grails.json.annotations

import grails.plugins.*

class JsonAnnotationsMarshallerGrailsPlugin extends Plugin {

    def version = "1.0"

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.0.1 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/**",
            "grails-app/domain/**",
            "**/org/grails/json/annotations/domain/**",
            "grails-app/controllers/**",
            "grails-app/i18n/**",
            "web-app/**",
            "src/test/**",
            "demo-output.html"
    ]

    // TODO Fill in these fields
    def title = "Json Annotations Marshaller" // Headline display name of the plugin
    def author = "Anthony Foulfoin"
    def authorEmail = "anthony.foulfoin@gmail.com"
    def description = '''\
Allows developers to declaratively define various JSON serialization
profiles and use them to marshall Grails domain classes at different
levels of detail or from different starting points in the object
graph. Adapted from the Gregor Petrin grails 2 json-apis plugin
'''
    def profiles = ['web']

    // URL to the plugin's documentation
    def documentation = "https://github.com/tony75/grails3-json-annotations-marshaller"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
    def developers = [ [ name: "Gregor Petrin", email: "gregap@gmail.com" ]]

    // Location of the plugin's issue tracker.
    def issueManagement = [ system: "github", url: "https://github.com/tony75/grails3-json-annotations-marshaller/issues" ]

    // Online location of the plugin's browseable source code.
    def scm = [ url: "https://github.com/tony75/grails3-json-annotations-marshaller" ]

    def watchedResources = "file:./grails-app/domain/**.groovy"

    def jsonApiRegistry = new JsonApiRegistry()

    Closure doWithSpring() { {->
            // TODO Implement runtime spring config (optional)
        } 
    }

    void doWithDynamicMethods() {
        // TODO Implement registering dynamic methods to classes (optional)
    }

    void doWithApplicationContext() {
        //Generate and register the required ObjectMarshaller instances.
        jsonApiRegistry.updateMarshallers(grailsApplication)
    }

    void onChange(Map<String, Object> event) {
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
        //Update ObjectMarshallers
        jsonApiRegistry.updateMarshallers(event.application)
    }

    void onConfigChange(Map<String, Object> event) {
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    void onShutdown(Map<String, Object> event) {
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
