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
	public boolean equalsContent(Object other) {
		// TODO Auto-generated method stub
		return super.equalsContent(other);
	}
	
	@Override
	public boolean equals(Object other) {
		
		if(!super.equals(other))
			return false;
		
		if (!(other instanceof AddressBody))
		        return false;
		
		AddressBody that = (AddressBody) other;
		
		if(this.getAddress() == null)
			return that.getAddress() == null; 
		else 
			return this.getAddress().equals(that.getAddress()); 
		
	}
	
}
