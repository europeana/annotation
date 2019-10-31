package eu.europeana.annotation.definitions.model.body.impl;

import eu.europeana.api.commons.definitions.entities.BaseAddress;
import eu.europeana.corelib.definitions.edm.entity.Address;

/**
 * This is a class for semantic tag with Vcard address body.
 * 
 * @author GrafR
 *
 */
public class VcardAddressBody extends BaseBody implements AddressBody {

	Address address = new BaseAddress();

	@Override
	public Address getAddress() {
		return address;
	}

	@Override
	public void setAddress(Address address) {
		this.address = address;
	}
			
	@Override
	public boolean equalsContent(Object addressBodyContent) {
		return super.equalsContent(addressBodyContent);
	}
	
	@Override
	public boolean equals(Object addressBody) {
		
		if(!super.equals(addressBody))
			return false;
		
		if (!(addressBody instanceof AddressBody))
		        return false;
		
		AddressBody addressBodyObj = (AddressBody) addressBody;
		
		if(this.getAddress() == null)
			return addressBodyObj.getAddress() == null; 
		else 
			return this.getAddress().equals(addressBodyObj.getAddress()); 
		
	}
	
}
