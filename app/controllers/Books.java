package controllers;

import java.util.List;

import models.*;
import play.Logger;
import play.data.Form;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Function0;
import views.html.*;
import formbinder.*;

public class Books extends Controller {
	private static final Form<Book> bookForm = new Form<>(Book.class);
	private static final Form<Isbn> isbnForm = new Form<>(Isbn.class);
	private static final Form<SearchTerm> searchTermForm = new Form<>(SearchTerm.class);
	
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
		Book showBook = Book.findById(id);
		if(showBook == null)
			return notFound("Not Found");
		return ok(details.render(showBook));
	}
	
	/**
	 * Show form to add a new Book
	 * @return
	 */
	public static Result newBook() {
		return ok(views.html.bookForm.render(bookForm));
	}
	
	
	/**
	 * Show form to add a new Book, by only entering its ISBN
	 * @return
	 */
	public static Result newBookFromIsbn() {
		return ok(views.html.isbnForm.render(isbnForm));
	}
	
	/**
	 * Show filled form to edit an existing book
	 * @param id The id of the Book
	 * @return
	 */
	public static Result edit(long id) {
		Book editBook = Book.findById(id);
		return ok(views.html.bookForm.render(bookForm.fill(editBook)));
	}
	
	/**
	 * Save a book by binding a request
	 * @return
	 */
	public static Result save() {
		Form<Book> boundForm = bookForm.bindFromRequest();
		
		if(boundForm.hasErrors()) {
			flash("error", "There was an error in your submission. Please try again.");
			return badRequest(views.html.bookForm.render(boundForm));
		}
		
		Book boundBook = boundForm.get();
		if(boundBook.id == null)
			boundBook.save();
		else
			boundBook.update();
		
		return redirect(routes.Books.details(boundBook.id));
	}
	
	/**
	 * Save a book by binding an ISBN number given in the request and searching for it
	 * @return
	 */
	public static Promise<Result> saveFromIsbn() {
		Form<Isbn> boundForm = isbnForm.bindFromRequest();
		
		if(boundForm.hasErrors()) {
			flash("error", "There was an error in your submission. Please try again.");
			Result badReq = badRequest(views.html.isbnForm.render(boundForm));
			return Promise.pure(badReq);
		}
		
		Isbn boundIsbn = boundForm.get();
		
		// search OpenLibrary for this ISBN
		Promise<List<BookInfo>> list = OpenLibrary.searchBooks(boundIsbn.isbn);
		
		// transform Promise<List<BookInfo>> -> Promise<Result>
		return list.map(
				new Function<List<BookInfo>, Result>() {
					public Result apply(List<BookInfo> list) {
						return ok(views.html.selectBook.render(list));
					}
				}
		);
	}

	public static Promise<Result> saveIsbnSelection () {
		Form<formbinder.SelectBookData> selectForm = Form.form(formbinder.SelectBookData.class).bindFromRequest();
		
		if(selectForm.hasErrors()) {
			Result bad = badRequest("You submitted an incorrect form. Please go back and try again.");
			return Promise.pure(bad);
		}
		
		// turn the OpenLibrary key we bound into a book
		Promise<Book> book = OpenLibrary.getBookByOLKey(selectForm.get().openlibraryKey);
		
		// persist the book and show its details
		final Promise<Result> promise = book.map(
				new Function<Book, Result>() {
					public Result apply(Book book) {
						book.save();
						flash("success", "Book was succesfully imported.");
						return redirect(routes.Books.details(book.id));
					}
				}
		);
		return promise;
	}
	
	public static Result showSearchForm() {
		return ok(views.html.searchForm.render(searchTermForm));
	}
	
	public static Result saveSearchForm() {
		Form<SearchTerm> boundForm = searchTermForm.bindFromRequest();
		
		if(boundForm.hasErrors()) {
			return badRequest();
		}
		
		SearchTerm search = boundForm.get();
		
		return ok(search.term);
	}
}
