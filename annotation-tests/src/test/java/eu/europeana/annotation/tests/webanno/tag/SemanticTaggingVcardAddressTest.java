package eu.europeana.annotation.tests.webanno.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.body.impl.VcardAddressBody;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

@SpringBootTest
@AutoConfigureMockMvc
public class SemanticTaggingVcardAddressTest extends BaseTaggingTest {

	protected Logger log = LogManager.getLogger(getClass());
	
	@Test
	public void createSemanticTagWithVcardAddress() throws Exception {

		Annotation storedAnno = createTag(SEMANTICTAG_VCARD_ADDRESS, false, true);
		createdAnnotations.add(storedAnno.getIdentifier());
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
