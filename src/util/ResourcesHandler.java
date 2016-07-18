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
	public static final String[] BDs = { "DB22-DES", "DB22-PRE", "DB22-PRO",
			"DB26-DES", "DB26-PRE", "DB26-PRO", "ORA-DES", "ORA-PRE", "ORA-PRO" };

	public static String[] getBDsTarget(String bd) {

		if (bd.startsWith("ORA")) {
			String[] BDsT = { bd };

			return BDsT;
		} else {
			String entorno = bd.substring(bd.indexOf("-") + 1);

			String bdAux = "ORA-" + entorno;

			String bd2 = "";
			if (bd.startsWith("DB22")) {
				bd2 = "DB26-" + entorno;
			} else if (bd.startsWith("DB26")) {
				bd2 = "DB22-" + entorno;
			}

			String[] BDsT = { bd, bd2, bdAux };

			return BDsT;
		}

	}

}
