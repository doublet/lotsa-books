package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class Book extends Model {
	public static final Finder<Long, Book> find = new Finder<>(Long.class, Book.class);
	
	@Id
	public Long id;
	
	public String title;
	public String isbn;
	
	public int pages;
	public int pagesRead;
	
	public static Book findById(Long id) {
		return find.byId(id);
	}
	
	public static List<Book> findAll() {
		return find.all();
	}
	
	/**
	 * Calculates how much of the book has been read
	 * @return The percentage of the book read
	 */
	public int getProgress() {
		return (pagesRead * 100 / pages);
	}
}
