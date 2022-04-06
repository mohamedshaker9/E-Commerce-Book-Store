package com.bookstore.service;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.bookstore.dao.BookDao;
import com.bookstore.dao.CategoryDao;
import com.bookstore.entity.Book;
import com.bookstore.entity.Category;

public class BookServices {
	private EntityManager entityManager;
	private BookDao bookDao;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private CategoryDao categoryDao;
	public BookServices(EntityManager entityManager, 
			HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		this.entityManager = entityManager;

		
		bookDao = new BookDao(entityManager);
		categoryDao = new CategoryDao(entityManager);
	}
	
	public List<Book> listBooks() {
		return bookDao.listAll();
		
	}
	
	public void showBookTable() throws ServletException, IOException {
		this.request.setAttribute("books", this.listBooks());
		
		String listPage = "book_list.jsp";
		
		this.redirectTo(listPage);
	}
	
	public void showBookForm() throws ServletException, IOException {
		System.out.println("show book form function");
		List<Category> categories = categoryDao.listAll();
		this.request.setAttribute("categories", categories);
		
		System.out.println(categories.get(0).toString());
		
		System.out.println("redirecting to book form");
		
		String bookFormPage = "../admin/book_form.jsp";
		redirectTo(bookFormPage);
	}
	
	public void createBook() throws IOException, ServletException {
		
		Book newBook = new Book();
		this.readRequestFields(newBook);
		
		bookDao.create(newBook);
		this.request.setAttribute("message", "book added successfully");
		showBookTable();
	}
	
	public void showEditBookForm() throws ServletException, IOException {
		int bookId = Integer.parseInt(this.request.getParameter("bookId"));
		Book book = bookDao.get(bookId);
		
		this.request.setAttribute("book", book);
		List<Category> catgories = categoryDao.listAll();
		this.request.setAttribute("categories", catgories);
		System.out.println("------------------reirecting to book_form");
		
		redirectTo("book_form.jsp");
	}
	
	
	public void updateBook() throws IOException, ServletException {
		int bookId = Integer.parseInt(this.request.getParameter("bookId"));
		String title = this.request.getParameter("bookTitle");
		String isbn = this.request.getParameter("bookIsbn");
		
		Book existingBook = bookDao.get(bookId);
		Book bookByIsbn = bookDao.findByIsbn(isbn);
		
		if(existingBook == null) {
			System.out.println("couldn't update cook with this isbn");
			this.request.setAttribute("message", "book with this id not exist");
			showBookTable();
		}
		else if(bookByIsbn != null && bookByIsbn.getBookId() != existingBook.getBookId() && bookByIsbn.getIsbn().equals(isbn)) {
			System.out.println("couldn't update cook with this isbn");
			redirectToWithMessage("edit_book?bookId=" + bookId, "there is a book with same isbn try different one");
		}else {
			this.readRequestFields(existingBook);		
			bookDao.update(existingBook);
			redirectToWithMessage("list_book", "Book Updated Successfully");
		}
		
		
	}
	
	private void readRequestFields(Book book) throws IOException, ServletException {
		String title = this.request.getParameter("bookTitle");
		int categoryId = Integer.parseInt(this.request.getParameter("category"));
		String author = this.request.getParameter("bookAuthor");
		String description = this.request.getParameter("description");
		String isbn = this.request.getParameter("bookIsbn");
		float price =Float.parseFloat(this.request.getParameter("bookPrice"));
		
		Category category = categoryDao.get(categoryId);
		
		book.setTitle(title);
		book.setCategory(category);
		book.setAuthor(author);
		book.setDescription(description);
		book.setIsbn(isbn);
		book.setPrice(price);
		getPublishDateFromRequest(book);
		getImageBytesFromRequest(book);
		
		
		System.out.println("title: " + title);
		System.out.println("categoryId: " + categoryId);
		System.out.println("author: " + author);
		System.out.println("description: " + description);
		System.out.println("isbn: " + isbn);
		System.out.println("price: " + price);
		
	
	}
	
	private void getPublishDateFromRequest(Book book) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date publishDate = null;
		try {
			String dateString = this.request.getParameter("bookDate");
			System.out.println("string date: ---->  " + dateString);
			publishDate = dateFormat.parse(dateString);
			book.setPublishDate(publishDate);
			System.out.println("publish Date: " + publishDate);
		}catch(ParseException px) {
			System.out.println("Error parsing the Date");
		}
	}
	
	public void deleteBook() throws ServletException, IOException {
		int bookId = Integer.parseInt(this.request.getParameter("bookId"));
		Book book = bookDao.get(bookId);
		if(book != null) {
			bookDao.delete(bookId);
		} else {
			this.request.setAttribute("message", "there is no book whit this id");
			showBookTable();
		}
		
		
		this.request.setAttribute("message", "Book Deleted Successfully");
		showBookTable();
	}
	
	private void getImageBytesFromRequest(Book book) throws IOException, ServletException {
		Part part = this.request.getPart("bookImage");	
		System.out.println("Checking part #########");
		if(part != null && part.getSize() > 0) {
			System.out.println("###part exist #########");
			int partSize = (int) part.getSize();
			byte[] imageBytes = new byte[partSize];
			System.out.println("the typ of file is ---------> " + part.getContentType());
			InputStream inputStream = part.getInputStream();
			inputStream.read(imageBytes);
			inputStream.close();
			book.setImage(imageBytes);
		}
	}
	
	
	private void redirectTo(String page) throws ServletException, IOException {
		RequestDispatcher requestDispatcher = request.getRequestDispatcher(page);
		requestDispatcher.forward(this.request, this.response);
	}
	
	private void redirectToWithMessage(String page, String message) throws ServletException, IOException {
		this.request.setAttribute("message", message);
		RequestDispatcher requestDispatcher = request.getRequestDispatcher(page);
		requestDispatcher.forward(this.request, this.response);
	}

	public void listBooksByCategory() throws ServletException, IOException {
		int catId = Integer.parseInt(this.request.getParameter("categoryId"));
		List<Book> books = bookDao.findByCategory(catId);
		System.out.println(books.get(0).getTitle());
		List<Category> categories = categoryDao.listAll();
		System.out.println(categories.get(0).getName());
		Category category = categoryDao.get(catId);
		System.out.println(category.getName());
		this.request.setAttribute("books", books);
		this.request.setAttribute("categories", categories);
		this.request.setAttribute("category", category);
		System.out.println("Redirecting to book viw category");
		redirectTo("frontend/book_view_by_category.jsp");

	}
}
