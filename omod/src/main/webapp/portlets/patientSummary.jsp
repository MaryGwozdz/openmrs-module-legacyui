<%@ include file="/WEB-INF/view/module/legacyui/template/include.jsp" %>

<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.openmrs.web.SummaryTable" %>
<%@ page import="org.openmrs.api.context.Context" %>
<%@ page import="org.openmrs.web.servlet.PopulateSummaryServlet" %>


<openmrs:htmlInclude file="/scripts/easyAjax.js" />
<openmrs:htmlInclude file="/dwr/interface/DWRRelationshipService.js" />
<openmrs:htmlInclude file="/dwr/interface/DWRPatientService.js" />
<openmrs:htmlInclude file="/dwr/interface/DWRObsService.js" />
<openmrs:htmlInclude file="/dwr/interface/DWRConceptService.js" />
<openmrs:htmlInclude file="/dwr/engine.js" />
<openmrs:htmlInclude file="/dwr/util.js" />

<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables.css" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui-1.7.2.custom.min.js" />
<link href="<openmrs:contextPath/>/scripts/jquery-ui/css/<spring:theme code='jqueryui.theme.name' />/jquery-ui.custom.css" type="text/css" rel="stylesheet" />
<openmrs:htmlInclude file="/scripts/flot/jquery.flot.js" />
<openmrs:htmlInclude file="/scripts/flot/jquery.flot.multiple.threshold.js"/>

<openmrs:globalProperty var="importantIdentifiers" key="patient_identifier.importantTypes" />
<openmrs:globalProperty key="use_patient_attribute.healthCenter" defaultValue="false" var="showHealthCenter"/>

<openmrs:extensionPoint pointId="org.openmrs.patientDashboard.summaryBox" type="html" parameters="patientId=${model.patient.patientId}" requiredClass="org.openmrs.module.web.extension.BoxExt">
	<openmrs:hasPrivilege privilege="${extension.requiredPrivilege}">
		<div class="boxHeader${model.patientVariation}"><openmrs:message code="${extension.title}" /></div>
		<div class="box${model.patientVariation}"><openmrs:message code="${extension.content}" />
  			<c:if test="${extension.portletUrl != null}">
   				<openmrs:portlet url="${extension.portletUrl}" moduleId="${extension.moduleId}" id="${extension.portletUrl}" patientId="${patient.patientId}" parameters="allowEdits=true"/>
 			</c:if>
		</div>
		<br />
	</openmrs:hasPrivilege>
</openmrs:extensionPoint>

<script type="text/javascript">
    jQuery(document).ready(function(){
        <%
            Integer userId = (Integer) Context.getAuthenticatedUser().getUserId();
        %>

        var userIdJS = <%= userId %>;

        jQuery.get("http://localhost:8888/openmrs/ms/legacyui/PopulateSummaryServlet?patientId=${model.patient.patientId}&userId=" + userIdJS, function(responseJson) {
            if(responseJson != "") {
                jQuery('#setup').hide();
                jQuery('#summary').show();
                jQuery.each(responseJson, function (key, value) {
                    var row = "<tr><td><b>" + key + "</b></td>";
                    for (var i = 0; i < value.length; i++) {
                        row = row + "<td>" + value[i] + "</td>";
                    }
                    row = row + "</tr>";
                    jQuery("#summaryTable").append(row);
                });
            } else {
                jQuery('#summary').hide();
                jQuery('#setup').show();
            }
        });

        jQuery('#setupDoneButton').on("click", function (e) {
            e.preventDefault();
            jQuery.post("http://localhost:8888/openmrs/ms/legacyui/PopulateSummaryServlet?patientId=${model.patient.patientId}&userId=" + userIdJS, jQuery("input[name='sumData']:checked").serialize(), function(msg) {});

            setTimeout(function(){
                jQuery.get("http://localhost:8888/openmrs/ms/legacyui/PopulateSummaryServlet?patientId=${model.patient.patientId}&userId=" + userIdJS, function(responseJson) {
                    if(responseJson != null) {
                        jQuery.each(responseJson, function (key, value) {
                            var row = "<tr><td><b>" + key + "</b></td>";
                            for (var i = 0; i < value.length; i++) {
                                row = row + "<td>" + value[i] + "</td>";
                            }
                            row = row + "</tr>";
                            jQuery("#summaryTable").append(row);
                        });
                    }
                });

                jQuery('#setup').hide();
                jQuery('#summary').show();

            }, 2000);

        });
    });
</script>

<openmrs:hasPrivilege privilege="Patient Summary - View Patient Actions">

<div id="setup">
<div id="patientActionsBoxHeader" class="boxHeader${model.patientVariation}"><openmrs:message code="patient.summary.setup" /></div>

     <form name="form1">
        <input type='hidden' name='patientId' id='patientId' value='${model.patient.patientId}' />
        <p><input type="checkbox" name="sumData" value="Program(s)"/>Program(s)</p>
        <p><input type="checkbox" name="sumData" value="HIV Status"/>HIV Status</p>
        <p><input type="checkbox" name="sumData" value="Indication(s)"/>Indication(s)</p>
        <p><input type="checkbox" name="sumData" value="Relationship(s)"/>Relationship(s)</p>
        <p><input type="checkbox" name="sumData" value="Medication(s)"/>Medication(s)</p>
        <p><input type="checkbox" name="sumData" value="Allergies"/>Allergies</p>
        <p><input type="submit" id="setupDoneButton" value="Done"/>
    </form>
</div>
<div id="summary">
<div id="patientActionsBoxHeader" class="boxHeader${model.patientVariation}"><openmrs:message code="Summary" /></div>
    <br>
    <table id="summaryTable" border="3" bordercolor="#laac9b"></table>
</div>
</openmrs:hasPrivilege>
