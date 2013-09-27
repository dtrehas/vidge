package org.vidge.controls;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;

public class CustomButton {

	private Label label;
	private List<SelectionListener> listeners = new ArrayList<SelectionListener>();
	private boolean enabled = true;
	private boolean selected = false;

	public CustomButton(Composite parent, Image image, String toolTip) {
		this(parent, SWT.NONE);
		label.setImage(image);
		label.setToolTipText(toolTip);
		if (image == null)
			label.setText("X");
	}

	public CustomButton(Composite parent, String text) {
		this(parent, SWT.NONE);
		label.setText(text);
	}

	public CustomButton(Composite parent, String text, int style) {
		this(parent, style);
		label.setText(text);
	}

	public CustomButton(Composite parent, Image image, String toolTip, int style) {
		this(parent, style);
		label.setImage(image);
		label.setToolTipText(toolTip);
		if (image == null)
			label.setText("X");
	}

	public CustomButton(Composite parent, int style) {
		label = new Label(parent, style | SWT.WRAP | SWT.CENTER);
		label.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				label.redraw();
				fireButtonSelected();
			}

			@Override
			public void mouseUp(MouseEvent e) {
				label.redraw();
			}
		});
		label.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == 32) {
					fireButtonSelected();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
			}
		});
		label.addMouseTrackListener(new MouseTrackListener() {

			public void mouseEnter(MouseEvent e) {
				label.redraw();
			}

			public void mouseExit(MouseEvent e) {
				label.redraw();
			}

			public void mouseHover(MouseEvent e) {
			}
		});
		label.addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {
				label.redraw();
			}

			public void focusLost(FocusEvent e) {
				label.redraw();
			}
		});
	}

	private void fireButtonSelected() {
		if (enabled) {
			selected = !selected;
			for (SelectionListener listener : listeners) {
				Event event = new Event();
				event.widget = label;
				listener.widgetSelected(new SelectionEvent(event));
				listener.widgetDefaultSelected(new SelectionEvent(event));
			}
		}
	}

	public void addSelectionListener(SelectionListener adapter) {
		listeners.add(adapter);
	}

	public void setLayoutData(Object layoutData) {
		label.setLayoutData(layoutData);
	}

	public void relax() {
		label.redraw();
	}

	public boolean forceFocus() {
		return label.forceFocus();
	}

	public boolean isFocusControl() {
		label.redraw();
		return label.isFocusControl();
	}

	public boolean setFocus() {
		label.redraw();
		return label.setFocus();
	}

	public void addFocusListener(FocusListener listener) {
		label.addFocusListener(listener);
	}

	public Point getLocation() {
		label.redraw();
		return label.getLocation();
	}

	public Label getLabel() {
		return label;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		label.setEnabled(enabled);
	}

	public void setFont(Font font) {
		label.setFont(font);
	}

	public void setText(String text) {
		label.setText(text);
	}

	public String getText() {
		return label.getText();
	}

	public void setSelection(boolean selected) {
		this.selected = selected;
	}

	public void setData(Object data) {
		label.setData(data);
	}

	public void addPaintListener(PaintListener listener) {
		label.addPaintListener(listener);
	}

	public Object getData() {
		return label.getData();
	}

	public void setImage(Image image) {
		label.setImage(image);
	}
}
