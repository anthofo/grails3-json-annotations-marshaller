import json.annotations.marshaller.JsonApi

class ViciousPet extends Pet {
	
	@JsonApi('detailedInformation')
	Integer licenceNumber

}