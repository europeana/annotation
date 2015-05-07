<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Europeana Creative - Annotation Templates</title>
<link href="../css/css1.css" rel="stylesheet" type="text/css" />
<link href="../css/main.css" rel="stylesheet" type="text/css" />
<link href="../css/main_002.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" href="templates.css" type="text/css">
</head>
<body>

	<div id="wrapper" class="max-width">
		<header id="banner"><!-- role="banner" ? -->
			<img class="display-none" src="../img/creative-logo.png" alt=""> <img
				class="bg" src="../img/creative-banner.jpg" alt="Europeana Projects">
			<h2 id="top">Europeana Creative - Annotation Templates </h2>
		</header>
		<div id="mainblock">
			<div id="content">
				<!-- Box for the Service Description -->
					<div class="">
						<!--  <h3>Annotation</h3>
						
						<div class="left">
							
						</div>
						-->
						<img src="http://www.tehamaschools.org/files/Fether%20Grey.png" height="64" />
						Annotation component is a service that implements the management and indexing of Web Annotations
					</div>

					<ul id="toc">
						<li><a href="#json-api">JSON API</a></li>
						<li><a href="#json-ld-api">JSON-LD API</a></li>
						<li><a href="#europeana-ld-api">EUROPEANA-LD API</a></li>
					</ul>

					<h3 id="json-api">JSON  API  <a href="#top">top</a></h3> 
					<jsp:include page="json-api.jsp" />

					<h3 id="json-ld-api">JSON-LD  API  <a href="#top">top</a></h3>
					<jsp:include page="json-ld-api.jsp" />

					<h3 id="europeana-ld-api">EUROPEANA-LD  API  <a href="#top">top</a></h3>
					<jsp:include page="europeana-ld-api.jsp" />											

			</div>
			<!-- end of content -->
		</div>
		<!-- end of mainblock -->

		<hr />
		<!--Europeana Group Block-->
		<footer id="footer">
			<div class="middle f-left">
				<a href="http://www.pro.europeana.eu/web/guest/projects"
					class="f-left">Europeana Network projects</a>
			</div>
			<span class="f-right">co-funded by the European Union<img
				alt="european union flag" src="img/eu-flag.gif"></span>
		</footer>


	</div>
	<!-- end of wrapper -->
</body>
</html>

