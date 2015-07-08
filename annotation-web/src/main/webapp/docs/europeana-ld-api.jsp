<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
					<div id="table" style="width: 100%;">	
					<table>
						<thead>
						<tr>
							<td class="clsHeaderCell">Method</td>
							<td class="clsHeaderCell">Object Type</td>
							<td class="clsHeaderCell">Response</td>
							<td class="clsHeaderCell">Path</td>
							<td class="clsHeaderCell">Function</td>
						</tr>
						</thead>
						<tr>
							<td class="clsCellBorder cls0_0" valign="top">POST</td>
							<td class="clsCellBorder" valign="top">SimpleTag</td>
							<td class="clsCellBorder" valign="top">JSON_LD</td>
							<td class="clsCellBorder">
									[annotation-web]/annotation/{motivation}/create.jsonld<br>
									Object as JSON-LD:
								<form action="annotation/create.jsonld" method="post" target="_new">
								<input type="hidden" name="wskey" value="key">
								<input type="hidden" name="profile" value="webtest">
								<textarea rows="20" cols="60" name="annotation" >
{
    "@context": "http://www.europeana.eu/annotation/context.jsonld",
    "@type": "oa:Annotation",
    "@id": "http://data.europeana.eu/annotation/historypin/456",
    "annotatedBy": {
        "@id": "https://www.historypin.org/en/person/55376/",
        "@type": "foaf:Person",
        "name": "John Smith"
    },
    "annotatedAt": "2015-02-27T12:00:43Z",
    "serializedAt": "2015-02-28T13:00:34Z",
    "serializedBy": "http://www.historypin.org",
    "body": "church",
    "target": "http://data.europeana.eu/item/123/xyz",
    "oa:equivalentTo": "https://www.historypin.org/en/item/456"
}
</textarea>
								<br>
								</form>
							</td>
							<td class="clsCellBorder" valign="top">
								Simple Tagging (when motivation is 'oa:tagging' and body is a string literal). Create an europeana annotation (SimpleTag) and return the object as stored in the database for 'historypin' provider. 
								Identification of annotation types works as follows. 
							</td>
						</tr>						

						<tr>
							<td class="clsCellBorder cls0_0" valign="top">POST</td>
							<td class="clsCellBorder" valign="top">SimpleLink</td>
							<td class="clsCellBorder" valign="top">JSON_LD</td>
							<td class="clsCellBorder">
									[annotation-web]/annotation/{motivation}/create.jsonld<br>
									Object as JSON-LD:
								<form action="annotation/create.jsonld" method="post" target="_new">
								<input type="hidden" name="wskey" value="key">
								<input type="hidden" name="profile" value="webtest">
								<textarea rows="20" cols="60" name="annotation" >
{
    "@context": "http://www.europeana.eu/annotation/context.jsonld",
    "@type": "oa:Annotation",
    "annotatedBy": {
        "@id": "https://www.historypin.org/en/person/55376/",
        "@type": "foaf:Person",
        "name": "John Smith"
    },
    "annotatedAt": "2015-02-27T12:00:43Z",
    "serializedAt": "2015-02-28T13:00:34Z",
    "serializedBy": "http://www.historypin.org",
    "target": [
        "http://www.europeana.eu/portal/record/123/xyz.html", 
        "http://www.europeana.eu/portal/record/333/xxx.html"
    ],
    "oa:equivalentTo": "https://www.historypin.org/en/item/456"
}
</textarea>
								<br>
								</form>
							</td>
							<td class="clsCellBorder" valign="top">
								Simple Object Linking (when motivation is '€œoa:linking'€ and no body is provided and target contains an array with at least 2 urls). 
								Create an europeana annotation (SimpleLink) and return the object as stored in the database for 'historypin' provider. 
							</td>
						</tr>						

						<tr>
							<td class="clsCellBorder cls0_0" valign="top">POST</td>
							<td class="clsCellBorder" valign="top">SimpleTag</td>
							<td class="clsCellBorder" valign="top">JSON_LD</td>
							<td class="clsCellBorder">
									[annotation-web]/annotation/{motivation}/create.jsonld<br>
									Object as JSON-LD:
								<form action="annotation/create.jsonld" method="post" target="_new">
								<input type="hidden" name="wskey" value="key">
								<input type="hidden" name="profile" value="webtest">
								<textarea rows="20" cols="60" name="annotation" >
{
    "@context": "http://www.europeana.eu/annotation/context.jsonld",
    "@type": "oa:Annotation",
    "annotatedBy": {
        "@id": "https://www.historypin.org/en/person/55376/",
        "@type": "foaf:Person",
        "name": "John Smith"
    },
    "annotatedAt": "2015-02-27T12:00:43Z",
    "serializedAt": "2015-02-28T13:00:34Z",
    "serializedBy": "http://www.historypin.org",
    "body": {
        "@type": [
        	"oa:Tag",
        	"cnt:ContentAsText",
        	"dctypes:Text"
        ],
        "chars": "Vlad Tepes",
        "format": "text/plain",
        "language": "ro"
    },
    "target": "http://data.europeana.eu/item/123/xyz",
    "oa:equivalentTo": "https://www.historypin.org/en/item/456"
}
</textarea>
								<br>
								</form>
							</td>
							<td class="clsCellBorder" valign="top">
								Simple Tagging (when motivation is 'oa:tagging'€ and body type is a string list). 
								Create an europeana annotation (SimpleTag) and return the object as stored in the database for 'historypin' provider. 								
							</td>
						</tr>											

						<tr>
							<td class="clsCellBorder cls0_0" valign="top">POST</td>
							<td class="clsCellBorder" valign="top">SimpleTag</td>
							<td class="clsCellBorder" valign="top">JSON_LD</td>
							<td class="clsCellBorder">
									[annotation-web]/annotation/{motivation}/create.jsonld<br>
									Object as JSON-LD:
								<form action="annotation/create.jsonld" method="post" target="_new">
								<input type="hidden" name="wskey" value="key">
								<input type="hidden" name="profile" value="webtest">
								<textarea rows="20" cols="60" name="annotation" >
{
 "@context": "http://www.europeana.eu/annotation/context.jsonld",
 "@type": "oa:Annotation", 
 "annotatedBy": {
   "@id": "https://www.historypin.org/en/person/55376/",
   "@type": "foaf:Person",
   "name": "John Smith"
 },
 "annotatedAt": "2015-02-27T12:00:43Z",
 "serializedAt": "2015-02-28T13:00:34Z",
 "serializedBy": "http://data.europeana.eu/provider/Historypin",
 "body": ["church", "orthodox"] ,
 "target": "http://data.europeana.eu/item/123/xyz",
 "oa:equivalentTo": "https://www.historypin.org/en/item/456"
}
</textarea>
								<br>
								</form>
							</td>
							<td class="clsCellBorder" valign="top">
								Multiple Simple Tags (when motivation is '€œoa:tagging'€ and body is a string array).
							</td>
						</tr>						

						<tr>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
						</tr>
					</table>
					
					</div>
					<!--  end table div -->	

</body>
</html>