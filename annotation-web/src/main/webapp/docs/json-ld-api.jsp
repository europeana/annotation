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
							<td class="clsCellBorder" valign="top">ObjectTag</td>
							<td class="clsCellBorder" valign="top">JSON_LD</td>
							<td class="clsCellBorder">
									[annotation-web]/annotationld/{collection}/{object_hash}.jsonld<br>
									Object as JSON-LD:
								<form action="annotations/15502/GG_8285.jsonld" method="post" target="_new">
								<input type="hidden" name="wskey" value="key">
								<input type="hidden" name="profile" value="webtest">
								<textarea rows="20" cols="60" name="annotation" >
{
    "@context": {
        "oa": "http://www.w3.org/ns/oa-context-20130208.json"
    },
    "@type": "oa:annotation",
    "annotatedAt": "2012-11-10T09:08:07",
    "annotatedBy": {
        "@id": "open_id_1",
        "@type": "foaf:Person",
        "name": "annonymous web user"
    },
    "body": {
        "@type": "[oa:Tag,cnt:ContentAsText,dctypes:Text,euType:SEMANTIC_TAG]",
        "chars": "Vlad Tepes",
        "foaf:page": "https://www.freebase.com/m/035br4",
        "format": "text/plain",
        "language": "ro",
        "multilingual": "[ro:Vlad Tepes,en:Vlad the Impaler]"
    },
    "motivatedBy": "oa:tagging",
    "serializedAt": "2012-11-10T09:08:07",
    "serializedBy": {
        "@id": "open_id_2",
        "@type": "prov:Software",
        "foaf:homepage": "http://annotorious.github.io/",
        "name": "Annotorious"
    },
    "styledBy": {
        "@type": "oa:Css",
        "source": "http://annotorious.github.io/latest/themes/dark/annotorious-dark.css",
        "styleClass": "annotorious-popup"
    },
    "target": {
        "type": "oa:Image",
        "contentType": "image/jpeg",
        "httpUri": "http://europeanastatic.eu/api/image?uri=http%3A%2F%2Fbilddatenbank.khm.at%2Fimages%2F500%2FGG_8285.jpg&size=FULL_DOC&type=IMAGE",
        "selector": {
            "@type": "[oa:SvgRectangle,euType:SVG_RECTANGLE_SELECTOR]",
            "dimensionMap": "[left:5,right:3]"
        },
        "source": {
            "@id": "/15502/GG_8285",
            "contentType": "text/html",
            "format": "dctypes:Text"
        },
        "targetType": "oa:SpecificResource"
    },
    "type": "OBJECT_TAG"
}
</textarea>
								<br>
								</form>
							</td>
							<td class="clsCellBorder" valign="top">
								create a semantic annotation (ObjectTag) and return the object as stored in the database for 'webanno' provider
							</td>
						</tr>						

						<tr>
							<td class="clsCellBorder cls0_0" valign="top">POST</td>
							<td class="clsCellBorder" valign="top">ObjectTag</td>
							<td class="clsCellBorder" valign="top">JSON_LD</td>
							<td class="clsCellBorder">
									[annotation-web]/annotationld/{collection}/{object_hash}.jsonld<br>
									Object as JSON-LD:
								<form action="annotations/15502/GG_8285.jsonld" method="post" target="_new">
								<input type="hidden" name="wskey" value="key">
								<input type="hidden" name="profile" value="webtest">
								<textarea rows="20" cols="60" name="annotation" >
{
    "@context": {
        "oa": "http://www.w3.org/ns/oa-context-20130208.json"
    },
    "@type": "oa:annotation",
    "sameAs": "http://historypin.com/annotation/1234",
    "equivalentTo": "http://historypin.com/annotation/1234",
    "annotatedAt": "2012-11-10T09:08:07",
    "annotatedBy": {
        "@id": "open_id_1",
        "@type": "foaf:Person",
        "name": "annonymous web user"
    },
    "body": {
        "@type": "[oa:Tag,cnt:ContentAsText,dctypes:Text,euType:SEMANTIC_TAG]",
        "chars": "Vlad Tepes",
        "foaf:page": "https://www.freebase.com/m/035br4",
        "format": "text/plain",
        "language": "ro",
        "multilingual": "[ro:Vlad Tepes,en:Vlad the Impaler]"
    },
    "motivatedBy": "oa:tagging",
    "serializedAt": "2012-11-10T09:08:07",
    "serializedBy": {
        "@id": "open_id_2",
        "@type": "prov:Software",
        "foaf:homepage": "http://annotorious.github.io/",
        "name": "Annotorious"
    },
    "styledBy": {
        "@type": "oa:Css",
        "source": "http://annotorious.github.io/latest/themes/dark/annotorious-dark.css",
        "styleClass": "annotorious-popup"
    },
    "target": {
        "type": "oa:Image",
        "contentType": "image/jpeg",
        "httpUri": "http://europeanastatic.eu/api/image?uri=http%3A%2F%2Fbilddatenbank.khm.at%2Fimages%2F500%2FGG_8285.jpg&size=FULL_DOC&type=IMAGE",
        "selector": {
            "@type": "[oa:SvgRectangle,euType:SVG_RECTANGLE_SELECTOR]",
            "dimensionMap": "[left:5,right:3]"
        },
        "source": {
            "@id": "/15502/GG_8285",
            "contentType": "text/html",
            "format": "dctypes:Text"
        },
        "targetType": "oa:Image"
    },
    "type": "OBJECT_TAG"
}
</textarea>
								<br>
								</form>
							</td>
							<td class="clsCellBorder" valign="top">
								create a semantic annotation (ObjectTag) and return the object as stored in the database for 'historypin' provider. Note that field 'sameAs' is employed for that task.
							</td>
						</tr>						

						<tr>
							<td class="clsCellBorder cls0_0" valign="top">POST</td>
							<td class="clsCellBorder" valign="top">ObjectTag</td>
							<td class="clsCellBorder" valign="top">JSON_LD</td>
							<td class="clsCellBorder">
									[annotation-web]/annotationld/{collection}/{object_hash}.jsonld<br>
									Object as JSON-LD:
								<form action="annotations/15502/GG_8285.jsonld" method="post" target="_new">
								<input type="hidden" name="wskey" value="key">
								<input type="hidden" name="profile" value="webtest">
								<textarea rows="20" cols="60" name="annotation" >
{
    "@context": {
        "oa": "http://www.w3.org/ns/oa-context-20130208.json"
    },
    "@type": "oa:annotation",
    "annotatedAt": "2012-11-10T09:08:07",
    "annotatedBy": {
        "@id": "open_id_1",
        "@type": "foaf:Person",
        "name": "annonymous web user"
    },
    "body": {
        "@type": "[oa:Tag,cnt:ContentAsText,dctypes:Text,euType:SEMANTIC_TAG]",
        "chars": "Vlad Tepes",
        "foaf:page": "https://www.freebase.com/m/035br4",
        "format": "text/plain",
        "language": "ro",
        "multilingual": "[ro:Vlad Tepes,en:Vlad the Impaler]",
        "concept": {
            "notation": "skos:notation",
            "prefLabel": {
                "@id": "skos:prefLabel",
                "@container": "@language"
            },
            "altLabel": {
                "@id": "skos:altLabel",
                "@container": "@language"
            },
            "hiddenLabel": {
                "@id": "skos:altLabel",
                "@container": "@language"
            },
            "narrower": "skos:narrower",
            "broader": "skos:broader",
            "related": "skos:related"
        }
    },
    "motivatedBy": "oa:tagging",
    "serializedAt": "2012-11-10T09:08:07",
    "serializedBy": {
        "@id": "open_id_2",
        "@type": "prov:Software",
        "foaf:homepage": "http://annotorious.github.io/",
        "name": "Annotorious"
    },
    "styledBy": {
        "@type": "oa:Css",
        "source": "http://annotorious.github.io/latest/themes/dark/annotorious-dark.css",
        "styleClass": "annotorious-popup"
    },
    "target": {
        "type": "oa:Image",
        "contentType": "image/jpeg",
        "httpUri": "http://europeanastatic.eu/api/image?uri=http%3A%2F%2Fbilddatenbank.khm.at%2Fimages%2F500%2FGG_8285.jpg&size=FULL_DOC&type=IMAGE",
        "selector": {
            "@type": "[oa:SvgRectangle,euType:SVG_RECTANGLE_SELECTOR]",
            "dimensionMap": "[left:5,right:3]"
        },
        "source": {
            "@id": "/15502/GG_8285",
            "contentType": "text/html",
            "format": "dctypes:Text"
        },
        "targetType": "oa:Image"
    },
    "type": "OBJECT_TAG"
}
</textarea>
								<br>
								</form>
							</td>
							<td class="clsCellBorder" valign="top">
								create a semantic annotation (ObjectTag) and return the object as stored in the database for 'webanno' provider using SKOS concept in Body.
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