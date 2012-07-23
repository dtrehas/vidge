package org.vidge.controls;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.vidge.VidgeResources;
import org.vidge.SharedImages;
import org.vidge.controls.calendar.CalendarDialog;

public class DateEditor extends Composite {

	private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(" dd.MM.yyyy "); //$NON-NLS-1$
	private Calendar calendar = Calendar.getInstance();
	private Text text;
	private CustomButton calendarButton;
	private CustomButton clearButton;
	private Date date;

	public DateEditor(Composite parent, int style) {
		super(parent, SWT.BORDER);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		this.setLayout(layout);
		text = new Text(this, SWT.NONE);
		text.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				e.doit = false;
			}

			public void keyReleased(KeyEvent e) {
				e.doit = false;
				if (Character.isDigit(e.keyCode) || Character.isLetter(e.keyCode) || Character.isSpaceChar(e.keyCode)) {
					showDialog();
				}
			}
		});
		text.setLayoutData(new GridData(GridData.FILL_BOTH));
		makeCalendarButton(this);
		makeClearButton(this);
		setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	protected void showDialog() {
		final CalendarDialog calendarDialog = new CalendarDialog(this, calendarButton.getLocation());
		if (calendarDialog.open() == IDialogConstants.OK_ID) {
			setCalendar(calendarDialog.getCalendar());
		}
		calendarButton.relax();
	}

	private void makeClearButton(Composite parent) {
		clearButton = new CustomButton(this, VidgeResources.getInstance().getImage(SharedImages.DELETE), "Delete");
		clearButton.setLayoutData(new GridData());
		clearButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				date = null;
				// fireChangeEvent();
				refreshText(date);
			}
		});
	}

	protected void refreshText(Object object) {
		if (object == null) {
			text.setText(""); //$NON-NLS-1$
		} else {
			text.setText(DATE_FORMAT.format(object));
		}
	}

	private void makeCalendarButton(final Composite parent) {
		calendarButton = new CustomButton(this, VidgeResources.getInstance().getImage(SharedImages.ACTION_PLUS2), "Select");
		calendarButton.setLayoutData(new GridData());
		calendarButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				showDialog();
			}
		});
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public Date getDate() {
		return date;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
		this.date = calendar.getTime();
		refreshText(date);
	}

	public void setDate(Date date) {
		this.date = date;
		if (date != null) {
			calendar.setTime(date);
		}
		refreshText(date);
	}

	public void addModifyListener(ModifyListener listener) {
		// super.addListener(SWT.Modify, new TypedListener(listener));
		text.addModifyListener(listener);
	}

	public void removeModifyListener(ModifyListener listener) {
		// super.removeListener(SWT.Modify, new TypedListener(listener));
		text.removeModifyListener(listener);
	}

	public void removeFocusListener(FocusListener listener) {
		text.removeFocusListener(listener);
	}

	public void addHelpListener(HelpListener listener) {
		text.addHelpListener(listener);
	}

	public void removeHelpListener(HelpListener listener) {
		text.removeHelpListener(listener);
	}

	public void addKeyListener(KeyListener listener) {
		text.addKeyListener(listener);
	}

	public void removeKeyListener(KeyListener listener) {
		text.removeKeyListener(listener);
	}

	public void addMouseListener(MouseListener listener) {
		text.addMouseListener(listener);
	}

	public void removeMouseListener(MouseListener listener) {
		text.removeMouseListener(listener);
	}

	public void addMouseMoveListener(MouseMoveListener listener) {
		text.addMouseMoveListener(listener);
	}

	public void removeMouseMoveListener(MouseMoveListener listener) {
		text.removeMouseMoveListener(listener);
	}

	public void addMouseTrackListener(MouseTrackListener listener) {
		text.addMouseTrackListener(listener);
	}

	public void removeMouseTrackListener(MouseTrackListener listener) {
		text.removeMouseTrackListener(listener);
	}

	public void addSelectionListener(SelectionListener listener) {
		text.addSelectionListener(listener);
	}

	public void removeSelectionListener(SelectionListener listener) {
		text.removeSelectionListener(listener);
	}

	public void addTraverseListener(TraverseListener listener) {
		text.addTraverseListener(listener);
	}

	public void removeTraverseListener(TraverseListener listener) {
		text.removeTraverseListener(listener);
	}

	public boolean forceFocus() {
		return text.forceFocus();
	}

	public boolean isFocusControl() {
		return text.isFocusControl();
	}

	public boolean setFocus() {
		return text.setFocus();
	}
}
