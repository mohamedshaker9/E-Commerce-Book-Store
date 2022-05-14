package com.bookstore.controller.frontend.customer;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bookstore.service.CustomerServices;


@WebServlet("/update_profile")
@MultipartConfig
public class UpdateProfileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
 

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		CustomerServices customerServices = new CustomerServices(request, response);
		System.out.println("updating customer profile");
		customerServices.updateCustomerProfile();
	}

}
