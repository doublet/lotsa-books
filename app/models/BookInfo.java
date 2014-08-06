package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;

import play.db.ebean.Model;

public class BookInfo extends Model {
	@Id
	public Long id;
	
	// main information
	public String title;
	public List<String> authorName = new ArrayList<>();
	public List<String> language = new ArrayList<>();
	
	// publish information
	public String publishYear;
	public List<String> publisher = new ArrayList<>();
	
	// identification
	public String openlibraryKey;
	public List<String> isbn = new ArrayList<>();
}
