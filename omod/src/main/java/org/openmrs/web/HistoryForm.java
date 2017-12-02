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
public class HistoryForm {
    private static String url = "jdbc:mysql://localhost:3306/openmrs";
    private static String username = "root";
    private static String password = "password1";
    private static String patientId;

    public HistoryForm(String patientId) {
        this.patientId = patientId;
    }

    public List<Object> generateMedicationTable() {
        String drugIdQuery = "SELECT drug_id FROM patient_history WHERE patient_id = ?";
        String drugNameQuery = "SELECT name FROM drug WHERE drug_id = ?";
        List<Object> medicationList = new ArrayList<>();

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Set<Object> drugIds = retrieveMultipleFromDB(drugIdQuery, patientId);
            Iterator<Object> drugIdItr = drugIds.iterator();
            while (drugIdItr.hasNext()) {
                Object drugId = drugIdItr.next();
                if (drugIds != null) {
                    medicationList.add(retrieveFromDB(drugNameQuery, drugId.toString()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return medicationList;
    }

    public List<Object> generateAllergyTable() {
        String allergyIdQuery = "SELECT allergy_id FROM patient_history WHERE patient_id = ?";
        String allergyNameQuery = "SELECT name FROM concept_name WHERE concept_name_id = ?";
        List<Object> allergyNameList = new ArrayList<>();

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Set<Object> allergyIds = retrieveMultipleFromDB(allergyIdQuery, patientId);
            Iterator<Object> allergyIdItr = allergyIds.iterator();
            while (allergyIdItr.hasNext()) {
                Object allergyId = allergyIdItr.next();
                if (allergyId != null) {
                    allergyNameList.add(retrieveFromDB(allergyNameQuery, allergyId.toString()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return allergyNameList;
    }


    private Set<Object> retrieveMultipleFromDB(String query, String id) {
        Set<Object> data = new HashSet<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object obj = rs.getObject(1);
                if (obj != null) {
                    data.add(obj);
                }
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
            while (rs.next()) {
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

    public Boolean isMedicationSetupComplete() {
        int drugCount = 0;
        String drugCountQuery = "SELECT COUNT(drug_id) FROM patient_history WHERE patient_id = ?";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = conn.prepareStatement(drugCountQuery);
            ps.setString(1, patientId.toString());
            ResultSet rs = ps.executeQuery();
            rs.next();
            drugCount = rs.getInt(1);
            ps.close();
            rs.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return drugCount > 0;
    }

    public Boolean isAllergySetupComplete() {
        int allergyCount = 0;
        String allergyCountQuery = "SELECT COUNT(allergy_id) FROM patient_history WHERE patient_id = ?";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = conn.prepareStatement(allergyCountQuery);
            ps.setString(1, patientId.toString());
            ResultSet rs = ps.executeQuery();
            rs.next();
            allergyCount = rs.getInt(1);
            ps.close();
            rs.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return allergyCount > 0;
    }

    public void saveMedications(String[] medicationData) {
        if (medicationData == null) {
            return;
        }
        String getMedicationsQuery = "SELECT drug_id FROM patient_history WHERE patient_id = ?";
        String saveMedicationItemQuery = "INSERT INTO patient_history (patient_id, drug_id) VALUES (?,?)";
        String removeDrugIdsQuery = "DELETE FROM patient_history WHERE patient_id = ? AND drug_id = ?";

        saveCheckedItems(medicationData, getMedicationsQuery, saveMedicationItemQuery, removeDrugIdsQuery);
    }

    public void saveAllergies(String[] allergyData) {
        if (allergyData == null) {
            return;
        }
        String getAllergiesQuery = "SELECT allergy_id FROM patient_history WHERE patient_id = ?";
        String saveAllergyItemQuery = "INSERT INTO patient_history (patient_id, allergy_id) VALUES (?,?)";
        String removeAllergyIdQuery = "DELETE FROM patient_history WHERE patient_id = ? AND allergy_id = ?";

        saveCheckedItems(allergyData, getAllergiesQuery, saveAllergyItemQuery, removeAllergyIdQuery);
    }

    public void saveCheckedItems(String[] data, String getIdsQuery, String saveIdQuery, String deleteIdQuery) {

        // convert data to Sets so comparisons can easily be performed
        Set<Object> dataToAddObjects = new HashSet<>(Arrays.asList(data));
        Set<Object> dataCheckedByUserObjects = new HashSet<>(Arrays.asList(data));
        Set<Integer> dataToAdd = new HashSet<>();
        Set<Integer> dataCheckedByUser = new HashSet<>();
        for (Object obj : dataCheckedByUserObjects) {
            dataCheckedByUser.add(Integer.parseInt(obj.toString()));
        }
        for (Object obj : dataToAddObjects) {
            dataToAdd.add(Integer.parseInt(obj.toString()));
        }

        // get patient data from db
        Set<Object> savedDataIdsObjects = new HashSet<>();
        Set<Object> dataIdsToRemoveObjects = new HashSet<>();
        Set<Integer> savedDataIds = new HashSet<>();
        Set<Integer> dataIdsToRemove = new HashSet<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            savedDataIdsObjects = retrieveMultipleFromDB(getIdsQuery, patientId);
            dataIdsToRemoveObjects = retrieveMultipleFromDB(getIdsQuery, patientId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (savedDataIdsObjects.size() > 0) {
            for (Object obj : savedDataIdsObjects) {
                savedDataIds.add(Integer.parseInt(obj.toString()));
            }
            for (Object obj : dataIdsToRemoveObjects) {
                dataIdsToRemove.add(Integer.parseInt(obj.toString()));
            }
        }

        // if checked data contains data that is not in db, add them
        dataToAdd.removeAll(savedDataIds);
        if (dataToAdd.size() > 0) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection(url, username, password);
                PreparedStatement ps = conn.prepareStatement(saveIdQuery);
                Iterator<Integer> medicationsToAddItr = dataToAdd.iterator();
                while (medicationsToAddItr.hasNext()) {
                    String drugId = medicationsToAddItr.next().toString();
                    ps.setString(1, patientId.toString());
                    ps.setString(2, drugId);
                    ps.executeUpdate();
                }
                ps.close();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        // if checked data does not contain data that is in the db, remove them
        dataIdsToRemove.removeAll(dataCheckedByUser);
        if (dataIdsToRemove.size() > 0) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection(url, username, password);
                PreparedStatement ps = conn.prepareStatement(deleteIdQuery);
                Iterator<Integer> drugIdsToRemoveItr = dataIdsToRemove.iterator();
                while (drugIdsToRemoveItr.hasNext()) {
                    String drugId = drugIdsToRemoveItr.next().toString();
                    ps.setString(1, patientId.toString());
                    ps.setString(2, drugId);
                    ps.executeUpdate();
                }
                ps.close();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
