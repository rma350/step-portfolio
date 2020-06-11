package com.google.sps.servlets;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
  
  public static class LoginStatus {
    public boolean isLoggedIn = false;
    public String userEmail = "";
    public String changeLogInUrl = "";
  }
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.setContentType("text/html");

    UserService userService = UserServiceFactory.getUserService();
    LoginStatus result = new LoginStatus();
    result.isLoggedIn = userService.isUserLoggedIn();
    String urlToRedirectToAfterLoginChange = "/step.html";
    if(result.isLoggedIn) {
      result.userEmail = userService.getCurrentUser().getEmail();      
      result.changeLogInUrl = userService.createLogoutURL(urlToRedirectToAfterLoginChange);
    } else {
      result.changeLogInUrl = userService.createLoginURL(urlToRedirectToAfterLoginChange);
    }
    response.setContentType("application/json;");
    response.getWriter().println(new Gson().toJson(result));
  }
}
