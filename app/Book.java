import java.util.List;

import javax.persistence.Id;

import play.db.ebean.Model;


public class Book extends Model {
	public Finder<Long, Book> find = new Finder<>(Long.class, Book.class);
	
	@Id
	public Long id;
	
	public String title;
	public String isbn;
	
	public int pages;
	public int pagesRead;
	
	public Book findById(Long id) {
		return find.byId(id);
	}
	
	public List<Book> findAll() {
		return find.all();
	}
}
