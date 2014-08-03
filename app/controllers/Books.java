package controllers;

import models.Book;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;

public class Books extends Controller {
	
	/**
	 * List all books
	 * @return
	 */
	public static Result list() {
		return ok(list.render(Book.findAll()));
	}
	
	/**
	 * List details for a given book
	 * @param id The book
	 * @return
	 */
	public static Result details(long id) {
		return TODO;
	}
	
	/**
	 * Show form to add a new Book
	 * @return
	 */
	public static Result newBook() {
		return TODO;
	}
	
	/**
	 * Show filled form to edit an existing book
	 * @param id The id of the Book
	 * @return
	 */
	public static Result edit(long id) {
		return TODO;
	}
	
	/**
	 * Save a book by binding a request
	 * @return
	 */
	public static Result save() {
		return TODO;
	}
}
