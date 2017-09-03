<?xml version="1.0" encoding="UTF-8"?>  
<xsl:stylesheet version="2.0"   
  xmlns:ui="http://xmlns.jcp.org/jsf/facelets" xmlns:h="http://xmlns.jcp.org/jsf/html"  
  xmlns:f="http://xmlns.jcp.org/jsf/core" xmlns:rich="http://richfaces.org/rich"  
  xmlns:a4j="http://richfaces.org/a4j" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">  
  
  
  <xsl:output  
    method="xml"  
    encoding="utf-8"  
    doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"  
    doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"  
    indent="no"  
    omit-xml-declaration="yes"  
    />  
  
  
  <!-- Copy everything -->  
  <xsl:template match="@*|node()">  
    <xsl:copy>  
      <xsl:apply-templates select="@*|node()" />  
    </xsl:copy>  
  </xsl:template>  
    
  <xsl:template match="head" name="head" xpath-default-namespace="http://www.w3.org/1999/xhtml">  
    <h:head><!-- Added -->  
      <xsl:apply-templates select="@*|node()" />  
    </h:head>  
  </xsl:template>  
  <xsl:template match="body" name="body" xpath-default-namespace="http://www.w3.org/1999/xhtml">  
    <h:body><!-- Added -->  
      <xsl:apply-templates select="@*|node()" />  
    </h:body>  
  </xsl:template>  
    
  <!-- script tag -->  
  <xsl:template match="//script" priority="20" xpath-default-namespace="http://www.w3.org/1999/xhtml" >  
    <xsl:element name="h:outputScript" ><!-- Added -->  
      <xsl:value-of select="." disable-output-escaping="yes"/>  
    </xsl:element>  
  </xsl:template>  
    
  <!-- style tag -->  
  <xsl:template match="//style" priority="20" xpath-default-namespace="http://www.w3.org/1999/xhtml" >  
    <xsl:element name="h:outputStylesheet" ><!-- Added -->  
      <xsl:value-of select="." disable-output-escaping="yes"/>  
    </xsl:element>  
  </xsl:template>  
  
    
  <!-- Ajax Attributes -->  
  <xsl:template match="@reRender" name="reRender" >  
    <xsl:attribute name="render"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
  <xsl:template match="@process" name="process" >  
    <xsl:attribute name="execute"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
  <xsl:template match="@limitToList" name="limitToList"  >  
    <xsl:attribute name="limitRender"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
  <xsl:template match="@ignoreDupResponse" /><!-- Deleted -->  
  <xsl:template match="@requestDelay" /><!-- Deleted -->  
  <xsl:template match="@timeout" /><!-- Deleted -->  
  <xsl:template match="@ajaxSingle" ><!-- Deleted -->  
    <xsl:if test="..[not(@process)]" >  
      <xsl:attribute name="execute"><xsl:text>@this</xsl:text></xsl:attribute><!-- Add an @execute='@this' if not present -->  
    </xsl:if>  
  </xsl:template>  
  
  
  <xsl:template match="h:graphicImage" name="h:graphicImage">  
    <h:graphicImage>  
      <xsl:apply-templates select="@*[name()!='value']" /><!-- All attributes whose name is not "value" -->  
      <xsl:attribute name="library">default</xsl:attribute>  
      <xsl:attribute name="name"><!-- Replace value by 'name' and give it the value of value -->  
        <xsl:value-of select="replace(./@value, '/(images/.*)\.([pngif]{3})', '$1.$2')" />  
      </xsl:attribute>  
      <xsl:attribute name="alt"><!-- fulfill 'alt' attribute with the file's name of the image -->  
       <xsl:if test=".[@value]" ><!--  -->  
        <xsl:analyze-string select="./@value" regex="[-\.a-zA-Z_]+$">  
          <xsl:matching-substring>  
            <xsl:value-of select="." />  
          </xsl:matching-substring>  
        </xsl:analyze-string>  
      </xsl:if>  
      </xsl:attribute>  
      <xsl:apply-templates select="node()" />  
    </h:graphicImage>  
  </xsl:template>  
    
  <xsl:template match="*/@oncomplete" name="oncomplete">  
    <xsl:attribute name="oncomplete">  
      <xsl:value-of select="replace(., '(.*)([^.])data(.*)', '$1$2event.data$3')" /><!-- Replace 'data' by 'event.data' -->  
    </xsl:attribute>  
  </xsl:template>  
    
  <xsl:template match="a4j:loadScript" name="a4j:loadScript">  
    <h:outputScript library="default"><!-- Renamed -->  
      <xsl:apply-templates select="@*[name()!='src']" />  
      <xsl:attribute name="library">default</xsl:attribute>  
      <xsl:attribute name="name"><xsl:value-of select="replace(./@src, '/js/(.*\.js)', 'js/$1')" /></xsl:attribute><!-- Replace 'src' by 'name' and give it the value of 'src' -->  
    </h:outputScript>  
  </xsl:template>  
  
  
  <xsl:template match="a4j:loadStyle" name="a4j:loadStyle">  
    <h:outputStylesheet ><!-- Renamed -->  
      <xsl:apply-templates select="@*[name()!='src']" />  
      <xsl:attribute name="library">default</xsl:attribute>  
      <xsl:attribute name="name"><xsl:value-of select="replace(./@src, '/css/(.*\.css)', 'css/$1')"></xsl:value-of></xsl:attribute><!-- Replace 'src' by 'name' and give it the value of 'src' -->  
    </h:outputStylesheet>  
  </xsl:template>  
  
  
  <xsl:template match="a4j:actionparam" name="a4j:actionparam">  
    <a4j:param><!-- Renamed -->  
      <xsl:apply-templates select="@*|node()" />  
    </a4j:param>  
  </xsl:template>  
  
  
  <xsl:template match="a4j:support" name="a4j:support">  
    <a4j:ajax><!-- Renamed -->  
      <xsl:apply-templates select="@*|node()" />  
    </a4j:ajax>  
  </xsl:template>  
  <xsl:template match="a4j:support/@action">  
    <xsl:attribute name="listener"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
  <xsl:template match="a4j:support/@event">  
    <xsl:attribute name="event">  
      <xsl:value-of select="replace( . , 'on(.*)', '$1')" /><!-- Transform the @event value by removing the 'on' part of the event eg. : [on]click -->  
    </xsl:attribute>  
  </xsl:template>  
    
  <xsl:template match="a4j:commandButton/@image" name="a4j:commandButton-image">  
    <xsl:attribute name="image">  
      <xsl:value-of select="replace( . , '(/image.*)', '/resources/default$1')" /><!-- transform the @image value by adding the resource folder -->  
    </xsl:attribute>   
  </xsl:template>  
    
  <xsl:template match="a4j:log/@popup">  
    <xsl:attribute name="mode"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
  
  
  <xsl:template match="a4j:include" name="a4j:include">  
    <ui:include><!-- Renamed -->  
      <xsl:apply-templates select="@*|node()" />  
    </ui:include>  
  </xsl:template>  
  
  
  <xsl:template match="a4j:page">  
    <h:panelGroup layout="block"><!-- Transform into block as it doesn't exist anymore -->  
        <xsl:apply-templates select="@*|node()" />  
    </h:panelGroup>  
  </xsl:template>  
  
  
  <xsl:template match="a4j:region/@renderRegionOnly" /><!-- Deleted -->  
  <xsl:template match="a4j:region/@selfRendered" /><!-- Deleted -->  
  <xsl:template match="a4j:region/@ajaxListener" /><!-- Deleted -->  
  <xsl:template match="a4j:region/@immediate" /><!-- Deleted -->  
  
  
  <!-- Rich validation components -->  
  
  
  <xsl:template match="rich:ajaxValidator" name="rich:ajaxValidator">  
    <rich:validator><!-- Renamed -->  
      <xsl:apply-templates select="@*|node()" />  
    </rich:validator>  
  </xsl:template>  
  
  
  <xsl:template match="rich:jQuery/@timing">  
    <xsl:attribute name="timing">domready</xsl:attribute><!-- Rename the value (specific to our projet)-->  
  </xsl:template>  
  
  
  <!-- Rich input components -->  
  
  
  <!-- rich:comboBox > rich:autocomplete -->  
  <xsl:template match="rich:comboBox" name="rich:comboBox">  
    <rich:autocomplete><!-- Renamed -->  
      <xsl:apply-templates select="@*|node()" />  
    </rich:autocomplete>  
  </xsl:template>  
    
  <!-- rich:suggestionBox > rich:autocomplete -->  
  <xsl:template match="rich:suggestionBox" name="rich:suggestionBox">  
    <rich:autocomplete><!-- Renamed -->  
      <xsl:apply-templates select="@*|node()" />  
    </rich:autocomplete>  
  </xsl:template>  
  
  
  <xsl:template match="rich:fileUpload/@error">  
    <xsl:attribute name="serverErrorLabel"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
  
  
  <xsl:template match="rich:inplaceInput/@onviewactivated" /><!-- Deleted -->  
  <xsl:template match="rich:inplaceSelect/@onviewactivated" /><!-- Deleted -->  
  
  
  
  
  <!-- Rich Panels/output components -->  
  
  
  <!-- Renommés -->  
  <xsl:template match="rich:modalPanel" name="rich:modalPanel">  
    <rich:popupPanel modal="true"><!-- Renamed -->  
      <xsl:apply-templates select="@*|node()" />  
    </rich:popupPanel>  
  </xsl:template>  
    
  <xsl:template match="rich:modalPanel/@showWhenRendered">  
    <xsl:attribute name="show"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
  
  
  <xsl:template match="rich:panelBar" name="rich:panelBar">  
    <rich:accordion><!-- Renamed -->  
      <xsl:apply-templates select="@*|node()" />  
    </rich:accordion>  
  </xsl:template>  
  
  
  <xsl:template match="rich:panelBarItem" name="rich:panelBarItem">  
    <rich:accordionItem><!-- Renamed -->  
      <xsl:apply-templates select="@*|node()" />  
    </rich:accordionItem>  
  </xsl:template>  
  
  
  <xsl:template match="rich:panelMenu/@ValueChangeListener">  
    <xsl:attribute name="itemchangeListener"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
  
  
  <xsl:template match="rich:simpleTogglePanel" name="rich:simpleTogglePanel">  
    <rich:collapsiblePanel><!-- Renamed -->  
      <xsl:apply-templates select="@*|node()" />  
    </rich:collapsiblePanel>  
  </xsl:template>  
  
  
  <xsl:template match="rich:tabPanel/@ValueChangeEvent">  
    <xsl:attribute name="itemChangeListener"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
  
  
  <xsl:template match="rich:tabPanel/@valueChangeListener">  
    <xsl:attribute name="itemChangeListener"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
  
  
  <xsl:template match="rich:tabPanel/@label">  
    <xsl:attribute name="header"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
  
  
  <xsl:template match="rich:tabPanel/@selectedTab">  
    <xsl:attribute name="activeItem"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
  
  
  <xsl:template match="rich:tab/@ValueChangeEvent">  
    <xsl:attribute name="itemChangeListener"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
  
  
  <xsl:template match="rich:tab/@valueChangeListener">  
    <xsl:attribute name="itemChangeListener"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
  
  
  <xsl:template match="rich:tab/@label">  
    <xsl:attribute name="header"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
    
  <xsl:template match="rich:tab/@ontabenter">  
    <xsl:attribute name="onheaderclick"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
  
  
  <xsl:template match="rich:togglePanel/@ValueChangeEvent">  
    <xsl:attribute name="itemChangeListener"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
  
  
  <xsl:template match="rich:togglePanel/@valueChangeListener">  
    <xsl:attribute name="itemChangeListener"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
  
  
  <xsl:template match="rich:facets" name="rich:facets">  
    <rich:togglePanelItem><!-- Renamed à vérifier -->  
      <xsl:apply-templates select="@*|node()" />  
    </rich:togglePanelItem>  
  </xsl:template>  
  
  
  <xsl:template match="rich:facets/@ValueChangeEvent">  
    <xsl:attribute name="itemChangeListener"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
  
  
  <xsl:template match="rich:facets/@valueChangeListener">  
    <xsl:attribute name="itemChangeListener"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
  
  
  <xsl:template match="rich:toggleControl/@ValueChangeEvent">  
    <xsl:attribute name="itemChangeListener"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
  
  
  <xsl:template match="rich:toggleControl/@valueChangeListener">  
    <xsl:attribute name="itemChangeListener"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
  
  
  <!--  rich:toolBar > rich:toolbar -->  
  <xsl:template match="rich:toolBar" name="rich:toolBar">  
    <rich:toolbar><!-- Renamed -->  
      <xsl:apply-templates select="@*|node()" />  
    </rich:toolbar>  
  </xsl:template>  
  
  
  <!--  rich:toolBarGroup > rich:toolbarGroup -->  
  <xsl:template match="rich:toolBarGroup" name="rich:toolBarGroup">  
    <rich:toolbarGroup><!-- Renamed -->  
      <xsl:apply-templates select="@*|node()" />  
    </rich:toolbarGroup>  
  </xsl:template>  
  
  
  <!--  ToolTip > tooltip -->  
  <xsl:template match="rich:toolTip" name="rich:toolTip">  
    <rich:tooltip><!-- Renamed -->  
      <xsl:apply-templates select="@*[name()!='for']" />  
      <xsl:attribute name="target"><!-- Renamed target into for -->  
        <xsl:value-of select="./@for" />  
      </xsl:attribute>  
      <xsl:apply-templates select="node()" />  
    </rich:tooltip>  
  </xsl:template>  
    
  <!--  Tooltip bottom-right becomes bottomRight -->  
  <xsl:template match="rich:toolTip/@direction" >  
    <xsl:attribute name="direction">  
      <xsl:choose>  
        <xsl:when test="contains(., 'right')">  
          <xsl:value-of select="replace( . , '(.*)-right', '$1Right')" /><!-- Transform the direction value into camelCase style -->  
        </xsl:when>  
        <xsl:otherwise>  
          <xsl:value-of select="replace( . , '(.*)-left', '$1Left')" />  
        </xsl:otherwise>  
      </xsl:choose>  
    </xsl:attribute>  
  </xsl:template>  
    
  <!-- rich:messages deleted the level classes -->  
  <xsl:template match="rich:messages/@errorClass" /><!-- Deleted -->  
  <xsl:template match="rich:messages/@infoClass" /><!-- Deleted -->  
  <xsl:template match="rich:messages/@styleClass" name="message-styleClass">  
    <xsl:attribute name="styleClass"><xsl:value-of select="." /><xsl:text> level_</xsl:text><xsl:value-of select="../@level" /></xsl:attribute><!-- Put the deleted @level value into styleClass for not loosing it -->  
  </xsl:template>  
  <xsl:template match="rich:messages[not(@styleClass)]" >  
    <rich:messages>  
      <xsl:attribute name="styleClass"><xsl:text>level_</xsl:text><xsl:copy-of select="./@level" /></xsl:attribute><!-- Put the deleted @level value into styleClass for not loosing it -->  
      <xsl:apply-templates select="@*|node()" />  
    </rich:messages>  
  </xsl:template>  
  <xsl:template match="rich:messages/@level" /><!-- Deleted -->  
    
  <xsl:template match="rich:separator" name="rich:separator">  
    <h:panelGroup><!-- Transformed into div to not loose it -->  
      <xsl:attribute name="layout"><xsl:text>block</xsl:text></xsl:attribute>  
      <xsl:attribute name="styleClass"><xsl:value-of select="@lineType" /><xsl:text> separator</xsl:text></xsl:attribute>  
      <xsl:attribute name="style"><xsl:value-of select="@style" /><xsl:text>;height:</xsl:text><xsl:value-of select="@height" /></xsl:attribute>  
    </h:panelGroup>  
  </xsl:template>  
    
  <xsl:template match="rich:spacer" name="rich:spacer">  
    <h:panelGroup><!-- Transformed into div to not loose it -->  
      <xsl:attribute name="layout"><xsl:text>block</xsl:text></xsl:attribute>  
      <xsl:attribute name="styleClass"><xsl:text>spacer</xsl:text></xsl:attribute>  
    </h:panelGroup>  
  </xsl:template>  
     
  
  
  
  
  <!-- Rich Ordering Components -->  
  
  
  <xsl:template match="rich:listShuttle" name="rich:listShuttle">  
    <rich:pickList><!-- Renamed -->  
      <xsl:apply-templates select="@*|node()" />  
    </rich:pickList>  
  </xsl:template>  
    
  <!-- Rich Menu -->  
    
  <xsl:template match="rich:contextMenu/@event">  
    <xsl:attribute name="showEvent"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
  
  
  <xsl:template match="rich:contextMenu/@attachTo">  
    <xsl:attribute name="target"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
  
  
  <xsl:template match="rich:contextMenu/@submitMode">  
    <xsl:attribute name="mode"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
  
  
  <xsl:template match="rich:menuGroup/@value" >  
    <xsl:attribute name="label" ><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
    
  <xsl:template match="rich:menuItem/@value">  
    <xsl:attribute name="label"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
    
  <xsl:template match="rich:menuItem/@submitMode">  
    <xsl:attribute name="mode">  
      <xsl:choose>  
          <xsl:when test=". = 'none'">client</xsl:when><!-- Rename the 'none' @submitMode value into 'client' -->  
          <xsl:otherwise>  
              <xsl:value-of select="."/>  
          </xsl:otherwise>  
      </xsl:choose>  
    </xsl:attribute>  
  </xsl:template>  
    
  <xsl:template match="rich:menuItem/@onselect">  
    <xsl:attribute name="onclick"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
    
  <xsl:template match="rich:menuItem/@icon">  
    <xsl:attribute name="icon">  
      <xsl:if test="not(contains(., 'resources'))" >  
        <xsl:value-of select="replace( . , '(/image.*)', '/resources/default$1')" /> <!-- Insert 'resources' folder into @icon value -->  
      </xsl:if>  
    </xsl:attribute>  
  </xsl:template>  
    
  <xsl:template match="rich:menuItem/@iconDisabled">  
    <xsl:attribute name="iconDisabled">  
      <xsl:if test="not(contains(., 'resources'))" >  
        <xsl:value-of select="replace( . , '(/image.*)', '/resources/default$1')" /> <!-- Insert 'resources' folder into @icon value -->  
      </xsl:if>  
    </xsl:attribute>  
  </xsl:template>  
    
  <!-- Put the deleted @labelClass value into @styleClass (specific to our project) -->  
  <xsl:template match="rich:menuItem/@labelClass" name="rich:m1">  
    <xsl:attribute name="styleClass" >  
      <xsl:choose>  
        <xsl:when test=" . = 'iconLabel'" >  
          <xsl:text>iconAndLabel</xsl:text>  
        </xsl:when>  
        <xsl:when test=" . = 'labelAlone'" >  
          <xsl:text>justLabel</xsl:text>  
        </xsl:when>  
        <xsl:when test=" . = 'noLabel'" >  
          <xsl:text>justIcon</xsl:text>  
        </xsl:when>  
        <xsl:otherwise>  
          <xsl:value-of select="." />  
        </xsl:otherwise>  
      </xsl:choose>  
      <xsl:text> </xsl:text>  
      <xsl:value-of select="../@styleClass"/>  
    </xsl:attribute>  
  </xsl:template>  
  <xsl:template match="rich:menuItem/@iconStyle" /><!-- Deleted -->  
  
  
  <!-- Rich Iteration Components -->  
  
  
  <xsl:template match="rich:dataOrderingList" name="rich:dataOrderingList">  
    <rich:list><!-- Renamed -->  
      <xsl:apply-templates select="@*|node()" />  
    </rich:list>  
  </xsl:template>  
  
  
  <xsl:template match="rich:dataDefinitionList" name="rich:dataDefinitionList">  
    <rich:list><!-- Renamed -->  
      <xsl:apply-templates select="@*|node()" />  
    </rich:list>  
  </xsl:template>  
  
  
  <xsl:template match="rich:dataList" name="rich:dataList">  
    <rich:list><!-- Renamed -->  
      <xsl:apply-templates select="@*|node()" />  
    </rich:list>  
  </xsl:template>  
  
  
  <xsl:template match="rich:datascroller" name="rich:datascroller">  
    <rich:dataScroller><!-- Renamed -->  
      <xsl:apply-templates select="@*|node()" />  
    </rich:dataScroller>  
  </xsl:template>  
  
  
  <xsl:template match="rich:datascroller/@onclick" >  
    <xsl:attribute name="onbegin"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
  <xsl:template match="rich:scrollableDataTable" name="rich:scrollableDataTable">  
    <rich:extendedDataTable><!-- Renamed -->  
      <xsl:apply-templates select="@*|node()" />  
    </rich:extendedDataTable>  
  </xsl:template>  
  <xsl:template match="rich:scrollableDataTable/@rows" name="rich:scrollableDataTableRows">  
    <xsl:attribute name="clientRows"><xsl:value-of select="." /> </xsl:attribute><!-- Renamed -->  
  </xsl:template>  
  <xsl:template match="rich:scrollableDataTable/@onRowDblClick" name="rich:scrollableDataTableRows1">  
    <xsl:attribute name="onrowdblclick"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
  <xsl:template match="rich:scrollableDataTable/@onRowClick" name="rich:scrollableDataTableRows2">  
    <xsl:attribute name="onrowclick"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
  <!-- match tables with @width and @height attributes but without @style attribute -->  
  <xsl:template match="rich:scrollableDataTable[@width and @height and not(@style)]/@width" name="rich:scrollableDataTableRows3">  
    <xsl:attribute name="style" >  
      <xsl:value-of select="concat( 'width:' , . , ';' , 'height:' , ../@height  , ';')" /> <!-- select the style attribute and concatenate its value with @width -->  
      <!-- I assume @height is specified as soon as @width is there (specific to our project)-->  
    </xsl:attribute>  
  </xsl:template>  
  <!-- match tables with @width and @height attributes but with @style attribute -->  
  <xsl:template match="rich:scrollableDataTable[@width and @height and @style]/@style" name="rich:scrollableDataTableRows4">  
    <xsl:attribute name="style" >  
      <xsl:value-of select="concat( 'width:' , ../@width , ';' , 'height:' , ../@height , ';', . )" /> <!-- select the style attribute and concatenate its value with @width -->  
    </xsl:attribute>  
  </xsl:template>  
  <xsl:template match="rich:scrollableDataTable[@width and @height and @style]/@width" name="rich:scrollableDataTableRows5" ></xsl:template> <!-- delete @width -->  
  <xsl:template match="rich:scrollableDataTable/@height" name="rich:scrollableDataTableRows6" ></xsl:template> <!-- delete @height -->  
    
    
  <!-- Rich Column(s) -->  
  
  
  <!-- In RF 3.3.3, putting sortable='true' was enough to make a column sortable, in RF 4.5 @sortBy must be used -->  
  <xsl:template match="rich:column" name="rich:column">  
    <rich:column>  
      <xsl:apply-templates select="@*[name()!='sortable' and name()!='width']" /> <!-- copy all attributes except 'sortable' -->  
      <xsl:if test=".[@sortable] and ./@sortable = 'true'" >  
        <xsl:attribute name="sortBy">  
          <xsl:value-of select="node()[@value]/@value" /> <!-- copy the value of @value attribute of the child ex h:outputText value="#{row.col2} -->  
        </xsl:attribute>  
      </xsl:if>  
      <xsl:if test=".[@width]" >  
        <xsl:attribute name="width">  
          <xsl:value-of select="concat(./@width , 'px')" /> <!-- concat 'px' to the @width value -->  
        </xsl:attribute>  
      </xsl:if>   
      <xsl:apply-templates select="node()" />  
    </rich:column>  
  </xsl:template>  
  <xsl:template match="rich:column/@sortable" name="rich:column2" /><!-- Deleted -->  
  <!-- Deleted but cannot be transformed easily  
    <xsl:template match="rich:columns" />  
    <xsl:template match="rich:dataFilterSlider" />  
    <xsl:template match="rich:subTable" /> -->  
  
  
  
  
  <!-- Rich Tree Components -->  
  
  
  <xsl:template match="rich:treeNodesAdaptor" name="rich:treeNodesAdaptor">  
    <rich:treeModelAdaptor><!-- Renamed -->  
      <xsl:apply-templates select="@*|node()" />  
    </rich:treeModelAdaptor>  
  </xsl:template>  
  
  
  <xsl:template match="rich:recursiveTreeNodesAdaptor" name="rich:recursiveTreeNodesAdaptor">  
    <rich:treeModelRecursiveAdaptor><!-- Renamed -->  
      <xsl:apply-templates select="@*|node()" />  
    </rich:treeModelRecursiveAdaptor>  
  </xsl:template>  
  
  
  <xsl:template match="rich:tree" name="rich:tree">  
    <rich:tree><!-- equal -->  
      <xsl:apply-templates select="@*|node()" />  
    </rich:tree>  
  </xsl:template>  
  
  
  <xsl:template match="rich:tree/@nodeFace">  
    <xsl:attribute name="nodeType"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
  
  
  <xsl:template match="rich:tree/@switchType">  
    <xsl:attribute name="toggleType"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
  
  
  <xsl:template match="rich:tree/@treeNodeVar">  
    <xsl:attribute name="var"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
  
  
  
  
  
  
  <!-- Rich Miscellaneous Components -->  
  
  
  <xsl:template match="rich:componentControl/@for">  
    <xsl:attribute name="target"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
    
  <xsl:template match="rich:componentControl/@event">  
    <xsl:attribute name="event">  
      <xsl:value-of select="replace( . , 'on(.*)', '$1')" /><!-- Remove the 'on' part of the event name -->  
    </xsl:attribute>  
  </xsl:template>  
  <xsl:template match="rich:hotKey/@handler" >  
    <xsl:attribute name="onkeyup"><xsl:value-of select="." /></xsl:attribute><!-- Renamed -->  
  </xsl:template>  
    
  <!-- Deleted but deal with it after testing  
    <xsl:template match="rich:effect" />  
    <xsl:template match="rich:gmap" />  
    <xsl:template match="rich:insert" />  
    <xsl:template match="rich:page" />  
    <xsl:template match="rich:components" />  
    <xsl:template match="rich:virtualEarth" />  
    <xsl:template match="rich:paint2D" />    
    <xsl:template match="rich:colorPicker" />   
    <xsl:template match="rich:beanValidator" />   
    <xsl:template match="a4j:form" />   
    <xsl:template match="a4j:AjaxListener" />-->  
</xsl:stylesheet>  

