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
							<td class="clsCellBorder" valign="top">SemanticTag</td>
							<td class="clsCellBorder" valign="top">JSON</td>
							<td class="clsCellBorder">
									[annotation-web]/annotations/{collection}/{object_hash}.json<br>
									ObjectTag as JSON:
								<form action="annotations/15502/GG_8285.json" method="post" target="_new">
								<input type="hidden" name="wskey" value="key">
								<input type="hidden" name="profile" value="webtest">
								<textarea rows="20" cols="60" name="annotation" >
{
"annotatedAt": 1403852113248,
"type": "OBJECT_TAG",
"annotatedBy": {
    "agentType": "[foaf:Person, euType:PERSON]",
    "name": "annonymous web user",
    "homepage": null,
    "mbox": null,
    "openId": null
},
"body": {
    "contentType": "Link",
    "mediaType": null,
    "httpUri": "https://www.freebase.com/m/035br4",
    "language": "ro",
    "value": "Vlad Tepes",
    "multilingual": "[ro:Vlad Tepes,en:Vlad the Impaler]",
    "bodyType": "[oa:Tag,euType:SEMANTIC_TAG]"
},
"target": {
    "contentType": "text-html",
    "mediaType": "image",
    "language": "en",
    "value": "Vlad IV. Tzepesch, der Pfaehler, Woywode der Walachei 1456-1462 (gestorben 1477)",
    "httpUri": "http://europeana.eu/portal/record/15502/GG_8285.html",
    "targetType": "[euType:WEB_PAGE]"
},
"serializedAt": "",
"serializedBy": {
    "agentType": "[prov:SoftwareAgent,euType:SOFTWARE_AGENT]",
    "name": "annonymous web user",
    "homepage": null,
    "mbox": null,
    "openId": null
},
"styledBy":{
    "contentType": "style",
    "mediaType": "text/css",
    "httpUri": "http://annotorious.github.io/latest/themes/dark/annotorious-dark.css",
    "value": null,
    "annotationClass": ".annotorious-popup"
}
}
</textarea>
								<br>
								</form>
							</td>
							<td class="clsCellBorder" valign="top">
								create a semantic tag (BodyType) for 'webanno' provider and return the object as stored in the database. Whereas, 'resourceId' is extracted from 'target.httpUri' and 'annotationNr' is generated.
							</td>
						</tr>
						
<tr>
							<td class="clsCellBorder cls0_0" valign="top">POST</td>
							<td class="clsCellBorder" valign="top">SemanticTag</td>
							<td class="clsCellBorder" valign="top">JSON</td>
							<td class="clsCellBorder">
									[annotation-web]/annotations/{collection}/{object_hash}.json<br>
									ObjectTag as JSON:
								<form action="annotations/15502/GG_8285.json" method="post" target="_new">
								<input type="hidden" name="wskey" value="key">
								<input type="hidden" name="profile" value="webtest">
								<textarea rows="20" cols="60" name="annotation" >
{
"annotatedAt": 1403852113248,
"type": "OBJECT_TAG",
"sameAs": "http://historypin.com/annotation/1234",
"equivalentTo": "http://historypin.com/annotation/1234",
"annotatedBy": {
    "agentType": "[foaf:Person, euType:PERSON]",
    "name": "annonymous web user",
    "homepage": null,
    "mbox": null,
    "openId": null
},
"body": {
    "contentType": "Link",
    "mediaType": null,
    "httpUri": "https://www.freebase.com/m/035br4",
    "language": "ro",
    "value": "Vlad Tepes",
    "multilingual": "[ro:Vlad Tepes,en:Vlad the Impaler]",
    "bodyType": "[oa:Tag,euType:SEMANTIC_TAG]"
},
"target": {
    "contentType": "text-html",
    "mediaType": "image",
    "language": "en",
    "value": "Vlad IV. Tzepesch, der Pfaehler, Woywode der Walachei 1456-1462 (gestorben 1477)",
    "httpUri": "http://europeana.eu/portal/record/15502/GG_8285.html",
    "targetType": "[euType:WEB_PAGE]"
},
"serializedAt": "",
"serializedBy": {
    "agentType": "[prov:SoftwareAgent,euType:SOFTWARE_AGENT]",
    "name": "annonymous web user",
    "homepage": null,
    "mbox": null,
    "openId": null
},
"styledBy":{
    "contentType": "style",
    "mediaType": "text/css",
    "httpUri": "http://annotorious.github.io/latest/themes/dark/annotorious-dark.css",
    "value": null,
    "annotationClass": ".annotorious-popup"
}
}
</textarea>
								<br>
								</form>
							</td>
							<td class="clsCellBorder" valign="top">
								create a semantic tag (BodyType) for 'historypin' provider and return the object as stored in the database. Note that field 'sameAs' is employed for that task.
							</td>
						</tr>
																	
<tr>
							<td class="clsCellBorder cls0_0" valign="top">POST</td>
							<td class="clsCellBorder" valign="top">SimpleTag</td>
							<td class="clsCellBorder" valign="top">JSON</td>
							<td class="clsCellBorder">
									[annotation-web]/annotations/{collection}/{object_hash}.json<br>
									ObjectTag as JSON:
								<form action="annotations/15502/GG_8285.json" method="post" target="_new">
								<input type="hidden" name="wskey" value="key">
								<input type="hidden" name="profile" value="webtest">
								<textarea rows="20" cols="60" name="annotation" >
{
"annotatedAt": 1403852113248,
"type": "OBJECT_TAG",
"annotatedBy": {
    "agentType": "[foaf:Person, euType:PERSON]",
    "name": "annonymous web user",
    "homepage": null,
    "mbox": null,
    "openId": null 
},
"body": {
    "contentType": "text",
    "mediaType": null,
	"httpUri": null,
    "language": "ro",
    "value": "Vlad Tepes",
    "bodyType": "[oa:Tag, euType:TAG]"
},
"target": {
    "contentType": "text-html",
    "mediaType": "image",
    "language": "de",
    "value": "Vlad IV. Tzepesch, der Pfaehler, Woywode der Walachei 1456-1462 (gestorben 1477)",
    "httpUri": "http://europeana.eu/portal/record/15502/GG_8285.html",
    "targetType": "[euType:WEB_PAGE]"
},
"serializedAt": "",
"serializedBy": {
    "agentType": "[prov:SoftwareAgent,euType:SOFTWARE_AGENT]",
    "name": "annonymous web user",
    "homepage": null,
    "mbox": null,
    "openId": null 
},
"styledBy":{
 	"contentType": "style",
    "mediaType": "text/css",
	"httpUri": "http://annotorious.github.io/latest/themes/dark/annotorious-dark.css",
	"value": null,
    "annotationClass": ".annotorious-popup"
}
}</textarea>
								<br>
								</form>
							</td>
							<td class="clsCellBorder" valign="top">
								create a simple tag (BodyType) and return the object as stored in the database
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