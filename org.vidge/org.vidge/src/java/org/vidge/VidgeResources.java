package org.vidge;

import java.util.logging.Logger;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

public class VidgeResources {

	private ImageRegistry imr;
	private static VidgeResources instance = new VidgeResources();

	private VidgeResources() {
		initializeImageRegistry();
	}

	public void initializeImageRegistry() {
		imr = Activator.getDefault().getImageRegistry();
		registerImage(SharedImages.DEFAULT, "database.gif"); //$NON-NLS-1$
		registerImage(SharedImages.FILTER, "filter-2.gif"); //$NON-NLS-1$
		registerImage(SharedImages.LAYOUT_HORIZONTAL, "th_horizontal.gif"); //$NON-NLS-1$
		registerImage(SharedImages.LAYOUT_VERTICAL, "th_vertical.gif"); //$NON-NLS-1$
		registerImage(SharedImages.TABLE_HEADER_DEFAULT, "table_header_default.gif"); //$NON-NLS-1$
		registerImage(SharedImages.TABLE_HEADER_DOWN, "table_header_down.gif"); //$NON-NLS-1$
		registerImage(SharedImages.TABLE_HEADER_UP, "table_header_up.gif"); //$NON-NLS-1$
		registerImage(SharedImages.ACTION_REFRESH, "refresh.gif"); //$NON-NLS-1$
		registerImage(SharedImages.ACTION_EDIT, "hammer.gif"); //$NON-NLS-1$
		registerImage(SharedImages.RED_SQUARE, "red.gif"); //$NON-NLS-1$
		registerImage(SharedImages.ACTION_PLUS, "plus.gif"); //$NON-NLS-1$
		registerImage(SharedImages.ACTION_MINUS, "minus.gif"); //$NON-NLS-1$
		registerImage(SharedImages.BEGIN, "begin.gif");//$NON-NLS-1$
		registerImage(SharedImages.FIRST, "db-first.gif"); //$NON-NLS-1$
		registerImage(SharedImages.LAST, "db-last.gif"); //$NON-NLS-1$
		registerImage(SharedImages.NEXT, "db-next.gif"); //$NON-NLS-1$
		registerImage(SharedImages.PREVIOUS, "db-previous.gif"); //$NON-NLS-1$
		registerImage(SharedImages.CANCEL, "db-cancel.gif"); //$NON-NLS-1$
		registerImage(SharedImages.SEARCH, "search.gif"); //$NON-NLS-1$
		registerImage(SharedImages.DEFAULT_NODE, "blue.gif"); //$NON-NLS-1$
		registerImage(SharedImages.ACTION_PLUS2, "plus2.gif"); //$NON-NLS-1$
		registerImage(SharedImages.DELETE, "delete.gif"); //$NON-NLS-1$
		registerImage(SharedImages.SQUARE, "square.gif"); //$NON-NLS-1$
		registerImage(SharedImages.TREE_EXP, "TreeExp.gif"); //$NON-NLS-1$
		registerImage(SharedImages.CARDFILE, "card-file.gif"); //$NON-NLS-1$
		registerImage(SharedImages.OBJECTS, "objects.gif"); //$NON-NLS-1$
		registerImage(SharedImages.BSQUARE, "blue-square-button.gif"); //$NON-NLS-1$
		registerImage(SharedImages.BALL, "ball.png"); //$NON-NLS-1$
		registerImage(SharedImages.BARCHART, "bar-chart.gif"); //$NON-NLS-1$
		registerImage(SharedImages.DBADD, "database-add.gif"); //$NON-NLS-1$
		registerImage(SharedImages.DBREM, "database-remove.gif"); //$NON-NLS-1$
		registerImage(SharedImages.DBEDT, "database-edit.gif"); //$NON-NLS-1$
		registerImage(SharedImages.OPEN, "open.gif"); //$NON-NLS-1$
		registerImage(SharedImages.LOCK, "lock.gif"); //$NON-NLS-1$
		registerImage(SharedImages.CLOCK, "clock-2.gif"); //$NON-NLS-1$
		registerImage(SharedImages.CALENDAR, "calendar.gif"); //$NON-NLS-1$
		registerImage(SharedImages.TICK, "tick.gif");
		registerImage(SharedImages.FOLDER, "folder.gif");
	}

	private void registerImage(SharedImages imageName, String fileName) {
		try {
			ImageDescriptor dsc = Activator.imageDescriptorFromPlugin(Activator.ID, "icons/" + fileName);
			if (dsc == null) {
				Logger.getLogger("Vidge").info("--------Image not found:--------  " + imageName + "/" + fileName);
				return;
			}
			imr.put(imageName.name(), dsc);
		} catch (Exception e) {
			new VidgeException(e);
		}
	}

	public Image getImage(SharedImages image) {
		if (image == SharedImages.NONE) {
			return null;
		}
		return imr.get(image.name());
	}

	public ImageDescriptor getImageDescriptor(SharedImages image) {
		return imr.getDescriptor(image.name());
	}

	public static VidgeResources getInstance() {
		return instance;
	}
}
