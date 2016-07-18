package modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.swt.custom.StyledText;

public class Results {

	private static void generarEspacios(StyledText log, int espacios) {

		for (int i = 1; i <= espacios; i++) {
			log.append("-");
		}

	}

	public static void showResults(String resultado, String bdController,
			String bdUser, String bdPass, StyledText log) throws SQLException {
		Connection con = DriverManager.getConnection(bdController, bdUser,
				bdPass);
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("select CAMPO, CANTIDAD from "
				+ resultado);

		log.append("CAMPO                                             CANTIDAD \n");
		log.append("---------------------------------------------------------- \n");
		// 45 espacios
		while (rs.next()) {
			String campo = rs.getString(1);
			String cantidad = Integer.toString(rs.getInt(2));
			log.append(campo);

			// Numero de espacios --> 42-campo.length()); Queremos 54 espacios
			generarEspacios(log, 54 - campo.length()); // TODO Imprimir la
														// separacion
														// correctamente

			log.append(cantidad + "\n");
		}

		log.append("\n");
	}

}
