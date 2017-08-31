<%@ taglib uri="http://xmlns.jcp.org/jsf/html" prefix="h" %>
<%@ taglib uri="http://xmlns.jcp.org/jsf/core" prefix="f"%>
<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j" %>
<html>
	<head>
		<title></title>
	</head>
	<body>
		<f:view>
		    <a4j:mediaOutput element="object" cacheable="false" session="true" 
		          value="rc044-010d-richfaces_3.pdf" createContent="#{bean.paint}" 
		          type="application/pdf">something</a4j:mediaOutput>
		</f:view>
	</body>	
</html>  
