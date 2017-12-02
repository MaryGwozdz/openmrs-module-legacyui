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
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.Set" %>
<%@ page import="org.openmrs.web.HistoryForm" %>
<%@ page import="org.openmrs.api.context.Context" %>
<%@ page import="org.openmrs.web.servlet.PopulateMedicationsServlet" %>
<%@ page import="org.openmrs.web.servlet.PopulateAllergiesServlet" %>


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

<openmrs:extensionPoint pointId="org.openmrs.patientDashboard.historyBox" type="html" parameters="patientId=${model.patient.patientId}" requiredClass="org.openmrs.module.web.extension.BoxExt">
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
        jQuery.get("http://localhost:8888/openmrs/ms/legacyui/PopulateMedicationsServlet?patientId=${model.patient.patientId}", function(responseJson) {
            if(responseJson != null) {
                jQuery.each(responseJson, function (index, item) {
                    jQuery('#medicationTable').append(jQuery("<tr><td>" + item + "</td></tr>"));
                });
            }
        });

        jQuery.get("http://localhost:8888/openmrs/ms/legacyui/PopulateAllergiesServlet?patientId=${model.patient.patientId}", function(responseJson) {
            if(responseJson != null) {
                jQuery.each(responseJson, function (index, item) {
                    jQuery('#allergiesTable').append(jQuery("<tr><td>" + item + "</td></tr>"));
                });
            }
        });

        jQuery('#medicationsSaveButton').on("click", function (e) {
            e.preventDefault();
            jQuery.post("http://localhost:8888/openmrs/ms/legacyui/PopulateMedicationsServlet?patientId=${model.patient.patientId}", jQuery("input[name='medicationData']:checked").serialize(), function(msg) {});

            setTimeout(function(){
                jQuery.get("http://localhost:8888/openmrs/ms/legacyui/PopulateMedicationsServlet?patientId=${model.patient.patientId}", function(responseJson) {
                    if(responseJson != null) {
                        jQuery("#medicationTable").find('tbody').empty();
                        jQuery.each(responseJson, function (index, item) {
                            var row = "<tr><td>" + item + "</td></tr>";
                            jQuery("#medicationTable").find('tbody').append(row);
                        });
                    }
                });
            }, 2000);

        });

        jQuery('#allergySaveButton').on("click", function (e) {
            e.preventDefault();
            jQuery.post("http://localhost:8888/openmrs/ms/legacyui/PopulateAllergiesServlet?patientId=${model.patient.patientId}", jQuery("input[name='allergyData']:checked").serialize(), function(msg) {});

            setTimeout(function(){
                jQuery.get("http://localhost:8888/openmrs/ms/legacyui/PopulateAllergiesServlet?patientId=${model.patient.patientId}", function(responseJson) {
                    if(responseJson != null) {
                        jQuery("#allergiesTable").find('tbody').empty();
                        jQuery.each(responseJson, function (index, item) {
                            var row = "<tr><td>" + item + "</td></tr>";
                            jQuery("#allergiesTable").find('tbody').append(row);
                        });
                    }
                });
            }, 2000);
        });
    });
</script>

<openmrs:hasPrivilege privilege="Patient Summary - View Patient Actions">

<div id="medications" style="padding-bottom: 250px;">
<div id="patientActionsBoxHeader" class="boxHeader${model.patientVariation}"><openmrs:message code="Medications" /></div>
<div id="medicationInput" style="float: right; width: 50%;">
     <form name="form1">
        <input type='hidden' name='patientId' id='patientId' value='${model.patient.patientId}' />
        <p><input type="checkbox" name="medicationData" value="2"/>Triomune-30</p>
        <p><input type="checkbox" name="medicationData" value="3"/>Triomune-40</p>
        <p><input type="checkbox" name="medicationData" value="5"/>d4T-30</p>
        <p><input type="checkbox" name="medicationData" value="6"/>d4T-40</p>
        <p><input type="checkbox" name="medicationData" value="9"/>DDI 125</p>
        <p><input type="checkbox" name="medicationData" value="10"/>DDI 200</p>
        <p><input type="submit" id="medicationsSaveButton" value="Save"/>
    </form>
</div>
<div id="medicationOutput" style="float: left; width: 50%;">
<div id="ajaxTestMedications"></div>
    <br>
    <table id="medicationTable" border="3" bordercolor="#laac9b"></table>
</div>
</div>
</openmrs:hasPrivilege>

<openmrs:hasPrivilege privilege="Patient Summary - View Patient Actions">

<div id="allergies" style="padding-bottom: 100px;">
<div id="patientActionsBoxHeader" class="boxHeader${model.patientVariation}"><openmrs:message code="Allergies" /></div>
<div id="allergyInput" style="float: right; width: 50%;">
     <form name="form1">
        <input type='hidden' name='patientId' id='patientId' value='${model.patient.patientId}' />
        <p><input type="checkbox" name="allergyData" value="2362"/>Allergy to Sulfa</p>
        <p><input type="checkbox" name="allergyData" value="2361"/>Allergy to Penicillin</p>
        <p><input type="checkbox" name="allergyData" value="1083"/>Allergy to Other Medicine</p>
        <p><input type="submit" id="allergySaveButton" value="Save"/>
    </form>
</div>
<div id="allergyOutput" style="float: left; width: 50%;">
<div id="ajaxTestAllergies"></div>
    <br>
    <table id="allergiesTable" border="3" bordercolor="#laac9b"></table>
</div>
</div>

</openmrs:hasPrivilege>
