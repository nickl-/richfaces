<%@ taglib uri="http://xmlns.jcp.org/jsf/html" prefix="h"%>
<%@ taglib uri="http://xmlns.jcp.org/jsf/core" prefix="f"%>
<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>
	<rich:dataDefinitionList value="#{dataDefinitionList.arrDefault}" var="def">
		<h:outputText value="#{def}" />
	</rich:dataDefinitionList>
