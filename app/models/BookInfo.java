package models;

import java.util.List;

import javax.persistence.Id;

import play.db.ebean.Model;

public class BookInfo extends Model {
	@Id
	public Long id;
	
	// main information
	public String title;
	public String authorName;
	public String language;
	
	// publish information
	public String publishPlace;
	public String firstPublishYear;
	public String publishYear;
	public String publisher;
	
	// identification
	public String openlibraryKey;
	public List<String> isbn;
}
