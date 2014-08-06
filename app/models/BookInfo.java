package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import play.db.ebean.Model;

@Entity
public class BookInfo extends Model {
	@Id
	public Long id;
	
	@OneToOne
	public Book book;
	
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
