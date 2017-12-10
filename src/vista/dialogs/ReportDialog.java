package vista.dialogs;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
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
import org.eclipse.swt.widgets.Text;

import controlador.ReportGenerator;

public class ReportDialog {

	private static Map<String, String> transformations;
	
	public static void setTransformations(Map<String, String> trans) {
		transformations = trans;
	}
	
	public static void launch(final Shell shell, final StyledText log, final String resultsTable) {

		GridLayout gridLayout = new GridLayout(2, false);
		final Shell dialog = new Shell(shell, SWT.DIALOG_TRIM
				| SWT.APPLICATION_MODAL);
		dialog.setLayout(gridLayout);

		dialog.setText("Generate Report...");
		dialog.setSize(377, 110);
		dialog.setMinimumSize(377, 110);

		// GROUP PARA EL NOMBRE DEL REPORT
		Group reportName = new Group(dialog, SWT.SHADOW_IN);
		reportName.setText("Report");
		reportName.setLayout(new GridLayout(2, false));
		reportName.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT,
				true, false, 2, 1));

		final Label labelFile = new Label(reportName, SWT.NONE);
		labelFile.setText("File name:");
		final Text fileName = new Text(reportName, SWT.BORDER);
		fileName.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
		
		final Button generate = new Button(dialog, SWT.PUSH);
		generate.setText("Generate");
		generate.setLayoutData(new GridData(SWT.CENTER, SWT.DEFAULT,
				true, false));
		
		final Button cancel = new Button(dialog, SWT.PUSH);
		cancel.setText("No Thanks");
		cancel.setLayoutData(new GridData(SWT.CENTER, SWT.DEFAULT,
				true, false));


		dialog.open();

		// Move the dialog to the center of the top level shell.
		Rectangle shellBounds = shell.getShell().getBounds();
		Point dialogSize = dialog.getSize();

		dialog.setLocation(shellBounds.x + (shellBounds.width - dialogSize.x)
				/ 2, shellBounds.y + (shellBounds.height - dialogSize.y) / 2);
		
		generate.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				
				if (fileName.getText()=="") { //TODO si el nombre esta vacio, no si el texto es "" --> or (fileName.getText()=="\s") o algo asi
					fileName.setText("report");
				}
				
				ReportGenerator.generateReport(shell, log, resultsTable, fileName.getText(), transformations);
				
				dialog.dispose();
			}
		});
		
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				dialog.dispose();
			}
		});

		while (!dialog.isDisposed()) {
			if (!shell.getDisplay().readAndDispatch())
				shell.getDisplay().sleep();
		}

	}
	
}
