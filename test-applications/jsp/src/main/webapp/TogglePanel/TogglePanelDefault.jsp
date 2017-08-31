<%@ taglib uri="http://xmlns.jcp.org/jsf/html" prefix="h"%>
<%@ taglib uri="http://xmlns.jcp.org/jsf/core" prefix="f"%>
<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>
	<rich:togglePanel switchType="client" stateOrder="closed,tip1">

		<f:facet name="closed">
			<rich:toggleControl>
				<h:graphicImage style="border-width:0" value="/pics/ajax_process.gif" />
			</rich:toggleControl>
		</f:facet>

		<f:facet name="tip1">
			<rich:toggleControl>
				<h:graphicImage style="border-width:0" value="/pics/ajax_stoped.gif" />
			</rich:toggleControl>
		</f:facet>
	</rich:togglePanel>
