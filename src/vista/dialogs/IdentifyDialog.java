package vista.dialogs;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import controlador.TransformationModule;
import util.QueryProvider;
import vista.tabs.QueryTab;

public class IdentifyDialog {

	private static int editors = 0;
	private static Map<Text, Integer> mapping = new HashMap<Text, Integer>();
	
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
		final TableEditor[] textEditor = new TableEditor[items.length-2];
		final TableEditor conditionEditor;
		final TableEditor groupEditor;
		final Text[] trans = new Text[items.length-2];
		
		//Listener to enter transformations
		Listener listener = new Listener()
	    {
			@Override
	        public void handleEvent(Event e)
	        {
	            Text t = (Text) e.widget;

	            editors = mapping.get(t);
	            trans[editors].addModifyListener(new ModifyListener(){
				      public void modifyText(ModifyEvent event) {
					        // Get the widget whose text was modified
					        Text text = (Text) event.widget;
					        textEditor[editors].getItem().setText(3, text.getText());
					      }
					});
	        }
	    };

		
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
			
			textEditor[i] = new TableEditor(table);
			trans[i] = new Text(table, SWT.BORDER);
			trans[i].setText("");
			textEditor[i].grabHorizontal = true;
			textEditor[i].setEditor(trans[i], items[i], 3);
			mapping.put(trans[i], i);
			trans[i].addListener(SWT.MouseDown, listener);

		}

			
		editor = new TableEditor(table);
		final Text groupLabel = new Text(table, SWT.BORDER);
		groupLabel.setText("GROUP BY ");
		groupLabel.setEditable(false);
		editor.grabHorizontal = true;
		editor.setEditor(groupLabel, items[nItems-2], 0);
		
		
		groupEditor = new TableEditor(table);
		Text group = new Text(table, SWT.BORDER);
		group.setText("");
		groupEditor.grabHorizontal = true;
		groupEditor.setEditor(group, items[nItems-2], 1);
		group.addModifyListener(new ModifyListener(){
	      public void modifyText(ModifyEvent event) {
		        // Get the widget whose text was modified
		        Text text = (Text) event.widget;
		        groupEditor.getItem().setText(1, text.getText());
		      }
		});
		
		
		editor = new TableEditor(table);
		final Text condLabel = new Text(table, SWT.BORDER);
		condLabel.setText("Condition = ");
		condLabel.setEditable(false);
		editor.grabHorizontal = true;
		editor.setEditor(condLabel, items[nItems-1], 0);
		
		conditionEditor = new TableEditor(table);
		Text condition = new Text(table, SWT.BORDER);
		condition.setText("a. = b.");
		conditionEditor.grabHorizontal = true;
		conditionEditor.setEditor(condition, items[nItems-1], 1);
		condition.addModifyListener(new ModifyListener(){
	      public void modifyText(ModifyEvent event) {
		        // Get the widget whose text was modified
		        Text text = (Text) event.widget;
		        conditionEditor.getItem().setText(1, text.getText());
		      }
		});
		
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
		userName2.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));

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
				}
				
				//ENVIAMOS AL CONTROLADOR LAS TRANSFORMACIONES COMO CLAVE-VALOR (targetColum-transformation): movId = movId    O    balance = SUM(balance) BY usrId
				Map<String, String> transformations = new HashMap<String, String>();
				boolean noContinue = false;
				for (int i = 0; i < nItems-2; i++) {
					String target = targetColumns.get(i);
					
					if (textEditor[i].getItem().getText(3) == "") {
						QueryTab.setNotTransformations(false);
						noContinue = true;
					} else {
						transformations.put(target, textEditor[i].getItem().getText(3));
					}

				}

				if (!noContinue) {
					String groupBy = "";
					if(groupEditor.getItem().getText(1) != "") {
						groupBy = " GROUP BY " + groupEditor.getItem().getText(1);
					}
					String formatedSrcQ = TransformationModule.FormatQuery(sourceRef, transformations, targetColumns, groupBy);
					
					QueryTab.setCondition(conditionEditor.getItem().getText(1));
					QueryTab.setSrcQuery(formatedSrcQ);
					QueryTab.setNotTransformations(true);
					ReportDialog.setTransformations(transformations);
				}
				
				dialog.dispose();
				
			}
		});

		while (!dialog.isDisposed()) {
			if (!shell.getDisplay().readAndDispatch())
				shell.getDisplay().sleep();
		}

	}

}
