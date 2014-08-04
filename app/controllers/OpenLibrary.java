package controllers;

import play.mvc.Controller;
import play.libs.WS;
import play.mvc.Result;

import static play.libs.F.Function;
import static play.libs.F.Promise;

public class OpenLibrary extends Controller {
	
	public static Promise<Result> matchingItems(String query) {
	    final Promise<Result> resultPromise = WS.url("http://openlibrary.org/search.json").setQueryParameter("q", query).get().map(
	            new Function<WS.Response, Result>() {
	                public Result apply(WS.Response response) {
	                    return ok("Matching items: " + response.asJson().findPath("numFound"));
	                }
	            }
	    );
	    return resultPromise;
	}
}
