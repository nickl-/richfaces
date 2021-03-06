<%@ taglib uri="http://xmlns.jcp.org/jsf/html" prefix="h"%>
<%@ taglib uri="http://xmlns.jcp.org/jsf/core" prefix="f"%>
<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>

<f:subview id="dataDefinitionListSubviewID">

	<a4j:outputPanel ajaxRendered="true">
		<h:messages />
	</a4j:outputPanel>

	<script>
		DnD.CLIENT_VALIDATION_OFF = false;
	</script>

	<h:panelGroup id="dragValueText">
		<h:outputText value="#{dndBean.dragValue}" />
	</h:panelGroup>

	<h:panelGrid columns="2">
		<h:dataTable var="type" value="#{dndBean.types}">
			<h:column>
				<h:panelGrid styleClass="dropzoneDecoration" id="drag1">
					<h:outputText value="#{type} - drag" />
					<rich:dragSupport dragType="#{type}" dragValue="#{type} - value"
						actionListener="#{dndBean.actListenerDrag}"
						action="#{dndBean.dragAction}"
						dragListener="#{dndBean.processDrag}"
						oncomplete="#{event.oncomplete}" ondragend="#{event.ondragend}"
						ondragstart="#{event.ondragstart}"
						onbeforedomupdate="#{event.onbeforedomupdate}"
						ondropout="#{event.ondropout}" ondropover="#{event.ondropover}"
						id="dragSupportID" binding="#{dndBean.htmlDrag}" dragIndicator="indicator">
						<rich:dndParam name="default">
							<h:graphicImage value="/pics/ajax_process.gif" />
						</rich:dndParam>
						<rich:dndParam name="accept">
							<h:graphicImage value="/src/main/webapp/pics/expand.gif"/>							
						</rich:dndParam>
						<rich:dndParam name="reject">
							<h:graphicImage value="/src/main/webapp/pics/collapse.gif"/>							
						</rich:dndParam>
					</rich:dragSupport>
				</h:panelGrid>
			</h:column>
		</h:dataTable>

		<h:dataTable var="type" value="#{dndBean.types}">
			<h:column>
				<h:panelGrid styleClass="dropzoneDecoration" id="drop2">
					<h:outputText value="#{type} - drop" />
					<rich:dropSupport reRender="dragValueText" id="dropSupportID"
						actionListener="#{dndBean.actListenerDrop}"
						action="#{dndBean.dropAction}" acceptedTypes="#{type}"
						dropListener="#{dndBean.processDrop}" dropValue="#{type} - value"
						ondragenter="#{event.ondragenter}"
						ondragexit="#{event.ondragexit}" ondrop="#{event.ondrop}"
						ondropend="#{event.ondropend}" oncomplete="#{event.oncomplete}"
						onbeforedomupdate="#{event.onbeforedomupdate}"
						binding="#{dndBean.htmlDrop}">						
					</rich:dropSupport>
				</h:panelGrid>
			</h:column>
		</h:dataTable>

		<h:panelGrid id="dndActionID" columns="1">
			<h:commandButton action="#{dndBean.add1}" value="add Drag test" />
			<h:commandButton action="#{dndBean.add2}" value="add Drop test" />
			<a4j:commandButton value="Show action" reRender="dndActionID"
				style=" width : 95px;"></a4j:commandButton>
			<h:outputText value="#{dndBean.actionDrag}" />
			<h:outputText value="#{dndBean.actionDrop}" />
			<h:outputText value="#{dndBean.actionListenerDrag}" />
			<h:outputText value="#{dndBean.actionListenerDrop}" />
		</h:panelGrid>
	</h:panelGrid>
	<rich:dragIndicator id="indicator" acceptClass="accept"
		rejectClass="reject" style="#{style.style}"
		styleClass="#{style.styleClass}">
		<f:facet name="single">
			<f:verbatim>{marker} <b>{testDrag}</b> {label}</f:verbatim>
		</f:facet>
		<rich:dndParam name="accept" value="ACCEPT:" />
		<rich:dndParam name="reject">
		
			<f:verbatim>
				<i style="text-decoration: line-through;">REJECT:</i>
			</f:verbatim>
		</rich:dndParam>
		<rich:dndParam name="dropping" value="dropping"></rich:dndParam>
		<rich:dndParam name="dragging" value="dragging"></rich:dndParam>
	</rich:dragIndicator>

	<rich:dragIndicator id="dIndicator" acceptClass="defaultIndicator"
		rejectClass="reject">
		<f:facet name="single">
			<f:verbatim>{marker} <b>{testDrag}</b> {label}</f:verbatim>
		</f:facet>
		<rich:dndParam name="accept" value="ACCEPT:" />
		<rich:dndParam name="reject">
			<f:verbatim>
				<i style="text-decoration: line-through;">REJECT:</i>
			</f:verbatim>
		</rich:dndParam>
	</rich:dragIndicator>

	<h:panelGrid columns="1" style="position: relative; left: 140px;">
		<h:panelGrid columns="1"
			style="position: absolute; top: 30px; left: 300px;">
			<rich:dragIndicator id="defaultIndicator">
			</rich:dragIndicator>
		</h:panelGrid>
	</h:panelGrid>

	<h:panelGrid columns="4" cellspacing="20">
		<h:panelGrid styleClass="dropzoneDecoration" id="grid1">
			<f:verbatim>Accepts file and folder... Customizes</f:verbatim>
			<rich:dropSupport id="zone1" oncomplete="#{event.oncomplete}"
				ondragenter="#{event.ondragenter}" ondragexit="#{event.ondragexit}"
				ondropend="#{event.ondropend}" ondrop="#{event.ondrop}"
				onbeforedomupdate="#{event.onbeforedomupdate}"
				acceptedTypes="file, folder" typeMapping="{file: testDrop}">
				<rich:dndParam name="testDrop">
					<h:graphicImage height="16" width="16"
						value="#{icon.iconFileManager}" />
				</rich:dndParam>
			</rich:dropSupport>
		</h:panelGrid>

		<h:panelGrid styleClass="dropzoneDecoration" id="grid2">
			<f:verbatim>Accepts none</f:verbatim>
			<rich:dropSupport>
			</rich:dropSupport>
		</h:panelGrid>

		<h:panelGrid styleClass="dropzoneDecoration" id="grid3">
			<f:verbatim>Accepts none... Customizes</f:verbatim>
			<rich:dropSupport typeMapping="{file: testDrop}"
				oncomplete="#{event.oncomplete}" ondragenter="#{event.ondragenter}"
				ondragexit="#{event.ondragexit}" ondropend="#{event.ondropend}"
				ondrop="#{event.ondrop}"
				onbeforedomupdate="#{event.onbeforedomupdate}">
				<rich:dndParam name="testDrop">
					<h:graphicImage height="16" width="16"
						value="#{icon.iconFileManagerReject}" />
				</rich:dndParam>
			</rich:dropSupport>
		</h:panelGrid>

		<h:panelGrid styleClass="dropzoneDecoration" id="grid4">
			<f:verbatim>Accepts file and folder</f:verbatim>
			<rich:dropSupport acceptedTypes="file, folder"
				oncomplete="#{event.oncomplete}" ondragenter="#{event.ondragenter}"
				ondragexit="#{event.ondragexit}" ondropend="#{event.ondropend}"
				ondrop="#{event.ondrop}"
				onbeforedomupdate="#{event.onbeforedomupdate}">
				<rich:dndParam name="testDrop" value="testDropValue" />
			</rich:dropSupport>
		</h:panelGrid>

		<h:panelGrid id="grid5">
			<rich:dragSupport dragType="file" ondragend="#{event.ondragend}"
				ondragstart="#{event.ondragstart}" ondropout="#{event.ondropout}"
				ondropover="#{event.ondropover}" oncomplete="#{event.oncomplete}"
				onbeforedomupdate="#{event.onbeforedomupdate}">
				<rich:dndParam name="label" value="Label" />
				<rich:dndParam name="testDrag" value="testDragValue" />
			</rich:dragSupport>
			<f:verbatim>File Draggable - no indicator</f:verbatim>
		</h:panelGrid>

		<h:panelGrid id="grid6">
			<rich:dragSupport dragType="file" dragIndicator="indicator"
				ondragend="#{event.ondragend}" ondragstart="#{event.ondragstart}"
				ondropout="#{event.ondropout}" ondropover="#{event.ondropover}"
				oncomplete="#{event.oncomplete}"
				onbeforedomupdate="#{event.onbeforedomupdate}">
				<rich:dndParam name="label" value="Label" />
				<rich:dndParam name="testDrag" value="testDragValue" />
			</rich:dragSupport>
			<f:verbatim>File Draggable with indicator</f:verbatim>
		</h:panelGrid>

		<h:panelGrid id="grid7">
			<rich:dragSupport dragType="folder" dragIndicator="indicator"
				ondragend="#{event.ondragend}" ondragstart="#{event.ondragstart}"
				ondropout="#{event.ondropout}" ondropover="#{event.ondropover}"
				oncomplete="#{event.oncomplete}"
				onbeforedomupdate="#{event.onbeforedomupdate}">
				<rich:dndParam name="label" value="Label" />
				<rich:dndParam name="testDrag" value="testDragValue for Folder" />
			</rich:dragSupport>
			<f:verbatim>Folder Draggable with indicator</f:verbatim>
		</h:panelGrid>

		<h:outputText />

		<h:panelGrid id="grid8">
			<rich:dragSupport dragType="folder" ondragend="#{event.ondragend}"
				ondragstart="#{event.ondragstart}" ondropout="#{event.ondropout}"
				ondropover="#{event.ondropover}" oncomplete="#{event.oncomplete}"
				onbeforedomupdate="#{event.onbeforedomupdate}">
				<rich:dndParam name="label" value="Label" />
				<rich:dndParam name="testDrag" value="testDragValue for Folder" />
			</rich:dragSupport>
			<f:verbatim>Folder Draggable - no indicator</f:verbatim>
		</h:panelGrid>

		<h:panelGrid id="grid9">
			<rich:dragSupport dragType="file" dragIndicator="defaultIndicator"
				ondragend="#{event.ondragend}" ondragstart="#{event.ondragstart}"
				ondropout="#{event.ondropout}" ondropover="#{event.ondropover}">
				<rich:dndParam name="testDrag" value="testDragValue" />
				<rich:dndParam name="marker" value="testMarkerValue" />
				<rich:dndParam name="label" value="testDragValue" />
			</rich:dragSupport>
			<f:verbatim>File Draggable with defaultIndicator</f:verbatim>
		</h:panelGrid>

		<h:panelGrid id="grid10">
			<rich:dragSupport dragType="folder" dragIndicator="defaultIndicator"
				ondragend="#{event.ondragend}" ondragstart="#{event.ondragstart}"
				ondropout="#{event.ondropout}" ondropover="#{event.ondropover}"
				oncomplete="#{event.oncomplete}"
				onbeforedomupdate="#{event.onbeforedomupdate}">
				<rich:dndParam name="label" value="testDragValue for Folder" />
			</rich:dragSupport>
			<f:verbatim>Folder Draggable with defaultIndicator</f:verbatim>
		</h:panelGrid>
		<h:outputText />
	</h:panelGrid>

	<h:panelGrid id="renderedId">
		<rich:dragSupport dragType="file" dragIndicator="defaultIndicator"
			ondragend="#{event.ondragend}" ondragstart="#{event.ondragstart}"
			ondropout="#{event.ondropout}" ondropover="#{event.ondropover}"
			oncomplete="#{event.oncomplete}"
			onbeforedomupdate="#{event.onbeforedomupdate}">
			<rich:dndParam name="marker" value="testMarkerValue" />
			<rich:dndParam name="label" value="testDragValue" />
		</rich:dragSupport>

		<h:graphicImage id="dragImage" value="/pics/masshtaby_01.jpg"
			width="200px" height="200px" />
		<f:verbatim>dragSupport</f:verbatim>
	</h:panelGrid>
	<br />
	<br />
	<div style="FONT-WEIGHT: bold;">rich:findComponent</div>
	<h:panelGrid columns="2">
		<rich:column>
			<a4j:commandLink value="getDragType" reRender="findID1"></a4j:commandLink>
		</rich:column>
		<rich:column id="findID1">
			<h:outputText value="#{rich:findComponent('dragSupportID').dragType}" />
		</rich:column>
		<rich:column>
			<a4j:commandLink value="getDropValue" reRender="findID2"></a4j:commandLink>
		</rich:column>
		<rich:column id="findID2">
			<h:outputText
				value="#{rich:findComponent('dropSupportID').dropValue}" />
		</rich:column>
		<rich:column>
			<a4j:commandLink value="getAcceptClass" reRender="findID3"></a4j:commandLink>
		</rich:column>
		<rich:column id="findID3">
			<h:outputText value="#{rich:findComponent('indicator').acceptClass}" />
		</rich:column>
	</h:panelGrid>
</f:subview>
