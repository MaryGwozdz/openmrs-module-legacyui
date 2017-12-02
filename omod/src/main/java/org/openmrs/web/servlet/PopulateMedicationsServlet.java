/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.web.servlet;

import org.openmrs.web.HistoryForm;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gwozd on 11/29/2017.
 */

@WebServlet("/PopulateMedicationsServlet")
public class PopulateMedicationsServlet extends HttpServlet {

    public PopulateMedicationsServlet() {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String patientId = request.getParameter("patientId");
        HistoryForm historyForm = new HistoryForm(patientId);

        List<Object> medicationList = new ArrayList<>();
        if (historyForm.isMedicationSetupComplete()) {
            medicationList = historyForm.generateMedicationTable();
        }

        String json = new Gson().toJson(medicationList);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] medicationData = request.getParameterValues("medicationData");
        String patientId = request.getParameter("patientId");
        HistoryForm historyForm = new HistoryForm(patientId);
        historyForm.saveMedications(medicationData);
    }

}
