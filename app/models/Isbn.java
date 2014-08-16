package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.apache.commons.validator.routines.ISBNValidator;

import play.data.validation.ValidationError;
import play.db.ebean.Model;

@Entity
public class Isbn extends Model {
	@Id
	public Long id;
	
	@ManyToOne(cascade=CascadeType.ALL)
	public Book book;
	
	public String isbn;

    public Isbn() {
    	// empty
    }
    public Isbn(String isbn) {
		this.isbn = isbn;
	}

	public boolean isValid() {
		return this.isValid(this.isbn);
	}
	
	public static boolean isValid(String input) {
        ISBNValidator validator = new ISBNValidator();
        return validator.isValid(input);
    }
    
    public List<ValidationError> validate() {
    	List<ValidationError> errors = new ArrayList<ValidationError>();
    	if(!Isbn.isValid(this.isbn)) errors.add(new ValidationError("isbn", "Malformed ISBN"));
    	return errors.isEmpty() ? null : errors;
    }
}
