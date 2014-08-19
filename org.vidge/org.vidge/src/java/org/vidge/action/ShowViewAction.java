package org.vidge.action;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.IViewDescriptor;

public class ShowViewAction extends Action {

	private final IViewDescriptor viewDescriptor;
	protected IViewPart showView;

	public ShowViewAction(IViewDescriptor viewDescriptor) {
		super(viewDescriptor.getLabel(), viewDescriptor.getImageDescriptor());
		this.viewDescriptor = viewDescriptor;
		// setToolTipText(viewDescriptor.getDescription());
		setDescription(viewDescriptor.getDescription());
	}

	@Override
	public void run() {
		try {
			showView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(viewDescriptor.getId());
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	public IViewDescriptor getViewDescriptor() {
		return viewDescriptor;
	}

	public IViewPart getShowView() {
		return showView;
	}
}
