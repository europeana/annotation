package eu.europeana.annotation.definitions.model;

public class BaseAddress implements Address {

    private String about;
    private String streetAddress;
    private String postalCode;
    private String postBox;
    private String locality;
    private String countryName;
    private String hasGeo;
    
    
    @Override
    public void setVcardPostOfficeBox(String vcardPostOfficeBox) {
    this.postBox = vcardPostOfficeBox;
    }

    @Override
    public String getVcardPostOfficeBox() {
    return postBox;
    }

    @Override
    public void setVcardCountryName(String vcardCountryName) {
    this.countryName = vcardCountryName;
    }

    @Override
    public String getVcardCountryName() {
    return countryName;
    }

    @Override
    public void setVcardPostalCode(String vcardPostalCode) {
    this.postalCode = vcardPostalCode;
    }

    @Override
    public String getVcardPostalCode() {
    return postalCode;
    }

    @Override
    public void setVcardLocality(String vcardLocality) {
    this.locality = vcardLocality;
    }

    @Override
    public String getVcardLocality() {
    return locality;
    }

    @Override
    public void setVcardStreetAddress(String vcardStreetAddress) {
    this.streetAddress = vcardStreetAddress;
    }

    @Override
    public String getVcardStreetAddress() {
    return streetAddress;
    }

    
    @Override
    public void setAbout(String about) {
    this.about = about;
    }

    @Override
    public String getAbout() {
    return about;
    }

    @Override
    public String getVcardHasGeo() {
    return hasGeo;
    }

    @Override
    public void setVcardHasGeo(String hasGeo) {
    this.hasGeo = hasGeo;
    }

}

