package util;

public class ResourcesHandler {

	// These filter names are displayed to the user in the file dialog. Note
	// that
	// the inclusion of the actual extension in parentheses is optional, and
	// doesn't have any effect on which files are displayed.
	public static final String[] FILTER_NAMES = { "SQL Files (*.sql)",
			"All Files (*.*)" };

	// These filter extensions are used to filter which files are displayed.
	public static final String[] FILTER_EXTS = { "*.sql", "*.*" };

	// Usamos este enumerado para almacenar los tipos de base de datos.
	public static final String[] BDs = { "DB2-DES", "DB2-PRE", "DB2-PRO",
			"ORA-DES", "ORA-PRE", "ORA-PRO" };

	public static String[] getBDsTarget(String bd) {

		if (bd.startsWith("ORA")) {
			String[] BDsT = { bd };

			return BDsT;
		} else {
			String entorno = bd.substring(bd.indexOf("-") + 1);

			String bdAux = "ORA-" + entorno;

			String[] BDsT = { bd, bdAux };

			return BDsT;
		}

	}

}
