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
import org.openmrs.web.SummaryTable;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

/**
 * Created by gwozd on 11/29/2017.
 */

@WebServlet("/PopulateSummaryServlet")
public class PopulateSummaryServlet extends HttpServlet {

    public PopulateSummaryServlet() {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String patientId = request.getParameter("patientId");
        Integer userId = Integer.parseInt(request.getParameter("userId"));
        SummaryTable summaryTable = new SummaryTable(patientId, userId);
        List<String> sumItems = summaryTable.retrieveUserSummaryItems();
        Boolean setupComplete = sumItems.size() > 0;
        Map<String, List<Object>> sumDataMap = new HashMap<>();
        String json = null;
        if (setupComplete) {
            sumDataMap = summaryTable.generateSummaryTable(sumItems);
            json = new Gson().toJson(sumDataMap);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json);
        }

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] sumData = request.getParameterValues("sumData");
        String patientId = request.getParameter("patientId");
        Integer userId = Integer.parseInt(request.getParameter("userId"));
        SummaryTable summaryTable = new SummaryTable(patientId, userId);
        summaryTable.saveSetup(sumData);
    }

}
