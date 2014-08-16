package controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import play.mvc.Controller;
import play.libs.WS;
import play.libs.Json;
import play.mvc.Result;
import static play.libs.F.Function;
import static play.libs.F.Promise;
import models.*;

public class OpenLibrary extends Controller {
	
	public static Promise<Result> numMatchingItems(String query) {
	    final Promise<Result> resultPromise = WS.url("http://openlibrary.org/search.json").setQueryParameter("q", query).get().map(
	            new Function<WS.Response, Result>() {
	                public Result apply(WS.Response response) {
	                    return ok("Matching items: " + response.asJson().findPath("numFound"));
	                }
	            }
	    );
	    return resultPromise;
	}

	public static Promise<List<BookInfo>> searchBooks(String query) {
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
							toAdd.publishYear = json.findValue("publish_year").asText();
							toAdd.openlibraryKey = json.findValue("key").asText();
							
							toAdd.authorName = new ArrayList<>();
							
							iteratorToList(json.findValue("author_name").elements(), toAdd.authorName);
							iteratorToList(json.findValue("isbn").elements(), toAdd.isbn);
							iteratorToList(json.findValue("publisher").elements(), toAdd.publisher);
							iteratorToList(json.findValue("language").elements(), toAdd.language);
							
							res.add(toAdd);
							
						}
	                    
	                	return res;
	                }
	            }
	    );
	    return resultPromise;
	}
	
	/**
	 * Adds all elements of an Iterator<JsonNode> to a List of Strings
	 */
	public static void iteratorToList(Iterator<JsonNode> it, List<String> list) {
		while (it.hasNext()) {
			JsonNode node = it.next();
			if(node.asText() == null) continue;
			list.add(node.asText());
		}
	}
	
	public static Promise<Result> getBookInfoJson(String query) {
	    final Promise<Result> promise = searchBooks(query).map(
	            new Function<List<BookInfo>, Result>() {
	                public Result apply(List<BookInfo> lijst) {
	                    return ok(Json.toJson(lijst));
	                }
	            }
	    );
	    return promise;
	}
	
	/**
	 * Get a book, given its OpenLibrary key
	 * @return
	 */
	public static Promise<Book> getBookByOLKey(String key) {
		final Promise<Book> promise = searchBooks(key).map(
			new Function<List<BookInfo>, Book>() {
				public Book apply(List<BookInfo> list) {
					BookInfo info = list.get(0);
					Book book = new Book();
					
					book.setInfo(info);
					
					return book;
				}
			}
		);
		return promise;
	}
	
}
