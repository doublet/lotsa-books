package controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;

import play.Logger;
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
							
							if(json.findValue("title") != null)
								toAdd.title = json.findValue("title").asText();
							else
								toAdd.title = "";
							
							if(json.findValue("publish_year") != null)
								toAdd.publishYear = json.findValue("publish_year").asText();
							
							toAdd.openlibraryKey = json.findValue("key").asText();
							
							toAdd.authorName = new ArrayList<>();
							
							if(json.findValue("author_name") != null)
								iteratorToList(json.findValue("author_name").elements(), toAdd.authorName);
							
							if(json.findValue("isbn") != null) {
								Iterator<JsonNode> itIsbn = json.findValue("isbn").elements();
								while(itIsbn.hasNext()) {
									JsonNode node = itIsbn.next();
									Isbn isbn = new Isbn(node.asText());
									if(isbn.isValid()) toAdd.addIsbn(isbn);
								}
							}
							
							if(json.findValue("publisher") != null)
								iteratorToList(json.findValue("publisher").elements(), toAdd.publisher);
							if(json.findValue("language") != null)
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
	                public Result apply(List<BookInfo> list) {
	                	StringBuilder res = new StringBuilder();
	                	res.append("{ \"data\": [ ");
	                	for (int i = 0; i < list.size(); i++) {
							res.append(list.get(i).toJson());
	                		if(i != list.size() - 1) res.append(", ");
						}
	                	res.append(" ] }");
	                	try {
							return ok(new JSONObject(res.toString()).toString(2));
						} catch (JSONException e) {
							return internalServerError(e.getMessage());
						}
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
