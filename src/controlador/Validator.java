package controlador;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.*;

import modelo.Results;
import modelo.DataProcessor;
import modelo.DataGenerator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;

import util.*;

public class Validator {

	private static String bdUserSource;
	private static String bdUserTarget;
	private static String bdControllerSource;
	private static String bdControllerTarget;
	private static String bdPassSource;
	private static String bdPassTarget;
	private static String resultado;

	private static void setControllers(String bdSource, String bdTarget) {

		if (bdSource.contains("DB22-DES")) {
			bdControllerSource = "jdbc:db2://servidor:puerto/BBDD";
		} else if (bdSource.contains("DB22-PRE")) {
			bdControllerSource = "jdbc:db2://servidor:puerto/BBDD";
		}
		//ETC...

		if (bdTarget.contains("DB22-DES")) {
			bdControllerSource = "jdbc:db2://servidor:puerto/BBDD";
		} else if (bdTarget.contains("DB22-PRE")) {
			bdControllerSource = "jdbc:db2://servidor:puerto/BBDD";
		}
		//ETC..

	}

	public static String validate(String query_destino, String query_origen,
			String conditionJoin, StyledText log) throws SQLException,
			ClassNotFoundException {

		long time_start, time_end;

		if (query_destino.contains(";")) {
			query_destino = query_destino.substring(0,
					query_destino.indexOf(';'));
		}

		String table_name = TableNamer.GetTableName(query_destino);

		String nombreTablaSource = "origen_" + table_name;
		String nombreTablaTarget = "destino_" + table_name;
		String nombreTablaValidacion = "validacion_" + table_name;

		if (nombreTablaSource.length() > 26)
			nombreTablaSource = nombreTablaSource.substring(0, 26);

		if (nombreTablaTarget.length() > 26)
			nombreTablaTarget = nombreTablaTarget.substring(0, 26);

		if (nombreTablaValidacion.length() > 24)
			nombreTablaValidacion = nombreTablaValidacion.substring(0, 24);

		time_start = System.currentTimeMillis();
		log.append("INFO: Cargando tabla origen... \n");
		int cant = DataGenerator.generate(nombreTablaSource, bdControllerSource,
				bdUserSource, bdPassSource, query_origen, log);
		time_end = System.currentTimeMillis();
		log.append("INFO: Ejecutado en " + (time_end - time_start) / 1000
				+ " segundos. " + cant + " Registros Insertados \n");

		time_start = System.currentTimeMillis();
		log.append("INFO: Cargando tabla destino... \n");
		cant = DataGenerator.generate(nombreTablaTarget, bdControllerTarget,
				bdUserTarget, bdPassTarget, query_destino, log);
		time_end = System.currentTimeMillis();
		log.append("INFO: Ejecutado en " + (time_end - time_start) / 1000
				+ " segundos. " + cant + " Registros Insertados \n");

		log.append("INFO: Generating Join Condition... \n");
		conditionJoin = GenerateJoin.generateConditionJoin(query_destino,
				bdControllerTarget, bdUserTarget, bdPassTarget);
		log.append("INFO: Join Condition --> " + conditionJoin + "\n");

		time_start = System.currentTimeMillis();
		log.append("INFO: Cargando tablas de comparacion... \n");
		resultado = nombreTablaValidacion + "_2";
		DataProcessor.processInfo(conditionJoin, nombreTablaSource,
				nombreTablaTarget, nombreTablaValidacion,
				"jdbc:oracle:thin:@//servidor:puerto/BBDD", "usuario",
				"contraseña", log);
		time_end = System.currentTimeMillis();
		log.append("INFO: Ejecutado en " + (time_end - time_start) / 1000
				+ " segundos \n");
		
		return nombreTablaValidacion;
	}

	public static void validateFolder(String folderSource, String folderTarget,
			String bdSource, String bdTarget, String userSource,
			String userTarget, String passSource, String passTarget,
			StyledText log) throws SQLException, ClassNotFoundException,
			IOException {

		Class.forName("com.ibm.db2.jcc.DB2Driver");

		final File folderNoProcessed = new File(folderSource);
		final File folderProcessed = new File(folderTarget);

		File[] listOfFiles = folderNoProcessed.listFiles();
		String query_origen = "", query_destino = "", conditionJoin = "";
		bdPassSource = passSource;
		bdPassTarget = passTarget;
		bdUserSource = userSource;
		bdUserTarget = userTarget;

		log.setText("");
		log.setText("Starting... \n");
		log.append("INFO: Getting conection info... \n");

		setControllers(bdSource, bdTarget);

		int read = 0; // Flag que usaremos para diferenciar entre query_origen,
						// query_destino.

		// NOTA: Los archivos tienen que estar ordenados en la carpeta de la
		// forma, query_destino_1, query_origen_1 etc
		for (File file : listOfFiles) {

			if (file.isFile()) {
				log.append("INFO: Reading new file --> " + file.getName()
						+ "\n");
			}

			if (read == 0) {

				query_destino = FilesReader.readFile(file);
				query_destino = query_destino.replaceAll("\\s+", " ");
			} else if (read == 1) {

				query_origen = FilesReader.readFile(file);
				query_origen = query_origen.replaceAll("\\s+", " ");

				validate(query_destino, query_origen, conditionJoin, log);
			}

			Path fileToMovePath = file.toPath();
			Path targetPath = folderProcessed.toPath();
			Files.copy(fileToMovePath,
					targetPath.resolve(fileToMovePath.getFileName()),
					StandardCopyOption.REPLACE_EXISTING);

			log.append("INFO: Deleting processed files... \n");

			file.delete(); // Ponemos esto para que los archivos del
			// source se borren al acabar la ejecucion.

			if (read == 1) {
				log.append("\n");
				read = 0;
			} else {
				read++;
			}

		}

		log.append("\n\n SUCCESS");
		log.setLineBackground(log.getLineCount() - 1, 1, Display.getCurrent()
				.getSystemColor(SWT.COLOR_GREEN));

	}

	public static String validateFiles(String fileSource, String fileTarget,
			String folderTarget, String bdSource, String bdTarget,
			String userSource, String userTarget, String passSource,
			String passTarget, StyledText log) throws SQLException,
			ClassNotFoundException, IOException {

		Class.forName("com.ibm.db2.jcc.DB2Driver");

		final File sourceQuery = new File(fileSource);
		final File targetQuery = new File(fileTarget);
		final File folderProcessed = new File(folderTarget);
		bdPassSource = passSource;
		bdPassTarget = passTarget;
		bdUserSource = userSource;
		bdUserTarget = userTarget;

		String query_origen = "", query_destino = "", conditionJoin = "";

		setControllers(bdSource, bdTarget);

		log.setText("");
		log.setText("Starting... \n");
		log.append("INFO: Getting conection info... \n");

		log.append("INFO: Reading Files... \n");
		query_origen = FilesReader.readFile(sourceQuery);
		query_origen = query_origen.replaceAll("\\s+", " ");
		query_destino = FilesReader.readFile(targetQuery);
		query_destino = query_destino.replaceAll("\\s+", " ");

		String resultsTable = validate(query_destino, query_origen, conditionJoin, log);

		Path targetPath = folderProcessed.toPath();
		Files.copy(sourceQuery.toPath(),
				targetPath.resolve(sourceQuery.toPath().getFileName()),
				StandardCopyOption.REPLACE_EXISTING);
		Files.copy(targetQuery.toPath(),
				targetPath.resolve(targetQuery.toPath().getFileName()),
				StandardCopyOption.REPLACE_EXISTING);

		log.append("INFO: Deleting processed files... \n");

		sourceQuery.delete();
		targetQuery.delete();

		log.append("\n Mostrando tabla resultados: \n\n\n");

		Results.showResults(resultado,
				"jdbc:oracle:thin:@//servidor:puerto/BBDD", "usuario",
				"contraseña", log);

		log.append("\n\n SUCCESS");
		log.setLineBackground(log.getLineCount() - 1, 1, Display.getCurrent()
				.getSystemColor(SWT.COLOR_GREEN));
		
		return resultsTable;
	}

	public static String validateQueries(String fileSource, String fileTarget,
			String bdSource, String bdTarget, String userSource,
			String userTarget, String passSource, String passTarget,
			StyledText log) throws SQLException, ClassNotFoundException {

		Class.forName("com.ibm.db2.jcc.DB2Driver");

		bdPassSource = passSource;
		bdPassTarget = passTarget;
		bdUserSource = userSource;
		bdUserTarget = userTarget;

		setControllers(bdSource, bdTarget);

		log.setText("");
		log.setText("Starting... \n");
		log.append("INFO: Getting conection info... \n");

		log.append("INFO: Reading Files... \n");
		String query_origen = fileSource, query_destino = fileTarget, conditionJoin = "";
		query_origen = query_origen.replaceAll("\\s+", " ");
		query_destino = query_destino.replaceAll("\\s+", " ");

		String resultsTable = validate(query_destino, query_origen, conditionJoin, log);

		log.append("\n Mostrando tabla resultados: \n\n\n");

		Results.showResults(resultado,
				"jdbc:oracle:thin:@//servidor:puerto/BBDD", "usuario",
				"contraseña", log);

		log.append("\n\n SUCCESS");
		log.setLineBackground(log.getLineCount() - 1, 1, Display.getCurrent()
				.getSystemColor(SWT.COLOR_GREEN));
		
		return resultsTable;
		
	}

}
