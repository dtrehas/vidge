package org.vidge.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class CaptureUtil {

	public static void capture(Control control) {
		GC gc = new GC(control);
		Rectangle bounds = control.getBounds();
		final Image image = new Image(control.getDisplay(), bounds);
		gc.copyArea(image, bounds.x, bounds.y);
		gc.dispose();
		Shell popup = new Shell(control.getShell());
		popup.setText("Captured Image");
		popup.addListener(SWT.Close, new Listener() {

			public void handleEvent(Event e) {
				image.dispose();
			}
		});
		Canvas canvas = new Canvas(popup, SWT.NONE);
		canvas.setBounds(0, 0, image.getImageData().width, image.getImageData().height);
		canvas.addPaintListener(new PaintListener() {

			public void paintControl(PaintEvent e) {
				e.gc.drawImage(image, 0, 0);
			}
		});
		popup.pack();
		popup.open();
	}
}
