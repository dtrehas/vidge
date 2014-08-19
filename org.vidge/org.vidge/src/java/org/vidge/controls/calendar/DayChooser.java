package org.vidge.controls.calendar;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;
import org.vidge.util.StringUtil;

public class DayChooser extends Composite implements MouseListener, FocusListener, TraverseListener, KeyListener {

	public static final int RED_SUNDAY = 1 << 24; // == SWT.EMBEDDED
	public static final int RED_SATURDAY = 1 << 28; // == SWT.VIRTUAL
	public static final int RED_WEEKEND = RED_SATURDAY | RED_SUNDAY;
	private Label[] dayTitles;
	private DayControl[] days;
	private int dayOffset;
	private Color activeSelectionBackground;
	private Color inactiveSelectionBackground;
	private Color activeSelectionForeground;
	private Color inactiveSelectionForeground;
	private Color otherMonthColor;
	private Calendar calendar;
	private Calendar today;
	private Locale locale;
	private List<ICalendarListener> listeners;
	private int style;

	public DayChooser(final Composite parent, int style) {
		super(parent, style);
		this.style = style | RED_WEEKEND;
		listeners = new ArrayList<ICalendarListener>(3);
		setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		otherMonthColor = new Color(getDisplay(), 128, 128, 128);
		activeSelectionBackground = getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION);
		inactiveSelectionBackground = getDisplay().getSystemColor(SWT.COLOR_GRAY);
		activeSelectionForeground = getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT);
		inactiveSelectionForeground = getForeground();
		locale = Locale.getDefault();
		GridLayout gridLayout = new GridLayout();
		gridLayout.makeColumnsEqualWidth = true;
		gridLayout.numColumns = 7;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		setLayout(gridLayout);
		dayTitles = new Label[7];
		for (int i = 0; i < dayTitles.length; i++) {
			Label label = new Label(this, SWT.CENTER);
			dayTitles[i] = label;
			label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
			label.addMouseListener(this);
			label.addKeyListener(this);
		}
		{
			final Composite spacer = new Composite(this, SWT.NO_FOCUS);
			spacer.setBackground(getBackground());
			final GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.heightHint = 2;
			gridData.horizontalSpan = 7;
			spacer.setLayoutData(gridData);
			spacer.setLayout(new GridLayout());
			spacer.addMouseListener(this);
		}
		{
			final Label label = new Label(this, SWT.HORIZONTAL | SWT.SEPARATOR);
			final GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = 7;
			label.setLayoutData(gridData);
		}
		days = new DayControl[42];
		for (int i = 0; i < days.length; i++) {
			DayControl day = new DayControl(this);
			days[i] = day;
			day.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			day.addMouseListener(this);
			day.addKeyListener(this);
		}
		setTabList(new Control[0]);
		setFont(parent.getFont());
		init();
		addMouseListener(this);
		addFocusListener(this);
		addTraverseListener(this);
		addKeyListener(this);
		addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent event) {
				otherMonthColor.dispose();
			}
		});
	}

	protected void init() {
		calendar = Calendar.getInstance(locale);
		calendar.setLenient(true);
		today = (Calendar) calendar.clone();
		int firstDayOfWeek = calendar.getFirstDayOfWeek();
		DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(locale);
		String[] dayNames = dateFormatSymbols.getShortWeekdays();
		int minLength = Integer.MAX_VALUE;
		for (int i = 0; i < dayNames.length; i++) {
			int len = dayNames[i].length();
			if (len > 0 && len < minLength) {
				minLength = len;
			}
		}
		if (minLength > 2) {
			for (int i = 0; i < dayNames.length; i++) {
				if (dayNames[i].length() > 0) {
					dayNames[i] = dayNames[i].substring(0, 1);
				}
			}
		}
		int d = firstDayOfWeek;
		for (int i = 0; i < dayTitles.length; i++) {
			Label label = dayTitles[i];
			label.setText(dayNames[d]);
			label.setBackground(getBackground());
			if (d == Calendar.SUNDAY && (style & RED_SUNDAY) != 0 || d == Calendar.SATURDAY && (style & RED_SATURDAY) != 0) {
				label.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
			} else {
				label.setForeground(getForeground());
			}
			d++;
			if (d > dayTitles.length) {
				d -= dayTitles.length;
			}
		}
		drawDays();
	}

	protected void drawDays() {
		drawDays(1);
	}

	protected void drawDays(int day) {
		calendar.get(Calendar.DAY_OF_YEAR); // Force calendar update
		Calendar cal = (Calendar) calendar.clone();
		int firstDayOfWeek = cal.getFirstDayOfWeek();
		cal.set(Calendar.DAY_OF_MONTH, day);
		dayOffset = firstDayOfWeek - cal.get(Calendar.DAY_OF_WEEK);
		if (dayOffset >= 0) {
			// dayOffset -= 7;
		}
		cal.add(Calendar.DAY_OF_MONTH, dayOffset);
		Color foregroundColor = getForeground();
		for (int i = 0; i < days.length; cal.add(Calendar.DAY_OF_MONTH, 1)) {
			DayControl dayControl = days[i++];
			dayControl.setText(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
			if (isSameDay(cal, today)) {
				dayControl.setBorderColor(getDisplay().getSystemColor(SWT.COLOR_BLACK));
			} else {
				dayControl.setBorderColor(getBackground());
			}
			if (isSameMonth(cal, calendar)) {
				int d = cal.get(Calendar.DAY_OF_WEEK);
				if (d == Calendar.SUNDAY && (style & RED_SUNDAY) != 0 || d == Calendar.SATURDAY && (style & RED_SATURDAY) != 0) {
					dayControl.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
				} else {
					dayControl.setForeground(foregroundColor);
				}
			} else {
				// dayControl.setForeground(otherMonthColor);
				dayControl.setText(StringUtil.NN);
			}
			if (isSameDay(cal, calendar)) {
				dayControl.setBackground(getSelectionBackgroundColor());
				dayControl.setForeground(getSelectionForegroundColor());
			} else {
				dayControl.setBackground(getBackground());
			}
		}
	}

	private static boolean isSameDay(Calendar cal1, Calendar cal2) {
		return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
	}

	private static boolean isSameMonth(Calendar cal1, Calendar cal2) {
		return cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
	}

	public void setMonth(int month) {
		calendar.set(Calendar.MONTH, month);
		drawDays();
		dateChanged();
	}

	public void setYear(int year) {
		calendar.set(Calendar.YEAR, year);
		drawDays();
		dateChanged();
	}

	public void setCalendar(Calendar cal) {
		calendar = (Calendar) cal.clone();
		calendar.setLenient(true);
		drawDays();
		dateChanged();
	}

	public void mouseDown(MouseEvent event) {
		if (event.button == 1) { // Left click
			setFocus();
			selectDayEvent(event);
		}
	}

	public void mouseDoubleClick(MouseEvent event) {
	}

	public void mouseUp(MouseEvent event) {
	}

	public void focusGained(FocusEvent event) {
		DayControl selectedDay = getSelectedDayControl();
		selectedDay.setBackground(getSelectionBackgroundColor());
		selectedDay.setForeground(getSelectionForegroundColor());
	}

	public void focusLost(FocusEvent event) {
		// DayControl selectedDay = getSelectedDayControl();
		// selectedDay.setBackground(getSelectionBackgroundColor());
		// selectedDay.setForeground(getSelectionForegroundColor());
	}

	public void keyTraversed(TraverseEvent event) {
		switch (event.detail) {
			case SWT.TRAVERSE_ARROW_PREVIOUS:
			case SWT.TRAVERSE_ARROW_NEXT:
			case SWT.TRAVERSE_PAGE_PREVIOUS:
			case SWT.TRAVERSE_PAGE_NEXT:
				event.doit = false;
				break;
			case SWT.TRAVERSE_TAB_NEXT:
			case SWT.TRAVERSE_TAB_PREVIOUS:
				event.doit = true;
		}
	}

	public void keyPressed(KeyEvent event) {
		switch (event.keyCode) {
			case SWT.ARROW_LEFT:
				selectDay(calendar.get(Calendar.DAY_OF_MONTH) - 1);
				break;
			case SWT.ARROW_RIGHT:
				selectDay(calendar.get(Calendar.DAY_OF_MONTH) + 1);
				break;
			case SWT.ARROW_UP:
				selectDay(calendar.get(Calendar.DAY_OF_MONTH) - 7);
				break;
			case SWT.ARROW_DOWN:
				selectDay(calendar.get(Calendar.DAY_OF_MONTH) + 7);
				break;
			case SWT.PAGE_UP:
				setMonth(calendar.get(Calendar.MONTH) - 1);
				break;
			case SWT.PAGE_DOWN:
				setMonth(calendar.get(Calendar.MONTH) + 1);
				break;
			case SWT.CR:
				setFocus();
				selectDayEvent(event);
				break;
			case SWT.KEYPAD_CR:
				setFocus();
				selectDayEvent(event);
				break;
		}
	}

	private void selectDayEvent(TypedEvent event) {
		if (event.widget instanceof DayControl) {
			int index = findDay(event.widget);
			selectDay(index + 1 + dayOffset);
		}
	}

	public void keyReleased(KeyEvent event) {
	}

	private int findDay(Widget dayControl) {
		for (int i = 0; i < days.length; i++) {
			if (days[i].equals(dayControl)) {
				return i;
			}
		}
		return -1;
	}

	private void selectDay(int day) {
		calendar.get(Calendar.DAY_OF_YEAR); // Force calendar update
		if (day >= calendar.getActualMinimum(Calendar.DAY_OF_MONTH) && day <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
			int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
			// Stay on the same month.
			DayControl selectedDay = getSelectedDayControl();
			selectedDay.setBackground(getBackground());
			if (dayOfWeek == Calendar.SUNDAY) {
				selectedDay.setForeground(getDisplay().getSystemColor(SWT.COLOR_DARK_RED));
			} else {
				selectedDay.setForeground(getForeground());
			}
			calendar.set(Calendar.DAY_OF_MONTH, day);
			selectedDay = getSelectedDayControl();
			selectedDay.setBackground(getSelectionBackgroundColor());
			selectedDay.setForeground(getSelectionForegroundColor());
		} else {
			// Move to a different month.
			// int newDay = 1;
			// if (day <= calendar.getActualMinimum(Calendar.DAY_OF_MONTH)) {
			// calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
			// newDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - Math.abs(day);
			// } else if (day >= calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
			// newDay = day - calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			// calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
			// }
			// System.out.println(day + "------------ff-----------" + newDay);
			// calendar.set(Calendar.DAY_OF_MONTH, newDay);
			// drawDays();
		}
		dateChanged();
	}

	private DayControl getSelectedDayControl() {
		int num = calendar.get(Calendar.DAY_OF_MONTH) - 1 - dayOffset;
		if (num < 0) {
			num = 0;
		}
		return days[num];
	}

	private Color getSelectionBackgroundColor() {
		return isFocusControl() ? activeSelectionBackground : inactiveSelectionBackground;
	}

	private Color getSelectionForegroundColor() {
		return isFocusControl() ? activeSelectionForeground : inactiveSelectionForeground;
	}

	public boolean isFocusControl() {
		for (Control control = getDisplay().getFocusControl(); control != null; control = control.getParent()) {
			if (control == this) {
				return true;
			}
		}
		return false;
	}

	public void addCalendarListener(ICalendarListener listener) {
		this.listeners.add(listener);
	}

	public void removeCalendarListener(ICalendarListener listener) {
		this.listeners.remove(listener);
	}

	private void dateChanged() {
		if (!listeners.isEmpty()) {
			Event event = new Event();
			event.widget = this;
			event.display = getDisplay();
			event.time = (int) System.currentTimeMillis();
			event.data = calendar.clone();
			CalendarEvent event2 = new CalendarEvent(event);
			for (ICalendarListener listener : listeners) {
				listener.dateChanged(event2);
			}
		}
	}

	public Calendar getCalendar() {
		return (Calendar) calendar.clone();
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
		init();
	}

	public void setFont(Font font) {
		super.setFont(font);
		for (int i = 0; i < dayTitles.length; i++) {
			dayTitles[i].setFont(font);
		}
		for (int i = 0; i < days.length; i++) {
			days[i].setFont(font);
		}
	}
	static private class DayControl extends Composite implements Listener {

		private Composite filler;
		private Label label;

		public DayControl(Composite parent) {
			super(parent, SWT.NO_FOCUS);
			{
				final GridLayout gridLayout = new GridLayout();
				gridLayout.marginWidth = 1;
				gridLayout.marginHeight = 1;
				setLayout(gridLayout);
			}
			filler = new Composite(this, SWT.NO_FOCUS);
			filler.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			{
				final GridLayout gridLayout = new GridLayout();
				gridLayout.marginWidth = 2;
				gridLayout.marginHeight = 0;
				filler.setLayout(gridLayout);
			}
			filler.addListener(SWT.MouseDown, this);
			// filler.addListener(SWT.MouseUp, this);
			// filler.addListener(SWT.MouseDoubleClick, this);
			filler.addListener(SWT.KeyDown, this);
			label = new Label(filler, SWT.HORIZONTAL | SWT.VERTICAL | SWT.CENTER | SWT.NO_FOCUS);
			// label = new DayLabel(filler, SWT.HORIZONTAL | SWT.VERTICAL | SWT.CENTER | SWT.NO_FOCUS);
			GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
			layoutData.verticalAlignment = SWT.CENTER;
			label.setLayoutData(layoutData);
			label.addListener(SWT.MouseDown, this);
			// label.addListener(SWT.MouseUp, this);
			// label.addListener(SWT.MouseDoubleClick, this);
			label.addListener(SWT.KeyDown, this);
			setBorderColor(parent.getBackground());
			setBackground(parent.getBackground());
		}

		public void setText(String text) {
			label.setText(text);
		}

		public void setFont(Font font) {
			super.setFont(font);
			filler.setFont(font);
			label.setFont(font);
		}

		public void setBackground(Color color) {
			filler.setBackground(color);
			label.setBackground(color);
		}

		public void setForeground(Color color) {
			label.setForeground(color);
		}

		public void setBorderColor(Color color) {
			super.setBackground(color);
		}

		public void handleEvent(Event event) {
			notifyListeners(event.type, event);
		}
	}

	@Override
	public void addMouseListener(MouseListener listener) {
		super.addMouseListener(listener);
		for (int i = 0; i < days.length; i++) {
			days[i].addMouseListener(listener);
		}
	}

	@Override
	public void addKeyListener(KeyListener listener) {
		super.addKeyListener(listener);
		for (int i = 0; i < days.length; i++) {
			days[i].addKeyListener(listener);
		}
	}
}
