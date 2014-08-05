package controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import play.mvc.Controller;
import play.libs.WS;
import play.mvc.Result;
import static play.libs.F.Function;
import static play.libs.F.Promise;
import models.*;

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

	public static Promise<List<BookInfo>> getBookInfo(String query) {
	    final Promise<List<BookInfo>> resultPromise = WS.url("http://openlibrary.org/search.json").setQueryParameter("q", query).get().map(
	            new Function<WS.Response, List<BookInfo>>() {
	                public List<BookInfo> apply(WS.Response response) {
	                	JsonNode results = response.asJson().findPath("docs");
	                    ArrayList<BookInfo> res = new ArrayList<>();
	                    Iterator<JsonNode> it = results.elements();
	                    
	                    while (it.hasNext()) {
							JsonNode json = it.next();
							if(json == null) continue;
							// build new BookInfo
							BookInfo toAdd = new BookInfo();
							
							toAdd.title = json.findValue("title").asText();
							toAdd.author_name = json.findValue("author_name").asText();
							res.add(toAdd);
							
						}
	                    
	                	return res;
	                }
	            }
	    );
	    return resultPromise;
	}
	
}
