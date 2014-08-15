package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import play.db.ebean.Model;

@Entity
public class Book extends Model {
	public static final Finder<Long, Book> find = new Finder<>(Long.class, Book.class);
	
	@Id
	public Long id;
	
	@OneToOne
	public BookInfo info = new BookInfo();
	
	public int pages;
	public int pagesRead;
	
	public static Book findById(Long id) {
		return find.byId(id);
	}
	
	public static List<Book> findAll() {
		return find.all();
	}
	
	public void save() {
		// save OneToOne relation
		this.info.save();
		super.save();
	}
	
	/**
	 * Calculates how much of the book has been read
	 * @return The percentage of the book read
	 */
	public int getProgress() {
		if(pages == 0 || pagesRead == 0) return 0;
		return (pagesRead * 100 / pages);
	}
}
