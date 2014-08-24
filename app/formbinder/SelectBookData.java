package formbinder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import controllers.OpenLibrary;
import models.*;
import play.Logger;
import play.libs.ws.*;
import play.libs.F.Function;
import play.libs.F.Promise;

public class SelectBookData {
	public String openlibraryKey;
	
	/**
	 * Checks if this OLID refers to a Work (a collection of different editions of a book)
	 * @return true if this is a Work, false otherwise
	 */
	public boolean isWork() {
		return openlibraryKey.endsWith("W");
	}
	
	/**
	 * Transparantly retrieve a list of info of all the editions for this Work/Book, whether it's a Book (only one possible) or a Work (possibly more than one)
	 * @return A list of info
	 */
	public Promise<List<BookInfo>> getAllEditions() {
		
		if(!isWork()) {
			// return a list with a single element - this book
			return OpenLibrary.getBookByOLKey(openlibraryKey).map(book -> {
				List<BookInfo> res = new ArrayList<>();
				res.add(book.info);
				return res;
			});
		}
		
		// get a list of OLIDs

		Promise<List<String>> olids = getEditionKeys(openlibraryKey);
	    
        // now get the info for each of these
		
		Promise<String> qs = olids.map((List<String> list) -> olidQueryString(list));
		
		Promise<List<BookInfo>> res = qs.flatMap(bibkeys -> {
			Logger.debug("about to hit http://openlibrary.org/api/books?bibkeys="+bibkeys+"&format=json&jscmd=data");
			Promise<List<BookInfo>> list = WS.url("http://openlibrary.org/api/books")
					.setQueryParameter("bibkeys", bibkeys)
					.setQueryParameter("format", "json")
					.setQueryParameter("jscmd", "data")
					.get().map( response -> {
				Iterator<JsonNode> it = response.asJson().elements();
				if(it == null) return null;
				List<BookInfo> infoList = new ArrayList<>();
				while (it.hasNext()) {
					JsonNode node = it.next();
					// node contains the following fields of interest:
					// pulishers, authors, publish_places, title, number_of_pages, publish_date

					BookInfo toAdd = new BookInfo();
					
					toAdd.title = node.findValue("title").asText();
					toAdd.publishYear = node.findValue("publish_date").asText();
					
					String[] key = node.findValue("key").asText().split("/");
					toAdd.openlibraryKey = key[key.length - 1];
					
					Iterator<JsonNode> authorsIt = node.findValue("authors").elements();
					while (authorsIt.hasNext()) {
						JsonNode author = authorsIt.next();
						toAdd.authorName.add(author.asText());
					}
					infoList.add(toAdd);
				}
				return infoList;
			});
			return list;
		});
		
		return res;
	}
	
	private String olidQueryString(List<String> olids) {
        // we want a string like "OLID:...M,OLID:...M,OLID:...M"
        StringBuilder bibkeys = new StringBuilder();
        for(String olid : olids) {
        	bibkeys.append("OLID:").append(olid).append(",");
        }
        if(olids.size() > 0)
        	bibkeys.deleteCharAt(bibkeys.lastIndexOf(",")); // delete last (trailing) comma
        
        return bibkeys.toString();
	}
	
	private Promise<List<String>> getEditionKeys(String workKey) {
	    final Promise<List<String>> resultPromise = WS.url("http://openlibrary.org/search.json").setQueryParameter("q", workKey).get().map(
	            new Function<WSResponse, List<String>>() {
	                public List<String> apply(WSResponse response) {
	                	Logger.debug("hit http://openlibrary.org/search.json?q="+workKey);
	                	List<BookInfo> res = new ArrayList<>();
	                	
	                	// first get a list of all OLIDs for this Work
	                	List<String> olids = new ArrayList<>();
	                	JsonNode results = response.asJson().findPath("docs");
	                    Iterator<JsonNode> it = results.elements();
	                    
	                    while (it.hasNext()) {
							JsonNode json = it.next();
							if(json == null) continue;
							
							if(json.findValue("edition_key") != null) {
								Iterator<JsonNode> edKeys = json.findValue("edition_key").elements();
								// add all edition keys to the list
								while(edKeys.hasNext())
									olids.add(edKeys.next().asText());
								
							}
							
						}
	                    Logger.debug("OLID string: " + olidQueryString(olids));
	                    return olids;
	                }
	            }
	    );
	    
	    return resultPromise;
	}
	
}
