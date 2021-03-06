<%@ taglib uri="http://xmlns.jcp.org/jsf/html" prefix="h" %>
<%@ taglib uri="http://xmlns.jcp.org/jsf/core" prefix="f" %>
<%@ taglib uri="http://labs.jboss.com/jbossrichfaces/ui/simpleTogglePanel" prefix="stp" %>
<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<html>
<head>
    <title>RF-2195</title>
</head>

<body>
	<f:view>
	<h:form id="SimpleToglePanel_form">
		<stp:simpleTogglePanel label="STP" switchType="ajax" reRender="out" action="#{simpleBean.changeLabel}">
			<h:outputText value="content"/>
		</stp:simpleTogglePanel>
		<h:outputText value="#{simpleBean.property}" id="out"/>
	</h:form>
	<a4j:log popup="false"/>
	</f:view>
</body>
</html>
