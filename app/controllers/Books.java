package controllers;

import java.util.List;
import java.util.Map;

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
import java.util.HashMap;

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
		else {
			Book staleBook = Book.findById(boundBook.id);
			if(staleBook == null)
				boundBook.save();
			else {
				staleBook.info.title = boundBook.info.title;
				staleBook.pages = boundBook.pages;
				staleBook.pagesRead = boundBook.pagesRead;
				
				staleBook.update();
			}
		}
		
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

	public static Promise<Result> saveFoundSelection () {
		Form<formbinder.SelectBookData> selectForm = Form.form(formbinder.SelectBookData.class).bindFromRequest();
		
		if(selectForm.hasErrors()) {
			Result bad = badRequest("You submitted an incorrect form. Please go back and try again.");
			return Promise.pure(bad);
		}
		
		// check if this OLID doesn't refer to a Work
		// if it does, redirect to a page to select an edition of this work
		if(selectForm.get().isWork()) {
			flash("notice", "This work has multiple editions. Please select one to add.");
			return selectForm.get().getAllEditions().map(allEditions -> ok(views.html.selectBook.render(allEditions)));
		}
		
		// turn the OpenLibrary key we bound into a book
		Promise<Book> book = OpenLibrary.getBookByOLKey(selectForm.get().openlibraryKey);
		
		final String olid = selectForm.get().openlibraryKey;
		Logger.debug("About to search for book with key " + olid);
		
		// persist the book and show its details
		final Promise<Result> promise = book.map(
				new Function<Book, Result>() {
					public Result apply(Book book) {
						if(book == null)
							return internalServerError(String.format("There was an error fetching the book from OpenLibrary (key: %s).", olid));
						book.save();
						flash("success", "Book was succesfully imported.");
						return redirect(routes.Books.details(book.id));
					}
				}
		);
		return promise;
	}
	
	public static Promise<Result> newBookFromSearch() {
		Result res = ok(views.html.searchForm.render(searchTermForm));
		return Promise.pure(res);
	}
	
	public static Promise<Result> saveSearchForm() {
		Form<SearchTerm> boundForm = searchTermForm.bindFromRequest();
		
		if(boundForm.hasErrors()) {
			flash("error", "There was an error in your submission. Please try again.");
			Result bad = badRequest(views.html.searchForm.render(boundForm));
			return Promise.pure(bad);
		}
		
		SearchTerm search = boundForm.get();
		
		// search OpenLibrary for this term
		Promise<List<BookInfo>> list = OpenLibrary.searchBooks(search.term);
		
		// transform Promise<List<BookInfo>> -> Promise<Result>
		return list.map(theList -> ok(views.html.selectBook.render(theList)) );
	}
}
