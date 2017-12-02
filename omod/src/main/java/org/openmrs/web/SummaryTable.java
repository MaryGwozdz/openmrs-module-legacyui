/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;


/**
 * Created by gwozdz on 11/6/2017.
 */
public class SummaryTable {
    private static String url="jdbc:mysql://localhost:3306/openmrs";
    private static String username="root";
    private static String password="password1";
    private static String programIdQuery="SELECT program_id FROM patient_program WHERE patient_id= ?";
    private static String stateQuery="SELECT state FROM patient_state WHERE patient_program_id = ?";
    private static String conceptQuery="SELECT concept_id FROM program_workflow_state WHERE program_workflow_state_id = ?";
    private static String programStateQuery="SELECT name FROM concept_name WHERE concept_id = ?";
    private static String indicationIdQuery = "SELECT indication_concept_id FROM visit WHERE patient_id = ?";
    private static String indicationQuery = "SELECT name FROM concept_name WHERE concept_name_id = ?";
    private static String relationshipPersonQuery = "SELECT person_a FROM relationship WHERE voided=0 AND person_b = ?";
    private static String relationshipTypeIdQuery = "SELECT relationship FROM relationship WHERE person_b = ? AND person_a = ?";
    private static String relationshipTypeQuery = "SELECT a_is_to_b FROM relationship_type WHERE relationship_type_id = ?";
    private static String relationshipFirstNameQuery = "SELECT given_name FROM person_name WHERE person_id = ?";
    private static String relationshipLastNameQuery = "SELECT family_name FROM person_name WHERE person_id = ?";
    private static String programNameQuery = "SELECT name FROM program WHERE program_id = ?";
    private static String patientId;
    private static Integer userId;
    public Boolean setupComplete = false;

    public SummaryTable(String patientId, Integer userId) {
        this.patientId = patientId;
        this.userId = userId;
    }

    public Map<String, List<Object>> generateSummaryTable(List<String> sumDataList) {
        Map<String, List<Object>> sumDataMap = new HashMap<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            if (sumDataList.contains("Program(s)")) {
                List<Object> programIds = retrieveMultipleFromDB(programIdQuery, patientId);
                List<Object> programNameList = new ArrayList<>();
                for (int i = 0; i < programIds.size(); i++) {
                    if (programIds.get(i) != null) {
                        programNameList.add(retrieveFromDB(programNameQuery, programIds.get(i).toString()));
                    }
                }
                sumDataMap.put("Program(s)", programNameList);

            }
            if (sumDataList.contains("HIV Status")) {
                List<Object> programIds = retrieveMultipleFromDB(programIdQuery, patientId);
                Integer concept = 0;
                List<Object> programState = new ArrayList<>();
                if (programIds.contains(1)) {
                    List<Object> states = retrieveMultipleFromDB(stateQuery, Integer.toString(1));
                    if (states.contains(2)) {
                        concept = (Integer) retrieveFromDB(conceptQuery, Integer.toString(2));
                        programState = new ArrayList<>(Arrays.asList(retrieveFromDB(programStateQuery, concept.toString())));

                    }
                    sumDataMap.put("HIV Status", programState);

                }
            }
            if (sumDataList.contains("Indication(s)")) {
                List<Object> indicationIds = retrieveMultipleFromDB(indicationIdQuery, patientId);
                List<Object> indicationList = new ArrayList<>();
                for (int i = 0; i < indicationIds.size(); i++) {
                    if (indicationIds.get(i) != null) {
                        indicationList.add(retrieveFromDB(indicationQuery, indicationIds.get(i).toString()));
                    }
                }
                sumDataMap.put("Indication(s)", indicationList);
            }
            if (sumDataList.contains("Relationship(s)")) {
                List<Object> relationshipNameIds = retrieveMultipleFromDB(relationshipPersonQuery, patientId);
                List<Object> relationshipStringList = new ArrayList<>();

                for (int i = 0; i < relationshipNameIds.size(); i++) {
                    Integer relationshipTypeId = null;
                    // Retrieve from db with two where clauses
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                        Connection conn = DriverManager.getConnection(url, username, password);
                        PreparedStatement ps = conn.prepareStatement(relationshipTypeIdQuery);
                        ps.setString(1, patientId);
                        ps.setString(2, relationshipNameIds.get(i).toString());
                        ResultSet rs = ps.executeQuery();
                        rs.next();
                        relationshipTypeId = rs.getInt(1);
                        ps.close();
                        rs.close();
                        conn.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String relationshipType = (String) retrieveFromDB(relationshipTypeQuery, relationshipTypeId.toString());
                    String givenName = (String) retrieveFromDB(relationshipFirstNameQuery, relationshipNameIds.get(i).toString());
                    String familyName = (String) retrieveFromDB(relationshipLastNameQuery, relationshipNameIds.get(i).toString());
                    String relationshipString = relationshipType + ": " + givenName + " " + familyName;
                    relationshipStringList.add(relationshipString);
                }
                sumDataMap.put("Relationship(s)", relationshipStringList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sumDataMap;
    }

    private List<Object> retrieveMultipleFromDB(String query, String id) {
        List<Object> data = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                data.add(rs.getObject(1));
            }
            ps.close();
            rs.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    private Object retrieveFromDB(String query, String id) {
        Object data = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                data = rs.getObject(1);
            }
            ps.close();
            rs.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public List<String> retrieveUserSummaryItems() {
        List<String> sumItems = new ArrayList<>();
        String sumItemQuery = "SELECT summary_item FROM user_summary WHERE user_id = ?";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = conn.prepareStatement(sumItemQuery);
            ps.setString(1, userId.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                sumItems.add(rs.getString(1));
            }
            ps.close();
            rs.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (sumItems.size() > 0) {
            setupComplete = true;
        }

        return sumItems;
    }

    public void saveSetup(String[] sumData) {
        if (sumData == null) {
            return;
        }
        String saveSumItemQuery = "INSERT INTO user_summary (user_id, summary_item) VALUES (?,?)";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = conn.prepareStatement(saveSumItemQuery);
            for (int i = 0; i < sumData.length; i++) {
                ps.setString(1, userId.toString());
                ps.setString(2, sumData[i]);
                ps.executeUpdate();
            }
            ps.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean isSetupComplete() {
        return setupComplete;
    }



}
