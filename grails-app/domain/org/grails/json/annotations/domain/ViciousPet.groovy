package org.grails.json.annotations.domain

import org.grails.json.annotations.JsonApi

class ViciousPet extends Pet {
	
	@JsonApi('detailedInformation')
	Integer licenceNumber

}