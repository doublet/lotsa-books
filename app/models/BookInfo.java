package models;

import java.util.List;

import play.db.ebean.Model;

public class BookInfo extends Model {
	// main information
	public String title;
	public String author_name;
	public String language;
	
	// publish information
	public String publish_place;
	public String first_publish_year;
	public String publish_year;
	public String publisher;
	
	// identification
	public String openlibrary_key;
	public List<String> isbn;
}
