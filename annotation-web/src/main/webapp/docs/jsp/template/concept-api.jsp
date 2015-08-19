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
							<td class="clsCellBorder" valign="top">Concept</td>
							<td class="clsCellBorder" valign="top">JSON</td>
							<td class="clsCellBorder">
									[annotation-web]/concepts/{uri}.json<br>
									ObjectTag as JSON:
								<form action="annotations/15502/GG_8285.json" method="post" target="_new">
								<input type="hidden" name="wskey" value="key">
								<input type="hidden" name="profile" value="webtest">
								<textarea rows="20" cols="60" name="concept" >
{
"uri": "concept.eu/new",
"type": "BASE_CONCEPT",
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
</textarea>
								<br>
								</form>
							</td>
							<td class="clsCellBorder" valign="top">
								create a concept and return the object as stored in the database. 
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