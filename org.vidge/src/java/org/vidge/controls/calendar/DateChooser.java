package org.vidge.controls.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.vidge.SharedImages;
import org.vidge.VidgeResources;

public class DateChooser extends Composite {

	public static final int DATE = 1 << 90;
	public static final int TIME = 1 << 91;
	public static final int TIME_DATE = 1 << 92;
	public static final int SECONDS = 1 << 93;
	private static final int HOUR = 3;
	private static final int COUNT = 6;
	private Spinner[] controls = new Spinner[COUNT];
	private String[] labels = new String[] {
			Messages.DateChooser_day, Messages.DateChooser_month, Messages.DateChooser_year, Messages.DateChooser_hour, Messages.DateChooser_minute, "Second"
	};
	private String[] separators = new String[] {
			Messages.DateChooser_date_separator, Messages.DateChooser_date_separator, " ", Messages.DateChooser_time_separator, ":", ""
	};
	private int[] min = new int[] {
			1, 1, 2000, 0, 0, 0
	};
	private int[] max = new int[] {
			31, 12, 2050, 23, 59, 59
	};
	private int[] shift = new int[] {
			0, 1, 0, 0, 0, 0
	};
	private int[] calendarValues = new int[] {
			Calendar.DAY_OF_MONTH, Calendar.MONTH, Calendar.YEAR, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND
	};
	private Button calendarButton;
	private List<ICalendarListener> listeners = new ArrayList<ICalendarListener>();
	private int start;
	private int finish;

	public DateChooser(Composite parent, String name, int style) {
		super(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 12;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 5;
		layout.verticalSpacing = 5;
		this.setLayout(layout);
		boolean showDate = (style & DATE) != 0;
		boolean showTime = (style & TIME) != 0;
		boolean showSeconds = (style & SECONDS) != 0;
		if ((style & TIME_DATE) != 0) {
			showDate = true;
			showTime = true;
		}
		if (!showDate && !showTime && !showSeconds) {
			showDate = true;
		}
		if (showDate) {
			start = 0;
			if (showTime) {
				if (showSeconds) {
					finish = COUNT;
				} else {
					finish = COUNT - 1;
				}
			} else {
				finish = HOUR;
			}
		} else {
			if (showTime) {
				start = HOUR;
				if (showSeconds) {
					finish = COUNT;
				} else {
					finish = COUNT - 1;
				}
			} else {
				if (showSeconds) {
					start = COUNT - 1;
					finish = COUNT;
				} else {
					finish = HOUR;
				}
			}
		}
		if (name != null && name.length() > 0) {
			new Label(this, SWT.NONE).setText(name);
		}
		setUpControls();
		if (showDate) {
			makeCalendarButton(this);
		}
		this.pack();
		setDate(new Date());
	}

	private void setUpControls() {
		Calendar now = Calendar.getInstance();
		for (int i = start; i < finish; i++) {
			Spinner current = new Spinner(this, SWT.BORDER);
			controls[i] = current;
			controls[i].addKeyListener(new KeyListener() {

				@Override
				public void keyReleased(KeyEvent e) {
				}

				@Override
				public void keyPressed(KeyEvent e) {
					e.doit = false;
				}
			});
			current.setSelection(now.get(calendarValues[i]) + shift[i]);
			current.setData(calendarValues[i]);
			current.setToolTipText(labels[i]);
			current.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			current.setMinimum(min[i]);
			current.setMaximum(max[i]);
			current.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					fireListenerChanged();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
			if (i < (finish - 1)) {
				new Label(this, SWT.NONE).setText(separators[i]);
			}
		}
	}

	public DateChooser(Composite parent, int style) {
		this(parent, null, style);
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

	private void makeCalendarButton(final Composite parent) {
		calendarButton = new Button(parent, SWT.PUSH);
		calendarButton.setImage(VidgeResources.getInstance().getImage(SharedImages.CALENDAR));
		calendarButton.setLayoutData(new GridData());
		calendarButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				final CalendarDialog calendarDialog = new CalendarDialog(parent, calendarButton.getLocation());
				calendarDialog.setCalendar(getCalendar());
				if (calendarDialog.open() == IDialogConstants.OK_ID) {
					setCalendar(calendarDialog.getCalendar());
				}
			}
		});
	}

	public Calendar getCalendar() {
		Calendar calendar = Calendar.getInstance();
		for (int a = 0; a < calendarValues.length; a++) {
			Integer val = calendarValues[a];
			if (a >= start && a < finish) {
				for (int b = 0; b < controls.length; b++) {
					if (controls[b] != null && controls[b].getData().equals(val)) {
						calendar.set(calendarValues[a], controls[b].getSelection() - shift[a]);
					}
				}
			} else {
				calendar.set(calendarValues[a], 0 - shift[a]);
			}
		}
		return calendar;
	}

	public Date getDate() {
		return getCalendar().getTime();
	}

	public void setCalendar(Calendar calendar) {
		checkWidget();
		if (calendar != null) {
			for (int i = 0; i < controls.length; i++) {
				if (controls[i] != null) {
					controls[i].setSelection(calendar.get(calendarValues[i]) + shift[i]);
				}
			}
			fireListenerChanged();
		}
	}

	public void setDate(Date date) {
		if (date != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			setCalendar(calendar);
		}
	}

	private void fireListenerChanged() {
		Event event = new Event();
		event.widget = this;
		event.display = getDisplay();
		event.time = (int) System.currentTimeMillis();
		event.data = getCalendar();
		for (ICalendarListener listener : listeners) {
			listener.dateChanged(new CalendarEvent(event));
		}
	}

	public void addCalendarListener(ICalendarListener listener) {
		listeners.add(listener);
	}

	public void removeCalendarListener(ModifyListener listener) {
		listeners.remove(listener);
	}
}
