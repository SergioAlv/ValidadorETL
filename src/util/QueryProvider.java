package util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QueryProvider {

	public static String GetQuery(String query, char origin) throws IOException {
		
		String fullQuery = "";
		
		if (origin == 'Q') {
			fullQuery = query;
		} else if (origin == 'F') {
			final File fileQuery = new File(query);
			fullQuery = FilesReader.readFile(fileQuery);
		}
		
		fullQuery = fullQuery.replaceAll("\\s+", " ");
		
		return fullQuery;
	}
	
	
	public static List<String> GetColumns(String query) {
		
		//TODO En el caso de un script sería coger la ultima query solamente.
		
		int from = -1;
		if (query.indexOf("FROM") != -1) {
			from = query.indexOf("FROM");
		} else if (query.indexOf("from") != -1) {
			from = query.indexOf("from");
		}
		
		query = query.substring(7,from);
		
		boolean finish = false;
		List<String> columns = new ArrayList<String>();
		while (!finish) {
			
			String column = "";
			if (query.indexOf(',') != -1) {
				column = query.substring(0,query.indexOf(',')-1);
				query = query.substring(query.indexOf(',')+1);
			} else {
				column = query;
				finish = true;
			}
			
			if (column.contains(" AS") || column.contains(" as")) {
				
				int as = -1;
				if (column.contains(" AS")) {
					as = column.indexOf(" AS") + 4;
				} else if (column.contains(" as")) {
					as = column.indexOf(" as") + 4;
				}
				
				column = column.substring(as);
			}
			
			column = column.replace(" ", "");
			columns.add(column);
		
		}
		
		return columns;
		
	}
		
}
