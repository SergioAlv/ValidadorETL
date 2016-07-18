package vista.dialogs;

import org.eclipse.swt.SWT;
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

import vista.tabs.FileTab;
import vista.tabs.FolderTab;
import vista.tabs.QueryTab;

public class IdentifyDialog {

	public static void launch(Shell shell, String src, String trg,
			String passSource, String passTarget, String userSource,
			String userTarget, final char tab) {

		GridLayout gridLayout = new GridLayout(3, false);
		final Shell dialog = new Shell(shell, SWT.DIALOG_TRIM
				| SWT.APPLICATION_MODAL);
		dialog.setLayout(gridLayout);

		dialog.setText("Login...");
		dialog.setSize(377, 110);
		dialog.setMinimumSize(377, 110);

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
				} else if (tab == 'F') {
					FolderTab.setCredentials(pass.getText(), pass2.getText(),
							userName.getText(), userName2.getText(), true);
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
