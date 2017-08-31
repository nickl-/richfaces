<%@ taglib uri="http://xmlns.jcp.org/jsf/html" prefix="h" %>
<%@ taglib uri="http://xmlns.jcp.org/jsf/core" prefix="f" %>
<%@ taglib uri="http://labs.jboss.com/jbossrichfaces/ui/calendar" prefix="rich" %>
<%@ taglib uri="http://labs.jboss.com/jbossrichfaces/ui/panelmenu" prefix="pm" %>
<html>
<body>
<f:view>
    <h:outputLink id="link" target="detailsPanelFrame" value="other.jsf">
        <h:outputText value="Click me (#{rf8052.date})" />
    </h:outputLink>
    <h:outputLink id="link2" target="qwe" value="other.jsf">
        <h:outputText value="Click me 2 (#{rf8052.date})" />
    </h:outputLink>
</f:view>
</body>
</html>
