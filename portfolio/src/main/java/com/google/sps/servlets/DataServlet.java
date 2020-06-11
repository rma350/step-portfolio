// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.servlets.LoginServlet.LoginStatus;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/data")
public class DataServlet extends HttpServlet {
  
  static class Comment {
    String comment = "";
    String userEmail = "";
    long timestamp = 0L;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    List<Comment> comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      Comment c = new Comment();
      c.comment = (String)entity.getProperty("comment");
      c.userEmail = (String)entity.getProperty("email");
      c.timestamp = (Long)entity.getProperty("timestamp");
      comments.add(c);
    }
    response.setContentType("application/json;");
    Gson gson = new Gson();
    String json = gson.toJson(comments);
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    if(!userService.isUserLoggedIn()) {
      // This shouldn't be reachable unless the user does something crazy.
      response.sendRedirect("/step.html");
      return;
    }
    String userEmail = userService.getCurrentUser().getEmail();
    String comment = request.getParameter("text-input");
    if(comment != null) {
      long timestamp = System.currentTimeMillis();
      Entity taskEntity = new Entity("Comment");
      taskEntity.setProperty("comment", comment);
      taskEntity.setProperty("timestamp", timestamp);
      taskEntity.setProperty("email", userEmail);
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(taskEntity);
    }
    response.sendRedirect("/step.html");
  }
}
