package org.vidge.action;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.IViewDescriptor;

public class ShowViewAction extends Action {

	private final IViewDescriptor viewDescriptor;

	public ShowViewAction(IViewDescriptor viewDescriptor) {
		super(viewDescriptor.getLabel(), viewDescriptor.getImageDescriptor());
		this.viewDescriptor = viewDescriptor;
		setDescription(viewDescriptor.getDescription());
	}

	@Override
	public void run() {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
				viewDescriptor.getId());
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

}
