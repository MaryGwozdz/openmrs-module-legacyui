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
        var setupCompleteJS;
        <%
            Integer userId = (Integer) Context.getAuthenticatedUser().getUserId();
            String patientId = request.getParameter("patientId");
            SummaryTable summaryTable = new SummaryTable(patientId, userId);
            List<String> sumItems = summaryTable.retrieveUserSummaryItems();
            Boolean setupComplete = sumItems.size() > 0;
            Map<String, List<Object>> sumDataMap = new HashMap<>();
            if (setupComplete) {
                sumDataMap = summaryTable.generateSummaryTable(sumItems);
            }
        %>


        if( window.localStorage ) {
          if( !localStorage.getItem('firstLoad') ) {
            localStorage['firstLoad'] = true;
            window.location.reload();
          }
          else {
            localStorage.removeItem('firstLoad');
          }
        }

        setupCompleteJS = <%= setupComplete %>;

        if (setupCompleteJS === true) {
            jQuery('#setup').hide();
            jQuery('#summary').show();
        } else {
            jQuery('#summary').hide();
            jQuery('#setup').show();
        }

        jQuery('#setupDoneButton').click(function(){
            <%
            String sumData[]= request.getParameterValues("sumData");
            summaryTable.saveSetup(sumData);
            sumDataMap = summaryTable.generateSummaryTable(sumItems);
            setupComplete = true;
            %>
            jQuery('#setup').hide();
            jQuery('#summary').show();
            history.go(0);
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
        <p><input type="submit" id="setupDoneButton" value="Done"/>
    </form>
</div>
<div id="summary">
<div id="patientActionsBoxHeader" class="boxHeader${model.patientVariation}"><openmrs:message code="Summary" /></div>
    <br>
    <table border="3" bordercolor="#laac9b">
    <%
        for(Map.Entry<String, List<Object>> entry : sumDataMap.entrySet()) {
            String key = entry.getKey();
            %><tr><td><b><%=key %></b></td><%
            for (Object value : entry.getValue()) {
                %><td><%=value.toString() %></td><%
            }
        %></tr><%
        }
        %>
        </table><%
    %>
</div>
</openmrs:hasPrivilege>
