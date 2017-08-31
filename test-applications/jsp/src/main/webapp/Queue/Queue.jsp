<%@ taglib uri="http://xmlns.jcp.org/jsf/html" prefix="h"%>
<%@ taglib uri="http://xmlns.jcp.org/jsf/core" prefix="f"%>
<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>

<f:subview id="queueSubviewID">	
	<a4j:queue binding="#{queue.myQueue}" 
	disabled="#{queue.disabled}"	 
	ignoreDupResponses="#{queue.ignoreDupResponses}" 
	name="formQueue"
	onbeforedomupdate="#{event.onbeforedomupdate}"
	oncomplete="#{event.oncomplete}"
	onerror="#{event.onerror}"
	onsizeexceeded="alert('onsizeexceeded');"
	onsubmit="#{event.onsubmit}"	
	requestDelay="#{queue.requestDelay}"
	size="#{queue.size}"
	sizeExceededBehavior="#{queue.sizeExceededBehavior}"
	timeout="#{queue.timeout}"/>
</f:subview>
