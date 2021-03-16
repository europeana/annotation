<?xml version="1.0" encoding="UTF-8"?>

<!--
  Document   : deref2json.xsl
  Author     : hmanguinhas
  Created on : January 15, 2020
  Updated on : January 15, 2020
-->

<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:lib="http://example.org/lib"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:metis="http://www.europeana.eu/schemas/metis"
    xmlns:edm="http://www.europeana.eu/schemas/edm/"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:foaf="http://xmlns.com/foaf/0.1/"
    xmlns:skos="http://www.w3.org/2004/02/skos/core#"
    xmlns:owl="http://www.w3.org/2002/07/owl#"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
    xmlns:rdaGr2="http://RDVocab.info/ElementsGr2/"
    xmlns:wgs84_pos="http://www.w3.org/2003/01/geo/wgs84_pos#"
    >

    <xsl:output method="text" omit-xml-declaration="yes" indent="no" encoding="UTF-8"/>

    <xsl:param name="uri"/>
    <!-- use portal languages by default (27) -->
    <xsl:param name="langs">en,pl,de,nl,fr,it,da,sv,el,fi,hu,cs,sl,et,pt,es,lt,lv,bg,ro,sk,hr,ga,mt,no,ca,ru</xsl:param>

    <xsl:template match="/">
        <xsl:apply-templates select="metis:results/metis:result/*[@rdf:about=$uri]"/>
    </xsl:template>

    <xsl:template match="skos:Concept|edm:Agent|edm:Place|edm:TimeSpan">
        <xsl:text>{ "@context": "http://www.europeana.eu/schemas/context/entity.jsonld"</xsl:text>
        <xsl:text>, "id": "</xsl:text><xsl:value-of select="@rdf:about"/><xsl:text>"</xsl:text>
        <xsl:text>, "type": "</xsl:text><xsl:value-of select="local-name()"/><xsl:text>"</xsl:text>

        <xsl:call-template name="lang-map">
            <xsl:with-param name="prop" select="skos:prefLabel[lib:isAcceptableLang(@xml:lang)]"/>
            <xsl:with-param name="name">prefLabel</xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="array">
            <xsl:with-param name="prop" select="edm:begin"/>
            <xsl:with-param name="name">begin</xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="array">
            <xsl:with-param name="prop" select="rdaGr2:dateOfBirth"/>
            <xsl:with-param name="name">dateOfBirth</xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="array">
            <xsl:with-param name="prop" select="rdaGr2:dateOfEstablishment"/>
            <xsl:with-param name="name">dateOfEstablishment</xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="array">
            <xsl:with-param name="prop" select="edm:end"/>
            <xsl:with-param name="name">end</xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="array">
            <xsl:with-param name="prop" select="rdaGr2:dateOfDeath"/>
            <xsl:with-param name="name">dateOfDeath</xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="array">
            <xsl:with-param name="prop" select="rdaGr2:dateOfTermination"/>
            <xsl:with-param name="name">dateOfTermination</xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="single">
            <xsl:with-param name="prop" select="wgs84_pos:lat"/>
            <xsl:with-param name="name">lat</xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="single">
            <xsl:with-param name="prop" select="wgs84_pos:long"/>
            <xsl:with-param name="name">long</xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="single">
            <xsl:with-param name="prop" select="wgs84_pos:alt"/>
            <xsl:with-param name="name">alt</xsl:with-param>
        </xsl:call-template>

        <xsl:text>}</xsl:text>
    </xsl:template>

    <xsl:template name="lang-map">
        <xsl:param name="prop"/>
        <xsl:param name="name"/>

        <xsl:if test="$prop">
            <xsl:text>, "</xsl:text><xsl:value-of select="$name"/><xsl:text>": { </xsl:text>
            <xsl:for-each select="$prop">
				<xsl:if test="position()>1"><xsl:text>, </xsl:text></xsl:if>
				<xsl:text>"</xsl:text><xsl:value-of select="lib:getLang(@xml:lang)"/><xsl:text>": </xsl:text>
				<xsl:text>"</xsl:text><xsl:value-of select="text()"/><xsl:text>"</xsl:text>
            </xsl:for-each>
            <xsl:text> }</xsl:text>
        </xsl:if>
    </xsl:template>

    <xsl:template name="array">
        <xsl:param name="prop"/>
        <xsl:param name="name"/>

        <xsl:if test="$prop">
            <xsl:text>, "</xsl:text><xsl:value-of select="$name"/><xsl:text>": [ </xsl:text>
            <xsl:for-each select="$prop">
                <xsl:if test="position()>1"><xsl:text>, </xsl:text></xsl:if>
                <xsl:text>"</xsl:text><xsl:value-of select="text()"/><xsl:text>"</xsl:text>
            </xsl:for-each>
            <xsl:text> ]</xsl:text>
        </xsl:if>
    </xsl:template>

    <xsl:template name="single">
        <xsl:param name="prop"/>
        <xsl:param name="name"/>

        <xsl:if test="$prop">
            <xsl:text>, "</xsl:text><xsl:value-of select="$name"/>
            <xsl:text>": "</xsl:text><xsl:value-of select="$prop[position()=1]/text()"/><xsl:text>"</xsl:text>
        </xsl:if>
    </xsl:template>


    <!--                          LANGUAGE UTILS                             -->

    <xsl:function name="lib:getLang" as="xs:string">
        <xsl:param name="string"/>
        <xsl:variable name="str" select="lower-case($string)"/>

        <xsl:choose>
            <xsl:when test="contains($langs,$str)">
                <xsl:value-of select="$string"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$langMap/language[altLang/@code=$str]/@code"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>

    <xsl:function name="lib:isAcceptableLang" as="xs:boolean">
        <xsl:param name="string"/>
        <xsl:variable name="str" select="lower-case($string)"/>
        
        <xsl:value-of select="contains($langs,$str) or 
            exists($langMap/language[contains($langs,@code)]/altLang[@code=$str])"/>
     </xsl:function>

    <xsl:variable name="langMap">
        <language code="en">
            <altLang code="eng"/>
            <altLang code="en-gb"/>
        </language>
        <language code="nl">
            <altLang code="dut"/>
            <altLang code="nld"/>
            <altLang code="nl-nl"/>
        </language>
        <language code="fr">
            <altLang code="fre"/>
            <altLang code="fra"/>
            <altLang code="fr-fr"/>
        </language>
        <language code="de">
            <altLang code="ger"/>
            <altLang code="deu"/>
            <altLang code="de-de"/>
        </language>
        <language code="es">
            <altLang code="spa"/>
            <altLang code="es-es"/>
        </language>
        <language code="sv">
            <altLang code="swe"/>
            <altLang code="sv-se"/>
        </language>
        <language code="it">
            <altLang code="ita"/>
            <altLang code="it-it"/>
        </language>
        <language code="fi">
            <altLang code="fin"/>
            <altLang code="fi-fi"/>
        </language>
        <language code="da">
            <altLang code="dan"/>
            <altLang code="da-dk"/>
        </language>
        <language code="el">
            <altLang code="ell"/>
            <altLang code="gre"/>
            <altLang code="el-gr"/>
        </language>
        <language code="cs">
            <altLang code="ces"/>
            <altLang code="cze"/>
            <altLang code="cs-cz"/>
        </language>
        <language code="sk">
            <altLang code="slo"/>
            <altLang code="sk-sk"/>
        </language>
        <language code="sl">
            <altLang code="slv"/>
            <altLang code="sl-si"/>
        </language>
        <language code="pt">
            <altLang code="por"/>
            <altLang code="pt-pt"/>
        </language>
        <language code="hu">
            <altLang code="hun"/>
            <altLang code="hu-hu"/>
        </language>
        <language code="lt">
            <altLang code="lit"/>
            <altLang code="lt-lt"/>
        </language>
        <language code="pl">
            <altLang code="pol"/>
            <altLang code="pl-pl"/>
        </language>
        <language code="ro">
            <altLang code="rum"/>
            <altLang code="ron"/>
            <altLang code="ro-ro"/>
        </language>
        <language code="bg">
            <altLang code="bul"/>
            <altLang code="bg-bg"/>
        </language>
        <language code="hr">
            <altLang code="hrv"/>
            <altLang code="hr-hr"/>
        </language>
        <language code="lv">
            <altLang code="lav"/>
            <altLang code="lv-lv"/>
        </language>
        <language code="ga">
            <altLang code="gle"/>
            <altLang code="ga-ie"/>
        </language>
        <language code="mt">
            <altLang code="mlt"/>
            <altLang code="mt-mt"/>
        </language>
        <language code="et">
            <altLang code="est"/>
            <altLang code="et-ee"/>
        </language>
    </xsl:variable>

</xsl:stylesheet>