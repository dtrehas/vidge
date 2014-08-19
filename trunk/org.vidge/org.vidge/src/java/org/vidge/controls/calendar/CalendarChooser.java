package org.vidge.controls.calendar;

import java.util.Calendar;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;

public class CalendarChooser extends Composite {

	public static final int RED_SUNDAY = DayChooser.RED_SUNDAY;
	public static final int RED_WEEKEND = DayChooser.RED_WEEKEND;
	private boolean settingDate;
	private Spinner yearChooser;
	private MonthChooser monthChooser;
	private DayChooser dayChooser;
	private boolean settingYearMonth;
	private int fontHeight = 8;
	public static Font FONT;

	public CalendarChooser(Composite parent) {
		this(parent, SWT.FLAT, 8);
	}

	public CalendarChooser(final Composite parent, int style) {
		this(parent, SWT.FLAT, 8);
	}

	public CalendarChooser(final Composite parent, int style, int fontSize) {
		super(parent, style);
		fontHeight = fontSize;
		if (fontHeight == 0) {
			fontHeight = 8;
		}
		FONT = getBoldFont(parent.getFont());
		Calendar calendar = Calendar.getInstance();
		{
			final GridLayout gridLayout = new GridLayout();
			gridLayout.marginHeight = 0;
			gridLayout.marginWidth = 0;
			gridLayout.horizontalSpacing = 2;
			gridLayout.verticalSpacing = 2;
			setLayout(gridLayout);
		}
		final Composite header = new Composite(this, SWT.NONE);
		{
			{
				final GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
				header.setLayoutData(gridData);
				final GridLayout gridLayout = new GridLayout();
				gridLayout.numColumns = 3;
				gridLayout.marginWidth = 0;
				gridLayout.marginHeight = 0;
				header.setLayout(gridLayout);
			}
			final Button prevMonthButton = new Button(header, SWT.ARROW | SWT.LEFT | SWT.CENTER | (style & SWT.FLAT));
			prevMonthButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL));
			prevMonthButton.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					previousMonth();
				}
			});
			final Composite composite = new Composite(header, SWT.NONE);
			composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
			{
				final GridLayout gridLayout = new GridLayout();
				gridLayout.numColumns = 2;
				gridLayout.marginWidth = 0;
				gridLayout.marginHeight = 0;
				composite.setLayout(gridLayout);
			}
			header.setTabList(new Control[] {
				composite
			});
			monthChooser = new MonthChooser(composite);
			monthChooser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
			monthChooser.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					if (!settingYearMonth) {
						dayChooser.setMonth(monthChooser.getMonth());
					}
				}
			});
			yearChooser = new Spinner(composite, SWT.BORDER);
			yearChooser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
			yearChooser.setMinimum(1);
			yearChooser.setMaximum(9999);
			yearChooser.setSelection(calendar.get(Calendar.YEAR));
			yearChooser.addModifyListener(new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					if (!settingYearMonth) {
						dayChooser.setYear(yearChooser.getSelection());
					}
				}
			});
			final Button nextMonthButton = new Button(header, SWT.ARROW | SWT.RIGHT | SWT.CENTER | (style & SWT.FLAT));
			nextMonthButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL));
			nextMonthButton.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					nextMonth();
				}
			});
		}
		{
			dayChooser = new DayChooser(this, SWT.BORDER | (style & RED_WEEKEND));
			GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
			gridData.horizontalSpan = 3;
			dayChooser.setLayoutData(gridData);
			dayChooser.addCalendarListener(new ICalendarListener() {

				public void dateChanged(CalendarEvent event) {
					refreshYearMonth(event.getCalendar());
				}
			});
		}
		setTabList(new Control[] {
				header, dayChooser
		});
		setFont(FONT);
		// setFont(parent.getFont());
	}

	private Font getBoldFont(Font font) {
		FontData[] data = font.getFontData();
		for (int i = 0; i < data.length; i++) {
			data[i].setStyle(SWT.BOLD);
			data[i].setHeight(fontHeight);
		}
		Font boldFont = new Font(this.getShell().getDisplay(), data);
		return boldFont;
	}

	public void setCalendar(Calendar cal) {
		settingDate = true;
		try {
			refreshYearMonth(cal);
			dayChooser.setCalendar(cal);
		} finally {
			settingDate = false;
		}
	}

	private void refreshYearMonth(Calendar cal) {
		settingYearMonth = true;
		yearChooser.setSelection(cal.get(Calendar.YEAR));
		monthChooser.setMonth(cal.get(Calendar.MONTH));
		settingYearMonth = false;
	}

	public void nextMonth() {
		Calendar cal = dayChooser.getCalendar();
		cal.add(Calendar.MONTH, 1);
		refreshYearMonth(cal);
		dayChooser.setCalendar(cal);
	}

	public void previousMonth() {
		Calendar cal = dayChooser.getCalendar();
		cal.add(Calendar.MONTH, -1);
		refreshYearMonth(cal);
		dayChooser.setCalendar(cal);
	}

	public Calendar getCalendar() {
		return dayChooser.getCalendar();
	}

	public void addCalendarListener(ICalendarListener listener) {
		dayChooser.addCalendarListener(listener);
	}

	public void removeCalendarListener(ICalendarListener listener) {
		dayChooser.removeCalendarListener(listener);
	}

	public void setLocale(Locale locale) {
		monthChooser.setLocale(locale);
		dayChooser.setLocale(locale);
		yearChooser.setSelection(getCalendar().get(Calendar.YEAR));
	}

	public void setFont(Font font) {
		super.setFont(font);
		monthChooser.setFont(font);
		yearChooser.setFont(font);
		dayChooser.setFont(font);
	}

	public boolean isSettingDate() {
		return settingDate;
	}

	@Override
	public void addKeyListener(KeyListener listener) {
		// super.addKeyListener(listener);
		dayChooser.addKeyListener(listener);
	}

	@Override
	public void addMouseListener(MouseListener listener) {
		// super.addMouseListener(listener);
		dayChooser.addMouseListener(listener);
	}
}
