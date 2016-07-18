package vista;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import vista.tabs.FileTab;
import vista.tabs.FolderTab;
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

		final TabFolder tabFolder = new TabFolder(shell, SWT.NONE);

		TabItem text = new TabItem(tabFolder, SWT.NONE);
		text.setText("Process Queries");
		text.setControl(QueryTab.getTabQueryControl(tabFolder));

		TabItem archive = new TabItem(tabFolder, SWT.NONE);
		archive.setText("Process Files");
		archive.setControl(FileTab.getTabFileControl(tabFolder));

		TabItem folder = new TabItem(tabFolder, SWT.NONE);
		folder.setText("Process Folder");
		folder.setControl(FolderTab.getTabFolderControl(tabFolder));
		
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

}
