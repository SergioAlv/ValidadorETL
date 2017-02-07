package modelo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import util.CommentCreator;
import util.ExcelCreator;
import util.GenericCellStyle;

public class ResultsReport {

	public static void generate(Shell shell, StyledText logText, String resultsTable, String fileName) throws IOException {
		
		Connection con;
		
		FileOutputStream archivo = ExcelCreator.create(shell, logText, fileName);
		
		Workbook libro = new HSSFWorkbook();
		Sheet hoja = libro.createSheet("Errores Validación");
		
		try {
			con = DriverManager.getConnection("jdbc:oracle:thin:@//localhost:1521/xe", "developer",
					"developer");
			
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select * from "
				+ resultsTable + " where validacion > 0");
		
			Row fila = hoja.createRow(0);
			Cell celda = fila.createCell(0);
			celda.setCellValue(resultsTable.toUpperCase());
			
			
			celda.setCellStyle(GenericCellStyle.getStyle(libro, HSSFColor.YELLOW.index));

			int nColumnas = rs.getMetaData().getColumnCount();
			Row filaNombreColumnas = hoja.createRow(rs.getRow()+1);
			CellStyle csColumns = GenericCellStyle.getStyle(libro, HSSFColor.LIGHT_ORANGE.index);
			for (int i = 0; i < nColumnas; i++) {
				Cell celdaTabla = filaNombreColumnas.createCell(i);
					
				String columnName = rs.getMetaData().getColumnName(i+1);
				
				celdaTabla.setCellValue(columnName);
				celdaTabla.setCellStyle(csColumns);
				hoja.autoSizeColumn(i);
			}
			
			logText.append("INFO: Consultando tabla de resultados de validacion --> " + resultsTable + "\n");
			
			CellStyle csReg = GenericCellStyle.getStyle(libro, HSSFColor.AQUA.index);
			
			// TODO 13/11/2016 se puede eliminar si no da mas lata sin descomentar.
			/*int pointer = 1;
			String aux = "";
			String commentError = "Estamos probando la ejecucion de los comentarios. \nMe gusta mucho como esta quedando esto. \n\nEs muy bonito.";
			int flagFila = -2;*/
			while (rs.next()) {
				Row filaTabla = hoja.createRow(rs.getRow()+1);
				for (int i = 0; i < nColumnas; i++) {
					Cell celdaTabla = filaTabla.createCell(i);
					
					String tipoColumna = rs.getMetaData().getColumnTypeName(1);
					if (rs.getString(i+1) == null) {
						celdaTabla.setCellValue("null");
					} else if (tipoColumna == "NUMBER") {
						celdaTabla.setCellValue(rs.getString(i+1));
					} else if (tipoColumna == "TIMESTAMP") {
						celdaTabla.setCellValue(rs.getTimestamp(i+1));
					} else if (tipoColumna == "DATE") {
						celdaTabla.setCellValue(rs.getDate(i+1));
					} else if (tipoColumna == "INTEGER") {
						celdaTabla.setCellValue(rs.getInt(i+1));
					} else if (tipoColumna == "VARCHAR2") {
						celdaTabla.setCellValue(rs.getString(i+1));
					} else if (tipoColumna == "FLOAT") {
						celdaTabla.setCellValue(rs.getDouble(i+1));
					} else {
						celdaTabla.setCellValue(rs.getString(i+1));
					}
					
					// TODO 13/11/2016 se puede eliminar si no da mas lata sin descomentar. Control del cambio de fila
					/*if (flagFila != rs.getRow()+1) {
						pointer = 1;
						flagFila = rs.getRow()+1;
					}*/
					
					//Almacena el valor de la primera celda en un auxiliar, y comparar con la segunda, usando la columna de validación para introducir comentario.
					//TODO No se por que empieza a introducir comentarios en la fila 156
					//TODO No puedo coger el valor de la celda como String?
					/*if (pointer == 0) {
						Comment comment = CommentCreator.create(commentError, celdaTabla);
						celdaTabla.setCellComment(comment);
						pointer++;
					} else if (pointer == 1) {
						//aux = celdaTabla.getStringCellValue();
						pointer++;
					} else if (pointer == 2){
						pointer = 0;
						//commentError = ControlError.resolve(aux, celdaTabla.getStringCellValue());
					}*/
					
					celdaTabla.setCellStyle(csReg);
				}
			}
			
			logText.append("INFO: Guardando fichero... \n ");
			
			libro.write(archivo);
			archivo.close();
			
			logText.append("\n\n SUCCESS");
			logText.setLineBackground(logText.getLineCount() - 1, 1, Display.getCurrent()
					.getSystemColor(SWT.COLOR_GREEN));
		
		} catch (SQLException e) {
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
