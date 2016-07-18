package util;

public class GenerateJoin {

	public static String generateConditionJoin(String query_destino,
			String jdbcConexion, String usrConexion, String passConexion) {

		// CREA LAS CONDICIONES DEL JOIN A PARTIR DE LAS QUERYS, QUE TENDRAN
		// CAMPOS QUE EMPEZARAN POR EL PREFIJO "COND"
		// INDICANDO ASI QUE ESOS SERAN LOS ATRIBUTOS DE COMPARACION, Y POR
		// TANTO LOS QUE SE INCLUIRAN EN EL JOIN.

		String conditionJoin = "";
		boolean first = true;
		while (query_destino.contains("COND_")) {
			int begin = query_destino.indexOf("COND_");
			String aux = query_destino.substring(begin);
			int end = aux.indexOf(' ');
			int end2 = aux.indexOf(',');

			String column_name = "";
			if (end2 == -1) {
				column_name = aux.substring(0, end);
			} else if (end2 > end) {
				column_name = aux.substring(0, end);
			} else if (end > end2) {
				column_name = aux.substring(0, end2);
			}

			if (first) {
				conditionJoin = "a." + column_name + " = b." + column_name;
				first = false;
			} else {
				conditionJoin = conditionJoin + " AND a." + column_name
						+ " = b." + column_name;
			}

			query_destino = query_destino.substring(begin + 5);
		}

		return conditionJoin;
	}
}
