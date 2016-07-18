package util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.RichTextString;

public class CommentCreator {

	public static Comment create(String commentStr, Cell celda) {
		
		//http://stackoverflow.com/questions/16099912/creating-cell-comments-in-apache-poi-for-xlsx-files-with-show-comments-disabl
		 Drawing drawing = celda.getSheet().createDrawingPatriarch();
		 CreationHelper factory = celda.getSheet().getWorkbook().getCreationHelper();
		 ClientAnchor anchor = factory.createClientAnchor();
		 anchor.setCol1(celda.getColumnIndex());
		 anchor.setCol2(celda.getColumnIndex() + 1);
		 anchor.setRow1(celda.getRowIndex());
		 anchor.setRow2(celda.getRowIndex() + 3);
		 //TODO Configurar dimensionado del comentario
		 //miguelitosss@gmail.com --> Miguel Barral (Pequeño Satán)
		 /*anchor.setDx1(300);
		 anchor.setDx2(200);
		 anchor.setDy1(300);
		 anchor.setDy2(200);*/

		 Comment comment = drawing.createCellComment(anchor);
		 RichTextString str = factory.createRichTextString(commentStr);
		 comment.setVisible(Boolean.FALSE);
		 comment.setString(str);
		 
		return comment;
		
	}
	
}
