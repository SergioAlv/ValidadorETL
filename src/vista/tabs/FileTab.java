package vista.tabs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;

import util.QueryProvider;
import util.ResourcesHandler;
import vista.dialogs.IdentifyDialog;
import vista.dialogs.ReportDialog;

import com.ibm.db2.jcc.am.SqlInvalidAuthorizationSpecException;

import controlador.Validator;

public class FileTab {

	private static String passSource = "";
	private static String passTarget = "";
	private static String userSource = "";
	private static String userTarget = "";
	private static boolean ejecucion = false;

	public static void setCredentials(String passS, String passT, String userS,
			String userT, boolean ejec) {
		passSource = passS;
		passTarget = passT;
		userSource = userS;
		userTarget = userT;
		ejecucion = ejec;
	}
	
	public static Control getTabFileControl(TabFolder tabFolder) {
		// Create a composite and add button
		final Composite composite = new Composite(tabFolder, SWT.NONE);
		GridLayout gridLayout = new GridLayout(3, false);
		composite.setLayout(gridLayout);

		// CONTROL PARA DATABASE SORUCE
		Group databaseSource = new Group(composite, SWT.HORIZONTAL);
		databaseSource.setText("Select DB Source");
		databaseSource.setLayout(gridLayout);

		final Combo bdsCombo = new Combo(databaseSource, SWT.DROP_DOWN);
		bdsCombo.setItems(ResourcesHandler.BDs);

		// SEPARADOR
		final Label label1 = new Label(composite, SWT.NONE);
		label1.setText("<- Por favor, seleccione BD Origen para ver Destinos disponibles ->");
		label1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));

		// CONTROL PARA DATABASE TARGET
		Group databaseTarget = new Group(composite, SWT.HORIZONTAL);
		databaseTarget.setText("Select DB Target");
		databaseTarget.setLayout(gridLayout);

		final Combo bdsCombo2 = new Combo(databaseTarget, SWT.DROP_DOWN);
		bdsCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (bdsCombo.getTouchEnabled() != true) {
					String bd = bdsCombo.getText();
					String[] BDsTarget = ResourcesHandler.getBDsTarget(bd);
					bdsCombo2.setItems(BDsTarget);
				}
			}
		});

		// SEPARADOR
		@SuppressWarnings("unused")
		final Label label2 = new Label(composite, SWT.NONE);

		// CONTROL PARA EXAMINAR SOURCE
		Group source = new Group(composite, SWT.SHADOW_IN);
		source.setText("Source Query");
		source.setLayout(gridLayout);
		source.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));

		final Text fileName = new Text(source, SWT.BORDER);
		fileName.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));

		Button openSource = new Button(source, SWT.PUSH);
		openSource.setText("Browse...");
		openSource.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				FileDialog dlg = new FileDialog(composite.getShell(), SWT.OPEN);
				dlg.setFilterNames(ResourcesHandler.FILTER_NAMES);
				dlg.setFilterExtensions(ResourcesHandler.FILTER_EXTS);
				String fn = dlg.open();
				if (fn != null) {
					fileName.setText(fn);
				}
			}
		});

		// SEPARADORES
		@SuppressWarnings("unused")
		final Label label3 = new Label(composite, SWT.NONE);
		@SuppressWarnings("unused")
		final Label label4 = new Label(composite, SWT.NONE);

		// CONTROL PARA EXAMINAR TARGET
		Group target = new Group(composite, SWT.HORIZONTAL);
		target.setText("Target Query");
		target.setLayout(gridLayout);
		target.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));

		final Text fileName2 = new Text(target, SWT.BORDER);
		fileName2
				.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));

		Button openTarget = new Button(target, SWT.PUSH);
		openTarget.setText("Browse...");
		openTarget.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				FileDialog dlg = new FileDialog(composite.getShell(), SWT.OPEN);
				dlg.setFilterNames(ResourcesHandler.FILTER_NAMES);
				dlg.setFilterExtensions(ResourcesHandler.FILTER_EXTS);
				String fn = dlg.open();
				if (fn != null) {
					fileName2.setText(fn);
					fileName2.getText();
				}
			}
		});

		// SEPARADORES
		@SuppressWarnings("unused")
		final Label label5 = new Label(composite, SWT.NONE);
		@SuppressWarnings("unused")
		final Label label6 = new Label(composite, SWT.NONE);

		// CONTROL PARA EXAMINAR CARPETA DESTINO
		Group folderTarget = new Group(composite, SWT.HORIZONTAL);
		folderTarget.setText("Target Folder");
		folderTarget.setLayout(gridLayout);
		folderTarget.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true,
				false));

		final Text folderName = new Text(folderTarget, SWT.BORDER);
		folderName.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true,
				false));

		Path currentRelativePath = Paths.get("");
		final File conf = new File(currentRelativePath.toAbsolutePath()
				.toString() + "/target.txt");
		// Si existe el fichero de configuración, cargamos la información que
		// contiene.
		if (conf.exists()) {
			BufferedReader br;
			try {
				br = new BufferedReader(new FileReader(conf));
				String fName = br.readLine();
				final File auxF = new File(fName);
				if (auxF.exists()) {
					folderName.setText(fName);
				} else {
					conf.delete();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Button openFolderTarget = new Button(folderTarget, SWT.PUSH);
		openFolderTarget.setText("Browse...");
		openFolderTarget.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				DirectoryDialog dlg = new DirectoryDialog(composite.getShell());
				dlg.setFilterPath(folderName.getText());
				dlg.setText("Almacenar");
				dlg.setMessage("Selecciona carpeta destino...");
				String dir = dlg.open();
				if (dir != null) {
					folderName.setText(dir);
				}
			}
		});

		// SEPARADORES
		final Button checkBox = new Button(composite, SWT.CHECK);
		@SuppressWarnings("unused")
		final Label label8 = new Label(composite, SWT.NONE);

		// BOTON DE VALIDACIÓN
		final Button validate = new Button(composite, SWT.PUSH);
		validate.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
		validate.setText("VALIDATE");

		// SEPARADORES
		@SuppressWarnings("unused")
		final Label label9 = new Label(composite, SWT.NONE);

		// CUADRO DE LOG
		final StyledText logText = new StyledText(composite, SWT.BORDER
				| SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL);
		logText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 0));
		logText.addListener(SWT.Modify, new Listener() {// .addListener(SWT.Modify,
														// new Listener(){
					public void handleEvent(Event e) {
						logText.setTopIndex(logText.getLineCount() - 1);
					}
				});

		validate.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					if ((bdsCombo2.getText().length() == 0)
							|| (bdsCombo.getText().length() == 0)
							|| fileName.getText().length() == 0
							|| fileName2.getText().length() == 0
							|| folderName.getText().length() == 0) {
						logText.setText("");
						logText.append("ERROR: Please complete the form...");
						logText.setLineBackground(0, 1, Display.getCurrent()
								.getSystemColor(SWT.COLOR_RED));
					} else {
						// Si el checkbox esta seleccionado, guardo los
						// datos en el fichero de configuracion.
						if (checkBox.getSelection()) {
							Path currentRelativePath = Paths.get("");
							final File conf = new File(currentRelativePath
									.toAbsolutePath().toString()
									+ "/target.txt");
							if (conf.exists()) {
								conf.delete();
							}

							BufferedWriter bw = new BufferedWriter(
									new FileWriter(conf));
							bw.write(folderName.getText());
							bw.close();
						}

						String sourceQuery = QueryProvider.GetQuery(fileName.getText(),'F');
						String targetQuery = QueryProvider.GetQuery(fileName2.getText(),'F');
						IdentifyDialog.launch(composite.getShell(), bdsCombo.getText(),
								bdsCombo2.getText(), passSource, passTarget, userSource, userTarget, 'f', sourceQuery, targetQuery);

						try {
							if ((passSource.length() == 0)
									|| (passTarget.length() == 0)
									|| (userSource.length() == 0)
									|| (userTarget.length() == 0)
									|| (ejecucion == false)) {
								logText.setText("");
								logText.append("INFO: No has especificado credenciales.");
								logText.setLineBackground(
										logText.getLineCount() - 1,
										1,
										Display.getCurrent().getSystemColor(
												SWT.COLOR_YELLOW));
							} else {
								ejecucion = false;
								String resultsTable = Validator.validateFiles(
										fileName.getText(),
										fileName2.getText(),
										folderName.getText(),
										bdsCombo.getText(),
										bdsCombo2.getText(), userSource,
										userTarget, passSource, passTarget,
										logText);
								
								ReportDialog.launch(composite.getShell(), logText, resultsTable);
							}
						} catch (SqlInvalidAuthorizationSpecException e) {
							logText.append("\n");
							logText.append("\n");
							logText.append("ERROR: Credenciales de acceso inválidas.");
							logText.setLineBackground(
									logText.getLineCount() - 1,
									1,
									Display.getCurrent().getSystemColor(
											SWT.COLOR_RED));
						}
					}
				} catch (ClassNotFoundException | IOException e) {
					logText.append("\n");
					logText.append("\n");
					logText.append("ERROR: Algo no ha ido bien... Pruebe a revisar los campos de entrada.");
					logText.setLineBackground(logText.getLineCount() - 1, 1,
							Display.getCurrent().getSystemColor(SWT.COLOR_RED));
					// e.printStackTrace();
				} catch (SQLException e) {
					logText.append("\n");
					logText.append("\n");
					logText.append("ERROR: Algo no ha ido bien... Compruebe que la Query sea correcta.");
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
		});

		return composite;
	}
	
}
