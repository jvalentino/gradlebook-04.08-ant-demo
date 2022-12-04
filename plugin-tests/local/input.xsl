<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/">
		<html>
			<body>
				<xsl:apply-templates />
			</body>
		</html>
	</xsl:template>
	<xsl:template match="why">
		Why:
		<xsl:value-of select="." />
		<br />
	</xsl:template>
	<xsl:template match="what">
		What:
		<xsl:value-of select="." />
		<br />
	</xsl:template>
	<xsl:template match="when">
		When:
		<xsl:value-of select="." />
		<br />
	</xsl:template>
	<xsl:template match="how">
		How:
		<xsl:value-of select="." />
		<br />
	</xsl:template>

</xsl:stylesheet>