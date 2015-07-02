package org.grails.json.annotations

import grails.core.DefaultGrailsApplication
import grails.core.GrailsApplication
import org.grails.core.DefaultGrailsDomainClass
import org.grails.json.annotations.domain.Pet
import org.grails.json.annotations.domain.User
import spock.lang.*

class JsonApiRegistrySpec extends Specification {
	DefaultGrailsDomainClass domainClass
	GrailsApplication grailsApplication
	JsonApiRegistry registry
	
	def setup() {
		domainClass = new DefaultGrailsDomainClass(Pet)
		grailsApplication = new DefaultGrailsApplication()
		grailsApplication.metaClass.domainClasses = [domainClass]
		registry = new JsonApiRegistry()
	}

	def "during live reloads .updateMarshallers should mark registered but unannotated marshallers as deleted"() {
		given: 'a registry with a registered marshaller not present in the annotations'
		def marshaller = new AnnotationMarshaller(domainClass, 'non-existant-api')
		registry.marshallersByApi['non-existant-api'] << marshaller
		
		when: 'updating marshallers from annotations'
		registry.updateMarshallers(grailsApplication)
		
		then: 'no-longer existing marshaller is marked as deleted'
		marshaller.deleted
	}
	
	def "during live reloads .updateMarshallers should add any annotated marshallers to the existing ones"() {
		given: 'marshallers registered for a domain class'
		registry.updateMarshallers(grailsApplication)
		
		when: 'another class is scanned for declared APIs'
		def anotherDomainClass = new DefaultGrailsDomainClass(User)
		grailsApplication.domainClasses = [domainClass, anotherDomainClass]
		registry.updateMarshallers(grailsApplication)
		
		then: 'new class should have its marshallers added to the registry'
		registry.marshallersByApi.any { api, marshallers ->
			marshallers.any { it.forClass == anotherDomainClass }
		}
		
		and: 'marshallers of the old class should remain in the registry'
		registry.marshallersByApi.any { api, marshallers ->
			marshallers.any { it.forClass == anotherDomainClass }
		}
	}
	
	def "during live reloads .updateMarshallers should rescan which properties to serialize"() {
		given: 'an API registry with a mocked marshaller'
		AnnotationMarshaller marshaller = Mock(constructorArgs:[domainClass, 'petDetails'])
		registry.marshallersByApi['petDetails'] << marshaller
		
		when:
		registry.updateMarshallers(grailsApplication)
		
		then:
		1 * marshaller.initPropertiesToSerialize()
	}
}