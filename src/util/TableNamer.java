package util;

public class TableNamer {

	public static String GetTableName(String query_destino) {
		
		int init = 0;
		if (query_destino.contains("FROM")) {
			init = query_destino.indexOf("FROM") + 5;
		} else if (query_destino.contains("from")) {
			init = query_destino.indexOf("from") + 5;
		}
		String from = query_destino.substring(init);
		String table_name;
		int begin_name = from.indexOf('.') + 1;
		if ((from.contains("JOIN")) || (from.contains("join"))) {
			table_name = from.substring(begin_name, from.indexOf(' '));
		} else {
			from = from + " ";
			table_name = from.substring(begin_name, from.indexOf(' '));
		}
		
		return table_name;
	}
	
}
