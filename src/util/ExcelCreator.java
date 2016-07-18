package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import vista.dialogs.ConfirmDialog;

public class ExcelCreator {

	private static boolean replace;
	
	public static void setReplaceFlag (boolean flag) {
		replace = flag;
	}
	
	public static FileOutputStream create(Shell shell, StyledText logText, String fileName) throws FileNotFoundException {
		
		Path currentRelativePath = Paths.get("");
		String rutaArchivo = currentRelativePath.toAbsolutePath().toString() + "\\" + fileName + ".xls";
		File archivoXLS = new File(rutaArchivo);
		
		logText.append("INFO: Creando el archivo especificado -> " + rutaArchivo + "\n");
		
		replace = false;
		if(archivoXLS.exists()){
			ConfirmDialog.launch(shell, logText); //TODO Preguntar si es correcto pasar el shell al modelo para que llegue hasta aqui.
		} 
		
		if (replace == true) {
			archivoXLS.delete();
			logText.append("INFO: El archivo ya existe en el directorio. Reemplazando... \n");
		}
		 
		try {
			
			archivoXLS.createNewFile();
			
		} catch (IOException e) {
			logText.append("\n");
			logText.append("\n");
			logText.append("ERROR: Algo no ha ido bien...");
			logText.setLineBackground(logText.getLineCount() - 1, 1,
					Display.getCurrent().getSystemColor(SWT.COLOR_RED));

			int color = logText.getCharCount();
			logText.append("\n\n" + e.getLocalizedMessage());
			
			StyleRange style = new StyleRange();
			style.start = color;
			style.length = logText.getCharCount() - color;
			style.foreground = Display.getCurrent().getSystemColor(
					SWT.COLOR_RED);
			logText.setStyleRange(style);
		}
		
		FileOutputStream archivo = new FileOutputStream(archivoXLS);

		return archivo;
		
	}
	
}
