import org.junit.*;

import play.mvc.*;
import play.test.*;
import play.libs.F.*;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

import models.Isbn;


public class IsbnTest {

	@Test
	public void regexTest(){
		assertThat(Isbn.validate("1-4493-4468-2")).isTrue();
		assertThat(Isbn.validate("978-1-4493-4468-9 ")).isTrue();

		assertThat(Isbn.validate("978-4493-4468-9 ")).isFalse();
		assertThat(Isbn.validate("978-1--4493-4468-9 ")).isFalse();
	}
}
