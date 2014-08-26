package formbinder;

import play.data.validation.Constraints.Required;

public class SearchTerm {
	@Required
	public String term;
}
