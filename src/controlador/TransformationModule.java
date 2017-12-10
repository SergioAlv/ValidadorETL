package controlador;

import java.util.List;
import java.util.Map;

public class TransformationModule {
	
	private static String ProcessCondicional(String transformation) {

		// IFF(CONDICION, SI, NO) Y EN EL MODELO COMPROBAR TIPADO.
			// CONDICION = IGUALACION, MAYOR QUE, ETC... CON UN CASE
		// SUM(ALGO) BY ALGO. MIRAR DE METER UN GROUP BY
		// CAMBIO EN EL FORMATO DE FECHAS

		transformation = transformation.replaceAll("\\s+", " ");
		transformation = transformation.toUpperCase();

		//TODO IFFs anidados.
		String condition = transformation.substring(transformation.indexOf('(')+1, transformation.indexOf(","));
		String firstConsecuence = transformation.substring(transformation.indexOf(",")+1);
		firstConsecuence = firstConsecuence.substring(0, firstConsecuence.indexOf(","));
		String secondConsecuence = transformation.substring(transformation.indexOf(",")+1);
		secondConsecuence = secondConsecuence.substring(secondConsecuence.indexOf(",")+1, secondConsecuence.indexOf(')'));
		
		String srcColumn = "CASE WHEN " + condition + " THEN " + firstConsecuence + " ELSE " + secondConsecuence + " END ";
		System.out.println(srcColumn);
		return srcColumn;
		
	}
	
	
	private static String ProcessTransformation(String transformation, String targetName) {
		
		if (transformation.contains("IFF") || transformation.contains("iff")) {
			String condicional = ProcessCondicional(transformation);
			return condicional;
		}
		
		return transformation;
		
	}
	

	public static String FormatQuery(String query, Map<String, String> transformations, List<String> targetColumns, String groupBy) {
		
		String formatedQuery =  "SELECT ";
		
		int nItems = targetColumns.size();
		int processItem = 0;
		for (String column : targetColumns) {
			
			String transformation = transformations.get(column);
			String sourceColumn = TransformationModule.ProcessTransformation(transformation, column);
			
			processItem++;
			if (processItem == nItems) {
				formatedQuery = formatedQuery + sourceColumn;
			} else {
				formatedQuery = formatedQuery + sourceColumn + ", ";
			}
			
		}
				
		formatedQuery = formatedQuery + query.substring(query.indexOf("FROM")-1, query.indexOf(';'));
		formatedQuery = formatedQuery + groupBy;
		return formatedQuery;
		
	}
	
}
