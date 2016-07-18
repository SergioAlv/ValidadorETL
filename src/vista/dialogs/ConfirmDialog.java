package vista.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import util.ExcelCreator;

public class ConfirmDialog {

	public static void launch(Shell shell, final StyledText log) {

		GridLayout gridLayout = new GridLayout(2, false);
		final Shell dialog = new Shell(shell, SWT.DIALOG_TRIM
				| SWT.APPLICATION_MODAL);
		dialog.setLayout(gridLayout);

		dialog.setText("Confirm please");
		dialog.setSize(350, 80);
		dialog.setMinimumSize(350, 80);

		final Label labelFile = new Label(dialog, SWT.NONE);
		labelFile.setText("Ya existe un archivo con este nombre. ¿Quieres reemplazarlo?");
		labelFile.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,
				true, false, 2, 1));
		
		final Button confirm = new Button(dialog, SWT.PUSH);
		confirm.setText("Confirm");
		confirm.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER | SWT.BOTTOM,
				true, false));
		
		final Button cancel = new Button(dialog, SWT.PUSH);
		cancel.setText("Cancel");
		cancel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER | SWT.BOTTOM,
				true, false));
		
		dialog.open();

		// Move the dialog to the center of the top level shell.
		Rectangle shellBounds = shell.getShell().getBounds();
		Point dialogSize = dialog.getSize();

		dialog.setLocation(shellBounds.x + (shellBounds.width - dialogSize.x)
				/ 2, shellBounds.y + (shellBounds.height - dialogSize.y) / 2);
		
		confirm.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				
				ExcelCreator.setReplaceFlag(true);
				
				dialog.dispose();
				
			}
		});
		
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				log.append("\n\n");
				log.append("INFO: Operation cancelled by user.");
				log.setLineBackground(log.getLineCount() - 1, 1,
						Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
				
				dialog.dispose();
			}
		});

		while (!dialog.isDisposed()) {
			if (!shell.getDisplay().readAndDispatch())
				shell.getDisplay().sleep();
		}

	}
	
}
