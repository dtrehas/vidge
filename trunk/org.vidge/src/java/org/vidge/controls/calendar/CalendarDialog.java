package org.vidge.controls.calendar;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class CalendarDialog extends Dialog {

	private CalendarChooser calendarChooser;
	private Calendar calendar;

	public CalendarDialog(Composite parent, Point point) {
		super(parent.getShell());
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		calendarChooser = new CalendarChooser(parent, SWT.BORDER);
		calendarChooser.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.ESC || e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					CalendarDialog.this.okPressed();
				}
			}
		});
		calendarChooser.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				CalendarDialog.this.okPressed();
			}
		});
		if (calendar != null) {
			calendarChooser.setCalendar(calendar);
		}
		return calendarChooser;
	}

	@Override
	protected Control createContents(Composite parent) {
		// parent.setSize(200,150);
		// PositionUtillity.center(parent);
		Control control = super.createContents(parent);
		Button buttonOk = getButton(IDialogConstants.OK_ID);
		buttonOk.addMouseListener(new MouseListener() {

			public void mouseDoubleClick(MouseEvent e) {
				okPressed();
			}

			public void mouseDown(MouseEvent e) {
			}

			public void mouseUp(MouseEvent e) {
				okPressed();
			}
		});
		return control;
	}

	public Calendar getCalendar() {
		return calendarChooser.getCalendar();
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
		if (calendarChooser != null) {
			calendarChooser.setCalendar(calendar);
		}
	}

	public void setDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		setCalendar(calendar);
	}

	public void addCalendarListener(ICalendarListener listener) {
		calendarChooser.addCalendarListener(listener);
	}

	public void removeCalendarListener(ICalendarListener listener) {
		calendarChooser.removeCalendarListener(listener);
	}
}
