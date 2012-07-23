package org.vidge.controls.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.vidge.SharedImages;
import org.vidge.VidgeResources;

public class DateChooser extends Composite {

	public static final int DATE = 1 << 90;
	public static final int TIME = 1 << 91;
	public static final int TIME_DATE = 1 << 92;
	private static final int DAY = 0;
	private static final int MONTH = 1;
	private static final int YEAR = 2;
	private static final int HOUR = 3;
	private static final int MINUTE = 4;
	private static final int COUNT = 5;
	private Spinner[] controls = new Spinner[COUNT];
	private String[] labels = new String[] {
			Messages.DateChooser_day, Messages.DateChooser_month, Messages.DateChooser_year, Messages.DateChooser_hour, Messages.DateChooser_minute
	};
	private String[] separators = new String[] {
			Messages.DateChooser_date_separator, Messages.DateChooser_date_separator, " ", Messages.DateChooser_time_separator, ""}; //$NON-NLS-3$ //$NON-NLS-5$
	private int[] min = new int[] {
			1, 1, 2005, 0, 0
	};
	private int[] max = new int[] {
			31, 12, 2100, 23, 59
	};
	private int[] shift = new int[] {
			0, 1, 0, 0, 0
	};
	private int[] calendarValues = new int[] {
			Calendar.DAY_OF_MONTH, Calendar.MONTH, Calendar.YEAR, Calendar.HOUR_OF_DAY, Calendar.MINUTE
	};
	private Button calendarButton;
	private List<ICalendarListener> listeners = new ArrayList<ICalendarListener>();
	private int start;
	private int finish;

	public DateChooser(Composite parent, String name, int style) {
		super(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 11;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 5;
		layout.verticalSpacing = 5;
		this.setLayout(layout);
		boolean showDate = (style & DATE) != 0;
		boolean showTime = (style & TIME) != 0;
		if ((style & TIME_DATE) != 0) {
			showDate = true;
			showTime = true;
		}
		if (!showDate && !showTime) {
			showDate = true;
		}
		start = (showDate ? 0 : HOUR);
		finish = (showTime ? COUNT : HOUR);
		if (name != null && name.length() > 0) {
			new Label(this, SWT.NONE).setText(name);
		}
		for (int i = start; i < finish; i++) {
			controls[i] = new Spinner(this, SWT.BORDER);
			if (i < (finish - 1)) {
				new Label(this, SWT.NONE).setText(separators[i]);
			}
		}
		Calendar now = Calendar.getInstance();
		for (int i = start; i < finish; i++) {
			setupControl(i);
			controls[i].setSelection(now.get(calendarValues[i]) + shift[i]);
		}
		if (showDate) {
			makeCalendarButton(this);
		}
		this.pack();
	}

	public DateChooser(Composite parent, int style) {
		this(parent, null, style);
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

	protected void setupControl(int i) {
		final Spinner current = controls[i];
		current.setToolTipText(labels[i]);
		current.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		current.setMinimum(min[i]);
		current.setMaximum(max[i]);
		current.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				fireListenerChanged();
			}
		});
	}

	public Calendar getCalendar() {
		Calendar calendar = Calendar.getInstance();
		for (int a = 0; a < calendarValues.length; a++) {
			if (a >= start && a < finish) {
				calendar.set(calendarValues[a], controls[a].getSelection() - shift[a]);
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
			fireListenerChanged();
			for (int i = 0; i < controls.length; i++) {
				if (controls[i] != null) {
					controls[i].setSelection(calendar.get(calendarValues[i]) + shift[i]);
				}
			}
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
