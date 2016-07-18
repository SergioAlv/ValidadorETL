package vista.tabs;

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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;

import util.ResourcesHandler;
import vista.dialogs.IdentifyDialog;
import vista.dialogs.ReportDialog;

import com.ibm.db2.jcc.am.SqlInvalidAuthorizationSpecException;

import controlador.Validator;

public class QueryTab {
	
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
	
	public static Control getTabQueryControl(TabFolder tabFolder) {
		final Composite composite = new Composite(tabFolder, SWT.NONE);
		GridLayout gridLayout = new GridLayout(3, false);
		composite.setLayout(gridLayout);

		// CONTROL PARA DATABASE SOURCE
		Group databaseSource = new Group(composite, SWT.HORIZONTAL);
		databaseSource.setText("Select DB Source");
		databaseSource.setLayout(gridLayout);
		databaseSource.setLayoutData(new GridData(SWT.CENTER, SWT.DEFAULT,
				true, false));

		final Combo bdsCombo = new Combo(databaseSource, SWT.DROP_DOWN);
		bdsCombo.setItems(ResourcesHandler.BDs);

		// SEPARADOR
		@SuppressWarnings("unused")
		final Label label1 = new Label(composite, SWT.NONE);

		// CONTROL PARA DATABASE TARGET
		Group databaseTarget = new Group(composite, SWT.HORIZONTAL);
		databaseTarget.setText("Select DB Target");
		databaseTarget.setLayout(gridLayout);
		databaseTarget.setLayoutData(new GridData(SWT.CENTER, SWT.DEFAULT,
				true, false));

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

		// QUERY SOURCE
		Group querySource = new Group(composite, SWT.HORIZONTAL);
		querySource.setText("Source Query");
		querySource.setLayout(gridLayout);
		querySource.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 5));

		final StyledText source = new StyledText(querySource, SWT.BORDER
				| SWT.V_SCROLL | SWT.H_SCROLL);
		source.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 5));
		
		// BOTON DE VALIDACIÓN
		final Button validate = new Button(composite, SWT.PUSH);
		validate.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		validate.setText("VALIDATE");

		// QUERY TARGET
		Group queryTarget = new Group(composite, SWT.HORIZONTAL);
		queryTarget.setText("Target Query");
		queryTarget.setLayout(gridLayout);
		queryTarget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 5));

		final StyledText target = new StyledText(queryTarget, SWT.BORDER
				| SWT.V_SCROLL | SWT.H_SCROLL);
		target.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 5));
		
		// SEPARADOR
		final Label label2 = new Label(composite, SWT.NONE);
		label2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false,
				3, 1));

		// CUADRO DE LOG
		final StyledText logText = new StyledText(composite, SWT.BORDER //PODEMOS INTENTAR ENMARCARLO EN UN GROUP PARA QUE NO DESAPAREZCA AL AUMENTAR LOS STYLEDTEXT
				| SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL);
		logText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 0));
		logText.addListener(SWT.Modify, new Listener() {
					public void handleEvent(Event e) {
						logText.setTopIndex(logText.getLineCount() - 1);
					}
				});
		
		validate.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					if ((bdsCombo2.getText().length() == 0)
							|| (bdsCombo.getText().length() == 0)
							|| source.getText().length() == 0
							|| target.getText().length() == 0) {
						logText.setText("");
						logText.append("ERROR: Please complete the form...");
						logText.setLineBackground(0, 1, Display.getCurrent()
								.getSystemColor(SWT.COLOR_RED));
					} else {

						IdentifyDialog.launch(composite.getShell(), bdsCombo.getText(),
								bdsCombo2.getText(), passSource, passTarget, userSource, userTarget, 'Q');
						
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
								String resultsTable = Validator.validateQueries(
										source.getText(), target.getText(),
										bdsCombo.getText(),
										bdsCombo2.getText(), userSource,
										userTarget, passSource, passTarget,
										logText);
								
								//Ventana de dialogo "Quiere generar un informe de errores?" y que pida el nombre del fichero. Comprueba el campo "Validacion" de la primera tabla resultados.
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
				} catch (ClassNotFoundException e) {
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
					// e.printStackTrace();
				}
			}
		});

		return composite;
	}
	
}
