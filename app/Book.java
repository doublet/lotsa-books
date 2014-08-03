import play.db.ebean.Model;


public class Book extends Model {
	public String title;
	public String isbn;
	
	public int pages;
	public int pagesRead;
}
