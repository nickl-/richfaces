<%@ taglib uri="http://xmlns.jcp.org/jsf/html" prefix="h" %>
<%@ taglib uri="http://xmlns.jcp.org/jsf/core" prefix="f" %>
<%@ taglib uri="http://labs.jboss.com/jbossrichfaces/ui/calendar" prefix="rich" %>
<html>
<body>
<f:view>
    <rich:calendar id="calendar" /><br />
    <h:outputText value="#{rf8052.date}" />
</f:view>

</body>
</html>
