package org.vidge.controls.calendar;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class CopyOfCalendarDialog extends Dialog {

	// private CalendarChooser calendarChooser;
	private DateTime dateTime;
	private Calendar calendar;

	public CopyOfCalendarDialog(Composite parent, Point point) {
		super(parent.getShell());
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		dateTime = new DateTime(parent, SWT.CALENDAR);
		if (calendar != null) {
			dateTime.setYear(calendar.get(Calendar.YEAR));
			dateTime.setMonth(calendar.get(Calendar.MONTH));
			dateTime.setDay(calendar.get(Calendar.DAY_OF_MONTH));
		}
		return dateTime;
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
		Calendar instance = Calendar.getInstance();
		instance.set(Calendar.YEAR, dateTime.getYear());
		instance.set(Calendar.MONTH, dateTime.getMonth());
		instance.set(Calendar.DAY_OF_MONTH, dateTime.getDay());
		return instance;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
		if (dateTime != null) {
			dateTime.setYear(calendar.get(Calendar.YEAR));
			dateTime.setMonth(calendar.get(Calendar.MONTH));
			dateTime.setDay(calendar.get(Calendar.DAY_OF_MONTH));
		}
	}

	public void setDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		setCalendar(calendar);
	}

	public void addCalendarListener(final ICalendarListener listener) {
		dateTime.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				listener.dateChanged(new CalendarEvent(getCalendar().getTime()));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	public static void main(String[] args) {
		Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		Button open = new Button(shell, SWT.PUSH);
		open.setText("Open Dialog");
		open.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				final Shell dialog = new Shell(shell, SWT.DIALOG_TRIM);
				dialog.setLayout(new GridLayout(3, false));
				final DateTime calendar = new DateTime(dialog, SWT.CALENDAR | SWT.BORDER);
				final DateTime date = new DateTime(dialog, SWT.DATE | SWT.SHORT);
				final DateTime time = new DateTime(dialog, SWT.TIME | SWT.SHORT);
				new Label(dialog, SWT.NONE);
				new Label(dialog, SWT.NONE);
				Button ok = new Button(dialog, SWT.PUSH);
				ok.setText("OK");
				ok.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
				ok.addSelectionListener(new SelectionAdapter() {

					public void widgetSelected(SelectionEvent e) {
						System.out.println("Calendar date selected (MM/DD/YYYY) = " + (calendar.getMonth() + 1) + "/" + calendar.getDay() + "/" + calendar.getYear());
						System.out.println("Date selected (MM/YYYY) = " + (date.getMonth() + 1) + "/" + date.getYear());
						System.out.println("Time selected (HH:MM) = " + time.getHours() + ":" + (time.getMinutes() < 10 ? "0" : "") + time.getMinutes());
						dialog.close();
					}
				});
				dialog.setDefaultButton(ok);
				dialog.pack();
				dialog.open();
			}
		});
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
