package org.grails.json.annotations

import grails.core.GrailsApplication
import groovy.util.logging.Log

import grails.converters.JSON

/**
 * Keeps track of all the JSON APIs registered by the plugin. Constains methods
 * that are called on live reload events to update the APIs.
 */
@Log
class JsonApiRegistry {
	final Map marshallersByApi
	
	JsonApiRegistry() {
		marshallersByApi = [:].withDefault { [] }
	}
	
	/**
	 * Updates the state of all registered marshallers, adding new ones or
	 * deleting existing (in case of a live reload).
	 */
	void updateMarshallers(GrailsApplication application) {
		def allApiNames = getAllApiNames(application.domainClasses)

        // Register a JSON serializer "common" that will only serialize the properties flagged with '@JsonApi' annotation with no name parameter
        JSON.createNamedConfig('common') { JSON ->
            for (domainClass in application.domainClasses) {
                JSON.registerObjectMarshaller(new AnnotationMarshaller<JSON>(domainClass, 'common'))
            }
        }

		def newApis = allApiNames - marshallersByApi.keySet()
		newApis.each { namespace ->
			JSON.createNamedConfig(namespace) { JSON ->
				for (domainClass in application.domainClasses) {
					def marshaller = new AnnotationMarshaller<JSON>(domainClass, namespace)
					JSON.registerObjectMarshaller(marshaller)
					marshallersByApi[namespace] << marshaller
                    JSON.registerObjectMarshaller(Enum, { Enum e -> e.name() })
				}
			}
		}
		
		def deletedApis = marshallersByApi.keySet() - allApiNames
		deletedApis.each { String apiName ->
			marshallersByApi[apiName]*.deleted = true
		}
		
		def remainingApis = allApiNames - deletedApis - newApis
		remainingApis.each { String apiName ->
			marshallersByApi[apiName]*.deleted = false
			marshallersByApi[apiName]*.initPropertiesToSerialize()
		}
	}
	
	/**
	 * Finds all API names defined in all domain classes.
	 * @return A set of all found namespace names.
	 */
	Set getAllApiNames(domainClasses) {
		Set namespaces = []
		domainClasses.each { domainClass ->
			domainClass.properties.each { groovyProperty ->
				def declaredNamespaces = getPropertyAnnotationValue( domainClass.clazz, groovyProperty.name )?.value()
				declaredNamespaces?.each { apiNamespace -> namespaces << apiNamespace }
			}
		}
		return namespaces
	}
	
	/**
	 * Finds the method or field corresponding to a Groovy property name and its JsonApi annotation if it exists.
	 */
	static JsonApi getPropertyAnnotationValue(Class clazz, String propertyName) {
		while (clazz) {
			def fieldOrMethod = clazz.declaredFields.find { it.name == propertyName } ?: clazz.declaredMethods.find { it.name == 'get' + propertyName.capitalize() }
			if (fieldOrMethod) return fieldOrMethod?.getAnnotation(JsonApi)
			else clazz = clazz.superclass
		}
		return null
	}


}