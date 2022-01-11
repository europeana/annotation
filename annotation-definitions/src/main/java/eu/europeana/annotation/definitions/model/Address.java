package eu.europeana.annotation.definitions.model;

public interface Address {

	void setVcardPostOfficeBox(String vcardPostOfficeBox);

	String getVcardPostOfficeBox();

	void setVcardCountryName(String vcardCountryName);

	String getVcardCountryName();

	void setVcardPostalCode(String vcardPostalCode);

	String getVcardPostalCode();

	void setVcardLocality(String vcardLocality);

	String getVcardLocality();

	void setVcardStreetAddress(String vcardStreetAddress);

	String getVcardStreetAddress();

	void setAbout(String about);

	String getAbout();

	void setVcardHasGeo(String vcardHasGeo);

	String getVcardHasGeo();

}
