package modelo;

import java.sql.*;

import org.eclipse.swt.custom.StyledText;

public class DataProcessor {
	public static int BATCH_MAX = 2000; // batch updates to be fasterbut don't
										// blow the memory out on the db driver
										// (sybase).

	public static void processInfo(String condicionCruce,
			String tablaOrigen, String tablaDestino,
			String nombreTablaValidacion, String jdbcConexion,
			String usrConexion, String passConexion, StyledText log)
			throws SQLException {

		Connection con = DriverManager.getConnection(jdbcConexion, usrConexion,
				passConexion);
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("select * from " + tablaOrigen);
		ResultSetMetaData metadata = rs.getMetaData();
		int columnCount = metadata.getColumnCount();

		Statement smtControlDrop = con.createStatement();
		try {
			smtControlDrop.execute("DROP TABLE " + nombreTablaValidacion);
			log.append("INFO: Tabla existente, reemplazando... \n");
			log.append("INFO: Drop table " + nombreTablaValidacion + "\n");
		} catch (Exception e) {
			// IGNORE
		}

		String createTable = "create table  " + nombreTablaValidacion
				+ " as select res.*, (";

		for (int i = 1; i <= columnCount; i++) {
			String columnName = metadata.getColumnName(i);
			if (columnName.length() > 26) {
				columnName = columnName.substring(0, 26);
			}
			createTable = createTable + "val_" + columnName;
			if (i == columnCount) {
				createTable = createTable + ") validacion from (select  ";
			} else {
				createTable = createTable + "+";
			}

		}

		for (int i = 1; i <= columnCount; i++) {
			String columnName = metadata.getColumnName(i);
			String columnNameAlias = metadata.getColumnName(i);
			if (columnName.length() > 26) {
				columnNameAlias = columnName.substring(0, 26);
			}
			createTable = createTable + " a." + columnName + " "
					+ "ori_" + columnNameAlias + ",b." + columnName + " "
					+ "des_" + columnNameAlias + ",case when a." + columnName
					+ "=b." + columnName + " or (a." + columnName
					+ " is null and b." + columnName
					+ " is null) then 0 else 1 end val_" + columnNameAlias;
			if (i != columnCount) {
				createTable = createTable + ", ";
			}
		}

		createTable = createTable + " from " + tablaOrigen + " a "
				+ "FULL OUTER join " + tablaDestino + "  b " + " on ("
				+ condicionCruce + ")" + ") res";

		log.append("INFO: " + createTable + "\n");
		stmt = con.createStatement();
		stmt.execute(createTable);

		Statement smtControlDrop2 = con.createStatement();
		try {
			smtControlDrop2.execute("DROP TABLE " + nombreTablaValidacion
					+ "_2");
			log.append("INFO: Tabla existente, reemplazando... \n");
			log.append("INFO: Drop table " + nombreTablaValidacion + "\n");
		} catch (Exception e) {
			// IGNORE
		}

		createTable = "create table "
				+ nombreTablaValidacion
				+ ""
				+ "_2 as SELECT 'REGISTROS ORIGEN' campo,COUNT(*) CANTIDAD FROM "
				+ tablaOrigen + " UNION ALL "
				+ "SELECT 'REGISTROS DESTINO',COUNT(*) FROM " + tablaDestino
				+ " UNION ALL SELECT ";

		//TODO Generar otro select que devuelva la suma de registros con un 1 en el campo de validación
		
		for (int i = 1; i <= columnCount; i++) {
			String columnName = metadata.getColumnName(i);
			if (columnName.length() > 26) {
				columnName = columnName.substring(0, 26);
			}
			createTable = createTable + "'" + columnName + "',SUM(VAL_"
					+ columnName + ") FROM " + nombreTablaValidacion;
			if (i != columnCount) {
				createTable = createTable + " UNION ALL SELECT ";
			}

		}

		log.append("INFO: " + createTable + "\n");
		stmt = con.createStatement();
		stmt.execute(createTable);

	}

}
