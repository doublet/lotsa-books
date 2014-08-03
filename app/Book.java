import javax.persistence.Id;

import play.db.ebean.Model;


public class Book extends Model {
	@Id
	public Long id;
	
	public String title;
	public String isbn;
	
	public int pages;
	public int pagesRead;
}
