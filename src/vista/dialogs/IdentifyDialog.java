package vista.dialogs;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import controlador.TransformationModule;
import util.QueryProvider;
import vista.tabs.FileTab;
import vista.tabs.QueryTab;

public class IdentifyDialog {

	public static void launch(Shell shell, String src, String trg,
			String passSource, String passTarget, String userSource,
			String userTarget, final char tab, String sourceQuery, String targetQuery) {

		GridLayout gridLayout = new GridLayout(3, false);
		final Shell dialog = new Shell(shell, SWT.DIALOG_TRIM
				| SWT.APPLICATION_MODAL);
		dialog.setLayout(gridLayout);
		dialog.setText("Login...");
		dialog.setSize(420, 500);
		dialog.setMinimumSize(420, 500);


		//RECUPERAMOS DE LAS QUERIES LAS COLUMNAS DE CADA UNA
		final String sourceRef = sourceQuery;
		final java.util.List<String> sourceColumns = QueryProvider.GetColumns(sourceQuery);
		final java.util.List<String> targetColumns = QueryProvider.GetColumns(targetQuery);
		
		//CREAMOS UNA TABLA QUE NOS PERMITA MAPEAR TODOS LOS CAMPOS
		final Table table = new Table(dialog, SWT.BORDER | SWT.MULTI);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				3, 2));
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		
		final int nItems = Math.max(sourceColumns.size(),targetColumns.size()+2);
		
		TableColumn columnSource = new TableColumn(table, SWT.NONE);
		columnSource.setWidth(100);
		columnSource.setText("Sources");
		TableColumn columnTrans = new TableColumn(table, SWT.NONE);
		columnTrans.setWidth(100);
		columnTrans.setText("");
		TableColumn columnTarget = new TableColumn(table, SWT.NONE);
		columnTarget.setWidth(100);
		columnTarget.setText("Targets");
		TableColumn columnKey = new TableColumn(table, SWT.NONE);
		columnKey.setWidth(100);
		columnKey.setText("Transformation");
		


		
		//RELLENAMOS LA TABLA CON LAS COLUMNAS DE LAS QUERIES, LAS POSIBLES TRANSFORMACIONES, Y UN CHECK PARA LA CLAVE PRIMARIA
		table.setItemCount(nItems);
		TableItem[] items = table.getItems();
		TableEditor editor = new TableEditor(table);
		for (int i = 0; i < items.length-2; i++) {
			
			editor = new TableEditor(table);
			final Text srcCols = new Text(table, SWT.BORDER);
			srcCols.setText(sourceColumns.get(i));
			srcCols.setEditable(false);
			editor.grabHorizontal = true;
			editor.setEditor(srcCols, items[i], 0);

			
			editor = new TableEditor(table);
			final Text separator = new Text(table, SWT.BORDER);
			separator.setText("|");
			separator.setEditable(false);
			editor.grabHorizontal = true;
			editor.setEditor(separator, items[i], 1);
			
			editor = new TableEditor(table);
			final Text tgtCols = new Text(table, SWT.BORDER);
			tgtCols.setText(targetColumns.get(i) + " = ");
			tgtCols.setEditable(false);
			editor.grabHorizontal = true;
			editor.setEditor(tgtCols, items[i], 2);

			
			// TODO INTENTAR QUE SALGA UN DIALOGO PARA INTRODUCIR EL TEXTO DE LA TRANSFORMACION EN UNA VENTANA GRANDE
		    editor = new TableEditor(table);
			final Text trans = new Text(table, SWT.BORDER);
			trans.setText(sourceColumns.get(i));
			editor.grabHorizontal = true;
			editor.setEditor(trans, items[i], 3);
			
		} 
			
		editor = new TableEditor(table);
		final Text condLabel = new Text(table, SWT.BORDER);
		condLabel.setText("Condition = ");
		condLabel.setEditable(false);
		editor.grabHorizontal = true;
		System.out.println(items.length);
		editor.setEditor(condLabel, items[nItems-1], 0);
			
		
		// TODO INTENTAR QUE SALGA UN DIALOGO PARA INTRODUCIR EL TEXTO DE LA TRANSFORMACION EN UNA VENTANA GRANDE	
		editor = new TableEditor(table);
		final Text condition = new Text(table, SWT.BORDER);
		condition.setText("Enter Condition");
		editor.grabHorizontal = true;
		editor.setEditor(condition, items[nItems-1], 1);
		
		
		// CREAMOS UN GRUPO PARA LAS CREDENCIALES DEL SOURCE
		Group source = new Group(dialog, SWT.SHADOW_IN);
		source.setText("UserName/Pass for " + src);
		source.setLayout(new GridLayout(2, false));

		final Label labelUser = new Label(source, SWT.NONE);
		labelUser.setText("Username:");
		final Text userName = new Text(source, SWT.BORDER);
		userName.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));

		final Label labelPass = new Label(source, SWT.NONE);
		labelPass.setText("Pass:");
		final Text pass = new Text(source, SWT.BORDER | SWT.PASSWORD);
		pass.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));

		final Button login = new Button(dialog, SWT.PUSH);
		login.setText("Login");

		// CREAMOS UN GRUPO PARA LAS CREDENCIALES DEL TARGET
		Group target = new Group(dialog, SWT.SHADOW_IN);
		target.setText("UserName/Pass for " + trg);
		target.setLayout(new GridLayout(2, false));

		final Label labelUser2 = new Label(target, SWT.NONE);
		labelUser2.setText("Username:");
		final Text userName2 = new Text(target, SWT.BORDER);
		userName2
				.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));

		final Label labelPass2 = new Label(target, SWT.NONE);
		labelPass2.setText("Pass:");
		final Text pass2 = new Text(target, SWT.BORDER | SWT.PASSWORD);
		pass2.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));

		dialog.open();

		// Move the dialog to the center of the top level shell.
		Rectangle shellBounds = shell.getShell().getBounds();
		Point dialogSize = dialog.getSize();

		dialog.setLocation(shellBounds.x + (shellBounds.width - dialogSize.x)
				/ 2, shellBounds.y + (shellBounds.height - dialogSize.y) / 2);

		if (passSource != "") {
			pass.setText(passSource);
		}

		if (passTarget != "") {
			pass2.setText(passTarget);
		}

		if (userSource != "") {
			userName.setText(userSource);
		}

		if (userTarget != "") {
			userName2.setText(userTarget);
		}

		login.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (tab == 'Q') {
					QueryTab.setCredentials(pass.getText(), pass2.getText(),
							userName.getText(), userName2.getText(), true);
				} else if (tab == 'f') {
					FileTab.setCredentials(pass.getText(), pass2.getText(),
							userName.getText(), userName2.getText(), true);
				}
	
				//ENVIAMOS AL CONTROLADOR LAS TRANSFORMACIONES COMO CLAVE-VALOR (targetColum-transformation): movId = movId    O    balance = SUM(balance) BY usrId
				TableItem[] items = table.getItems();
				Map<String, String> transformations = new HashMap<String, String>();
				for (int i = 0; i < nItems-2; i++) {
					
					int equal = items[i].getText().indexOf("=");
					String target = items[i].getText(2).substring(0, equal-1);
					transformations.put(target, items[i].getText(3));
					
				}

				String formatedSrcQ = TransformationModule.FormatQuery(sourceRef, transformations, targetColumns);
				
				QueryTab.setSrcQuery(formatedSrcQ);
				
				dialog.dispose();
			}
		});

		while (!dialog.isDisposed()) {
			if (!shell.getDisplay().readAndDispatch())
				shell.getDisplay().sleep();
		}

	}

}
