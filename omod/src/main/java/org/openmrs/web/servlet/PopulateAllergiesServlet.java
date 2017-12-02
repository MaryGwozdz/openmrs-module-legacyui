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

import com.google.gson.Gson;
import org.openmrs.web.HistoryForm;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gwozd on 11/29/2017.
 */

@WebServlet("/PopulateAllergiesServlet")
public class PopulateAllergiesServlet extends HttpServlet {

    public PopulateAllergiesServlet() {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String patientId = request.getParameter("patientId");
        HistoryForm historyForm = new HistoryForm(patientId);

        List<Object> allergiesList = new ArrayList<>();
        if (historyForm.isAllergySetupComplete()) {
            allergiesList = historyForm.generateAllergyTable();
        }

        String json = new Gson().toJson(allergiesList);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] allergyData = request.getParameterValues("allergyData");
        String patientId = request.getParameter("patientId");
        HistoryForm historyForm = new HistoryForm(patientId);
        historyForm.saveAllergies(allergyData);
    }

}
