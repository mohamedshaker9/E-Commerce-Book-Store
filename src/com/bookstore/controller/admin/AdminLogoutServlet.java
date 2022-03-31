package com.bookstore.controller.admin;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bookstore.service.UsersServices;


@WebServlet("/admin/logout")
public class AdminLogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
  
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession httpSession = request.getSession();
		httpSession.removeAttribute("userLogedIn");
		UsersServices userServices = new UsersServices(request, response);
		userServices.showLoginForm();
	}

}