package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FilesReader {
	public static String readFile(File f) throws IOException {
		String consulta = "";
		if (f.exists()) {
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			String linea;
			while ((linea = br.readLine()) != null) {
				String lectura = linea;
				consulta = consulta + " \n" + lectura;
			}

			fr.close();

		} else {
			System.out.println("El fichero no existe");
		}
		return consulta;

	}
}
