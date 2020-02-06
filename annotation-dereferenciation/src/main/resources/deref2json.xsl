<?xml version="1.0" encoding="UTF-8"?>

<!--
  Document   : wkd2edm.xsl
  Author     : hmanguinhas
  Created on : October 13, 2019
  Updated on : December 17, 2019
-->

<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
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
    xmlns:fn="http://www.w3.org/2005/xpath-functions"
    xmlns:doc="http://www.example.org/functions"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"

    xmlns:wdt="http://www.wikidata.org/prop/direct/"
    xmlns:schema="http://schema.org/"
    >

    <xsl:output method="text" omit-xml-declaration="yes" indent="no" encoding="UTF-8"/>

    <xsl:param name="uri"/>
    <!-- use portal languages by default (27) -->
    <xsl:param name="langs">en,pl,de,nl,fr,it,da,sv,el,fi,hu,cs,sl,et,pt,es,lt,lv,bg,ro,sk,hr,ga,mt,no,ca,ru</xsl:param>

    <xsl:template match="/">
        <xsl:apply-templates select="metis:results/metis:enrichmentBaseWrapperList/*[@rdf:about=$uri]"/>
    </xsl:template>

    <xsl:template match="skos:Concept|edm:Agent|edm:Place|edm:TimeSpan">
        <xsl:text>{ "@context": "http://www.europeana.eu/schemas/context/entity.jsonld"</xsl:text>
        <xsl:text>, "id": "</xsl:text><xsl:value-of select="@rdf:about"/><xsl:text>"</xsl:text>
        <xsl:text>, "type": "</xsl:text><xsl:value-of select="local-name()"/><xsl:text>"</xsl:text>

        <xsl:call-template name="lang-map">
            <xsl:with-param name="prop" select="skos:prefLabel[contains($langs,@xml:lang)]"/>
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
				<xsl:text>"</xsl:text><xsl:value-of select="@xml:lang"/><xsl:text>": </xsl:text>
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
            <xsl:text>": </xsl:text><xsl:value-of select="$prop[position()=1]/text()"/><xsl:text>"</xsl:text>
        </xsl:if>
    </xsl:template>


</xsl:stylesheet>