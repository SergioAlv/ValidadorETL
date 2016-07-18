package util;

import java.util.ArrayList;
import java.util.List;

public class ScriptParser {
	
	public static List<String> parseScript(String script) {
		
		List<String> operaciones = new ArrayList<String>();
		String particion;
		int end = 0;
		while (end != 1) {
			
			if (script.indexOf(";") == -1) {
				if (script.contains("select") || script.contains("SELECT")) {
					operaciones.add(script);
				}
				end = 1;
			} else {
				particion = script.substring(0, script.indexOf(";"));
				script = script.substring(script.indexOf(";")+1);
				operaciones.add(particion);
			}
			
		}
		
		return operaciones;
		
	}

}
