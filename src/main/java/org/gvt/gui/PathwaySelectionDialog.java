package org.gvt.gui;

/**
 *
 */

import org.biopax.paxtools.model.Model;
import org.biopax.paxtools.model.level3.Pathway;
import org.biopax.paxtools.model.level3.Process;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.util.*;
import java.util.List;

public class PathwaySelectionDialog extends Dialog
{
	Shell shell;
	Model model;
	List<String> selected;
	private boolean pressedCancel;

	public PathwaySelectionDialog(Shell shell, Model model, List<String> selected)
	{
		super(shell, SWT.MODELESS);
		this.model = model;
		this.selected = selected;
		this.pressedCancel = false;
	}

	public void open()
	{
		pressedCancel = true;
		createContents();

		shell.setText("Select pathways to display");
		shell.pack();
		if (shell.getSize().y > 500) shell.setSize(new Point(shell.getSize().x, 500));

		shell.setLocation(
			getParent().getLocation().x + (getParent().getSize().x / 2) -
				(shell.getSize().x / 2),
			getParent().getLocation().y + (getParent().getSize().y / 2) -
				(shell.getSize().y / 2));

		shell.open();
		Display display = getParent().getDisplay();

		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch()) display.sleep();
		}
	}

	private void createContents()
	{
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.RESIZE);

		shell.setLayout(new FillLayout());
		Composite parent = new Composite(shell, SWT.BORDER);
		GridLayout grid = new GridLayout(2, true);
		parent.setLayout(grid);

		final Tree tree = new Tree(parent,
			SWT.MULTI | SWT.CHECK | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		fillTree(tree);

		setChecks(tree, selected);

		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		tree.setLayoutData(data);

		Button okButton = new Button(parent, SWT.PUSH);
		okButton.setText("OK");
		okButton.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event event)
			{
				Set<TreeItem> checked = getChecked(tree);
				selected.clear();
				for (TreeItem item : checked)
				{
					selected.add(item.getText());
				}

				pressedCancel = false;
				shell.dispose();
			}
		});
		data = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		okButton.setLayoutData(data);

		Button cancelButton = new Button(parent, SWT.PUSH);
		cancelButton.setText("Cancel");
		cancelButton.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event event)
			{
				shell.dispose();
			}
		});
		data = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		cancelButton.setLayoutData(data);
	}

	private void fillTree(Tree tree)
	{
		for (Pathway p : model.getObjects(Pathway.class))
		{
			if (p.getPathwayComponent().isEmpty()) continue;

			TreeItem item = new TreeItem(tree, SWT.NONE);
			item.setText(p.getDisplayName());
			handleChildren(p, item);
		}
	}

	private void handleChildren(Pathway p, TreeItem parent)
	{
		for (Process prc : p.getPathwayComponent())
		{
			if (prc instanceof Pathway)
			{
				Pathway c = (Pathway) prc;
				TreeItem item = new TreeItem(parent, SWT.NONE);
				item.setText(c.getDisplayName());
				handleChildren(c, item);
			}
		}
	}

	private void setChecks(Tree tree, List<String> selected)
	{
		Set<String> set = new HashSet<String>();
		set.addAll(selected);
		for (TreeItem item : tree.getItems())
		{
			setCheckedRecursive(item, set);
		}
	}
	
	private void setCheckedRecursive(TreeItem item, Set<String> selected)
	{
		item.setChecked(selected.contains(item.getText()));

		for (TreeItem child : item.getItems())
		{
			setCheckedRecursive(child, selected);
		}
	}

	/**
	 * Since ok button only disposes the dialog, we need a mechanism to understand if cancel is
	 * pressed.
	 * @return
	 */
	public boolean isCancelled()
	{
		return pressedCancel;
	}
	
	private Set<TreeItem> getChecked(Tree tree)
	{
		Set<TreeItem> set = new HashSet<TreeItem>();
		for (TreeItem item : tree.getItems())
		{
			collectChecked(item, set);
		}
		return set;
	}

	private void collectChecked(TreeItem item, Set<TreeItem> set)
	{
		if (item.getChecked()) set.add(item);
		for (TreeItem child : item.getItems())
		{
			collectChecked(child, set);
		}
	}
}
