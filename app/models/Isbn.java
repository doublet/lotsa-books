package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.validator.routines.ISBNValidator;

import play.db.ebean.Model;

@Entity
public class Isbn extends Model {
	@Id
	public Long id;
	
	public String isbn;
	
    public static boolean validate(String input) {
        ISBNValidator validator = new ISBNValidator();
        return validator.isValid(input);
    }
}
