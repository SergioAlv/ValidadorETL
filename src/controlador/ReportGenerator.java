package controlador;

import java.io.IOException;
import java.util.Map;

import modelo.ResultsReport;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ReportGenerator {

	public static void generateReport(Shell shell, StyledText logText, String resultsTable, String fileName, Map<String, String> transformations) {
		
		logText.append("\n");
		logText.append("\n");
		logText.append("INFO: Generando informe de errores... \n");
		
		try {
			
			ResultsReport.generate(shell, logText, resultsTable, fileName, transformations);
			
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
		
	}
	
}
