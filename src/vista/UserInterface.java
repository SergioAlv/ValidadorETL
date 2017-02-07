package vista;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import vista.tabs.FileTab;
import vista.tabs.QueryTab;

public class UserInterface {

	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display, SWT.SHELL_TRIM & (~SWT.RESIZE)
				& (~SWT.MAX));
		shell.setLayout(new FillLayout());
		shell.setText("Validador ETL");
		shell.setSize(1000, 800);
		shell.setMinimumSize(1000, 800);

		
		
		// TODO Hacer un menu donde poder incorporar conexiones con los distintos servidores.
		// TODO En este menu se debe poder poner la url de conexión (puerto, servidor...) y tambien el tipo de BBDD para darle un controlador u otro
		
		
		
		final TabFolder tabFolder = new TabFolder(shell, SWT.NONE);

		TabItem text = new TabItem(tabFolder, SWT.NONE);
		text.setText("Process Queries");
		text.setControl(QueryTab.getTabQueryControl(tabFolder));

		TabItem archive = new TabItem(tabFolder, SWT.NONE);
		archive.setText("Process Files");
		archive.setControl(FileTab.getTabFileControl(tabFolder));
		
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

}
