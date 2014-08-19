package org.vidge.util;

import java.util.prefs.Preferences;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class PositionUtillity {

	private static final String Y = "y";//$NON-NLS-1$
	private static final String X = "x";//$NON-NLS-1$
	private static final String APP_LAST_POSITION = "app.last.position."; //$NON-NLS-1$
	private static final String APP_LAST_SIZE = "app.last.size."; //$NON-NLS-1$
	private static final String APP_MAXIMIZED = "app.maximized"; //$NON-NLS-1$
	private static int cx = 0, cy = 0;

	public static void initAppWindow(final Class<?> applicationClass) {
		IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (activeWorkbenchWindow != null) {
			final Shell shell = activeWorkbenchWindow.getShell();
			setShellSizeAndPosition(applicationClass, shell);
			memoPositions(applicationClass, shell);
		} else {
			System.out.println("***PositionUtillity: Window not created yet!");
		}
	}

	public static void initShell(final Class<?> windowClass, final Shell shell) {
		setShellSizeAndPosition(windowClass, shell);
		memoPositions(windowClass, shell);
	}

	public static void memoPositions(final Class<?> windowClass, final Shell shell) {
		shell.addControlListener(new ControlListener() {

			public void controlMoved(ControlEvent e) {
				try {
					if (!shell.isDisposed()) {
						Preferences.userNodeForPackage(windowClass).putInt(APP_LAST_POSITION + X, shell.getLocation().x);
						Preferences.userNodeForPackage(windowClass).putInt(APP_LAST_POSITION + Y, shell.getLocation().y);
						Preferences.userNodeForPackage(windowClass).putBoolean(APP_MAXIMIZED, shell.getMaximized());
					}
				} catch (Throwable e1) {
					e1.printStackTrace();
				}
			}

			public void controlResized(ControlEvent e) {
				try {
					if (!shell.isDisposed()) {
						Preferences.userNodeForPackage(windowClass).putInt(APP_LAST_SIZE + X, shell.getSize().x);
						Preferences.userNodeForPackage(windowClass).putInt(APP_LAST_SIZE + Y, shell.getSize().y);
						Preferences.userNodeForPackage(windowClass).putBoolean(APP_MAXIMIZED, shell.getMaximized());
					}
				} catch (Throwable e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	public static void setShellSizeAndPosition(Class<?> applicationClass, final Shell shell) {
		try {
			if (!shell.isDisposed()) {
				if (Preferences.userNodeForPackage(applicationClass).getBoolean(APP_MAXIMIZED, false)) {
					shell.setMaximized(true);
				} else {
					int width = Preferences.userNodeForPackage(applicationClass).getInt(APP_LAST_SIZE + X, 780);
					int height = Preferences.userNodeForPackage(applicationClass).getInt(APP_LAST_SIZE + Y, 500);
					shell.setSize(width, height);
					Rectangle clientArea = shell.getDisplay().getClientArea();
					int xx = Preferences.userNodeForPackage(applicationClass).getInt(APP_LAST_POSITION + X, 0);
					int yy = Preferences.userNodeForPackage(applicationClass).getInt(APP_LAST_POSITION + Y, 0);
					if (xx > clientArea.width - width || yy > clientArea.height - height || xx < 0 || yy < 0) {
						shell.setLocation(0, 0);
					} else {
						shell.setLocation(xx, yy);
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static void setShellPosition(Class<?> applicationClass, final Shell shell) {
		try {
			shell.setLocation(Preferences.userNodeForPackage(applicationClass).getInt(APP_LAST_POSITION + X, 0), //$NON-NLS-1$
				Preferences.userNodeForPackage(applicationClass).getInt(APP_LAST_POSITION + Y, 0));//$NON-NLS-1$
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static void center(Composite client) {
		try {
			Rectangle displayBounds = client.getDisplay().getBounds();
			Rectangle clientBounds = client.getBounds();
			clientBounds.x = cx + displayBounds.width / 2 - clientBounds.width / 2;
			clientBounds.y = cy + displayBounds.height / 2 - clientBounds.height / 2;
			cx += 15;
			cy += 15;
			if (cx >= 150) {
				cx = 0;
			}
			if (cy >= 150) {
				cy = 0;
			}
			client.setBounds(clientBounds);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static void centerAbs(Composite client, int width, int height, int yshift, int xshift) {
		try {
			Rectangle displayBounds = PlatformUI.getWorkbench().getDisplay().getBounds();
			Rectangle clientBounds = new Rectangle(0, 0, width, height);
			clientBounds.x = displayBounds.width / 2 - width / 2 + xshift;
			clientBounds.y = displayBounds.height / 2 - height / 2 + yshift;
			client.setBounds(clientBounds);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
