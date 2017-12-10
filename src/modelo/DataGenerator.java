package modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.custom.StyledText;

import util.ScriptParser;

public class DataGenerator {
	public static int BATCH_MAX = 2000; // batch updates to be fasterbut don't

	// blow the memory out on the db driver
	// (sybase).
	public static int generate(String nombreTabla, List<String> targetColumns, String jdbcConexion,
			String usrConexion, String passConexion, String queryConsulta,
			StyledText log) throws SQLException {
		Connection conControl = DriverManager.getConnection(
				"jdbc:oracle:thin:@//localhost:1521/xe", "developer",
				"developer");
		Connection con = DriverManager.getConnection(jdbcConexion, usrConexion,
				passConexion);
		Statement stmt = con.createStatement();
		
		//Devuelve las operaciones a partir de la query segun su ';'
		List<String> operaciones = ScriptParser.parseScript(queryConsulta);
		
		//Si es un script añadimos al batch todas las operaciones previas al select, que sera la ultima operacion
		if (operaciones.size() > 1) {
			for (int i = 0; i < operaciones.size()-1; i++) {
				stmt.addBatch(operaciones.get(i));
			}
			stmt.executeBatch();
		}
		
		queryConsulta = operaciones.get(operaciones.size()-1); //Consulta select, ultimo elemento del script
		
		log.append(queryConsulta + "\n");
		
		ResultSet rs = stmt.executeQuery(queryConsulta);
		ResultSetMetaData metadata = rs.getMetaData();
		int columnCount = metadata.getColumnCount();

		Statement smtControlDrop = conControl.createStatement();
		try {
			smtControlDrop.execute("DROP TABLE " + nombreTabla);
			log.append("INFO: Tabla existente, reemplazando... \n");
			log.append("INFO: Drop table " + nombreTabla + "\n");
		} catch (Exception e) {
			// IGNORE
		}

		String createTable = "create table  " + nombreTabla + " (";
		String tipoDato = "";

		ArrayList<String> columns = new ArrayList<String>();
		for (int i = 1; i <= columnCount; i++) {
			//String columnName = metadata.getColumnName(i);
			String columnName = targetColumns.get(i-1);
			String columnType = metadata.getColumnTypeName(i);
			if (columnType == "NUMBER" || columnType == "TIMESTAMP"
					|| columnType == "DATE" || columnType == "INTEGER") {
				tipoDato = "";
			} else if (columnType == "VARCHAR2") {
				tipoDato = "( " + 3200 + ")";
			} else if (columnType == "VARGRAPHIC") {
				columnType = "VARCHAR2";
				tipoDato = "( " + 3200 + ")";
			} else if (columnType == "DECIMAL" || columnType == "DECFLOAT") {
				columnType = "FLOAT";
				tipoDato = "";
			} else if (columnType == "VARCHAR") {
				columnType = "VARCHAR2";
				tipoDato = "( " + 3200 + ")";
			} else if (columnType == "CHAR") {
				columnType = "VARCHAR2";
				tipoDato = "( " + 3200 + ")";
			} else if (columnType == "SMALLINT") {
				columnType = "INTEGER";
				tipoDato = "";
			} else if (columnType == "BIGINT") {
				columnType = "FLOAT";
				tipoDato = "";
			}

			createTable = createTable + columnName + "  " + columnType
					+ tipoDato;
			if (i != columnCount) {
				createTable = createTable + ", ";
			} else {
				createTable = createTable + ") ";
			}

			columns.add(columnName);
		}

		String sqlInsert = "insert into  " + nombreTabla + " (";
		String questionMarks = "";
		int iColumns = metadata.getColumnCount();
		for (int c = 1; c <= iColumns; c++) {
			if (c > 1) {
				sqlInsert += ",";
				questionMarks += ",";
			}
			sqlInsert += targetColumns.get(c-1);
			questionMarks += "?";
		}
		sqlInsert += ")  " + "values ( " + questionMarks + ")";

		
		PreparedStatement pstatementOut = conControl
				.prepareStatement(sqlInsert);
		int iRecords = 0;
		log.append("INFO: " + createTable + "\n");
		Statement smtControlOrigen = conControl.createStatement();
		smtControlOrigen.execute(createTable);

		while (rs.next()) {
			for (int c = 1; c <= iColumns; c++) {
				pstatementOut.setObject(c, rs.getObject(c));
			}
			pstatementOut.addBatch();
			iRecords++;
			if (iRecords > BATCH_MAX) {
				iRecords = 0;
				pstatementOut.executeBatch();
			}
		}
		if (iRecords > 0)
			pstatementOut.executeBatch();

		return iRecords;
	}
}
