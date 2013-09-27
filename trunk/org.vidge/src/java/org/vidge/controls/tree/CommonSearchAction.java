package org.vidge.controls.tree;

import org.eclipse.jface.action.Action;
import org.vidge.SharedImages;
import org.vidge.VidgeResources;


public class CommonSearchAction extends Action{

	public CommonSearchAction() {
		super(Messages.CommonSearchAction_0,VidgeResources.getInstance().getImageDescriptor(SharedImages.SEARCH));
		setToolTipText(Messages.CommonSearchAction_0);
		setDescription(Messages.CommonSearchAction_0);
	}

}
