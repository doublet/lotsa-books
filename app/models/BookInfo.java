package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.github.doublet.lotsabooks.Jsonizable;

import play.db.ebean.Model;

@Entity
public class BookInfo extends Model implements Jsonizable {
	@Id
	public Long id;
	
	@OneToOne(cascade = CascadeType.ALL)
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
	@OneToMany(cascade=CascadeType.ALL)
	public List<Isbn> isbns = new ArrayList<>();
	
	public void addIsbn(Isbn isbn) {
		this.isbns.add(isbn);
		isbn.bookInfo = this;
	}

	@Override
	public String toJson() {
		return String.format("{ `id`: `%s`, `title`: `%s`, `isbns`: %s }".replace('`', '"'), this.id, this.title, this.isbnsToString());
	}

	private String isbnsToString() {
		StringBuilder ret = new StringBuilder();
		ret.append("[ ");
		for (int i = 0; i < this.isbns.size(); i++) {
			ret.append(this.isbns.get(i).toJson());
			if(i != this.isbns.size() - 1) ret.append(", ");
		}
		ret.append("]");
		return ret.toString();
	}
}
