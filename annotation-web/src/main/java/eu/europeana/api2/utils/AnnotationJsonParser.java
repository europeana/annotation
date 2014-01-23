package eu.europeana.api2.utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.codehaus.jackson.Base64Variant;
import org.codehaus.jackson.JsonLocation;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonStreamContext;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.ObjectCodec;

public class AnnotationJsonParser extends JsonParser {

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public BigInteger getBigIntegerValue() throws IOException,
			JsonParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getBinaryValue(Base64Variant arg0) throws IOException,
			JsonParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObjectCodec getCodec() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonLocation getCurrentLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCurrentName() throws IOException, JsonParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal getDecimalValue() throws IOException, JsonParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getDoubleValue() throws IOException, JsonParseException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getFloatValue() throws IOException, JsonParseException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getIntValue() throws IOException, JsonParseException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getLongValue() throws IOException, JsonParseException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public NumberType getNumberType() throws IOException, JsonParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Number getNumberValue() throws IOException, JsonParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonStreamContext getParsingContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getText() throws IOException, JsonParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public char[] getTextCharacters() throws IOException, JsonParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTextLength() throws IOException, JsonParseException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTextOffset() throws IOException, JsonParseException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public JsonLocation getTokenLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isClosed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public JsonToken nextToken() throws IOException, JsonParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCodec(ObjectCodec arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public JsonParser skipChildren() throws IOException, JsonParseException {
		// TODO Auto-generated method stub
		return null;
	}

}
