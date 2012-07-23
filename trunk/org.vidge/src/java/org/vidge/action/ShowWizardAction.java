package org.vidge.action;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.wizards.IWizardDescriptor;

public class ShowWizardAction extends Action {

	private final IWizardDescriptor wizardDescriptor;

	public ShowWizardAction(IWizardDescriptor wizardDescriptor) {
		super(wizardDescriptor.getLabel(), wizardDescriptor.getImageDescriptor());
		this.wizardDescriptor = wizardDescriptor;
		setDescription(wizardDescriptor.getDescription());
		setToolTipText(wizardDescriptor.getLabel());
	}

	@Override
	public void run() {
	}

	public IWizardDescriptor getWizardDescriptor() {
		return wizardDescriptor;
	}
}
