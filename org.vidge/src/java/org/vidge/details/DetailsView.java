package org.vidge.details;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class DetailsView extends ViewPart {

	public static final String ID = "org.vidge.chunks.details.DetailsView"; //$NON-NLS-1$
	private DetailsComposite detailsComposite;
	private static DetailsView INSTANCE;

	public DetailsView() {
		INSTANCE = this;
	}

	@Override
	public void createPartControl(Composite parent) {
		detailsComposite = new DetailsComposite(parent, SWT.BORDER);
	}

	public static void setDetails(Details details) {
		if (INSTANCE != null) {
			INSTANCE.setDetailsInternal(details);
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ID);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
	}

	private void setDetailsInternal(Details details) {
		detailsComposite.setDetails(details);
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void dispose() {
		disposeInternal();
		INSTANCE = null;
		super.dispose();
	}

	private void disposeInternal() {
		detailsComposite.dispose();
	}

	public static void clear() {
		if (INSTANCE != null) {
			INSTANCE.clearInternal();
		}
	}

	public static void clear(String message) {
		if (INSTANCE != null) {
			INSTANCE.detailsComposite.clean(message);
		}
	}

	private void clearInternal() {
		detailsComposite.clean(Messages.DetailsView_NO_SELECTED_OBJECTS);
	}
}