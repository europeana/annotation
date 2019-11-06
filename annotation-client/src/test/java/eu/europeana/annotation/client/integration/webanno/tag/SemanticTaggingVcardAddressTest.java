package eu.europeana.annotation.client.integration.webanno.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.body.impl.VcardAddressBody;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class SemanticTaggingVcardAddressTest extends BaseTaggingTest {

	protected Logger log = LogManager.getLogger(getClass());
	
	@Test
	public void createSemanticTagWithVcardAddress() throws IOException, JsonParseException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		Annotation storedAnno = createTag(SEMANTICTAG_VCARD_ADDRESS, false);
		log.info(storedAnno.getBody().getInternalType());
		assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
		assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.VCARD_ADDRESS.name());
		assertEquals(
				((VcardAddressBody) storedAnno.getBody()).getAddress().getVcardCountryName()
				,"The Netherlands");
		assertNotNull(
				((VcardAddressBody) storedAnno.getBody()).getAddress().getVcardHasGeo());
		assertNotNull(
				((VcardAddressBody) storedAnno.getBody()).getAddress().getVcardLocality());
		assertNotNull(
				((VcardAddressBody) storedAnno.getBody()).getAddress().getVcardPostalCode());
		assertNotNull(
				((VcardAddressBody) storedAnno.getBody()).getAddress().getVcardPostOfficeBox());
		assertNotNull(
				((VcardAddressBody) storedAnno.getBody()).getAddress().getVcardStreetAddress());
	}

}
