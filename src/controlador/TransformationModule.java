package controlador;

import java.util.List;
import java.util.Map;

public class TransformationModule {

	private static String ProcessTransformation(String transformation) {

		// IFF(CONDICION, SI, NO) Y EN EL MODELO COMPROBAR TIPADO.
			// CONDICION = IGUALACION, MAYOR QUE, ETC... CON UN CASE
		// SUM(ALGO) BY ALGO. MIRAR DE METER UN GROUP BY
		// CAMBIO EN EL FORMATO DE FECHAS

		return "sourceColumn";

	}

	public static String FormatQuery(String query, Map<String, String> transformations, List<String> targetColumns) {
		
		String formatedQuery =  "SELECT ";
		
		for (String column : targetColumns) {
			
			String transformation = transformations.get(column);
			
			String sourceColumn = TransformationModule.ProcessTransformation(transformation);
			formatedQuery = formatedQuery + sourceColumn;
			
		}
				
		formatedQuery = formatedQuery + query.substring(query.indexOf("FROM")-1);
		
		return formatedQuery;
		
	}
	
}
