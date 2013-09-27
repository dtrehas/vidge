package org.vidge.details;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.vidge.SharedImages;
import org.vidge.Vidge;
import org.vidge.VidgeResources;
import org.vidge.action.BusinessAction;
import org.vidge.controls.TablePanel;
import org.vidge.controls.tree.ObjectTree;
import org.vidge.form.impl.BlindNodeForm;

public class DetailsComposite2 extends Composite {

	private FormToolkit toolkit;
	private Font boldFont;
	private Details details = new Details(""); //$NON-NLS-1$
	protected Composite removableBody;
	private Label title;
	private List<Composite> outerComposits = new ArrayList<Composite>();
	public static final int NO_HEADER = 30456;
	protected BusinessAction editAction;

	public DetailsComposite2(Composite parent, int style) {
		super(parent, style);
		prepareComposite();
		removableBody = new Composite(this, SWT.NONE);
		toolkit.adapt(removableBody);
		removableBody.setLayoutData(new GridData(GridData.FILL_BOTH));
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		layout.leftMargin = 10;
		layout.rightMargin = 10;
		layout.horizontalSpacing = 10;
		removableBody.setLayout(layout);
		removableBody.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		removableBody.setFont(JFaceResources.getHeaderFont());
		Button button = new Button(this, SWT.PUSH);
		button.setImage(VidgeResources.getInstance().getImage(SharedImages.ACTION_EDIT));
		button.setLayoutData(new GridData(GridData.END | GridData.VERTICAL_ALIGN_CENTER));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (editAction != null) {
					editAction.run();
				}
			}
		});
	}

	private void prepareComposite() {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 4;
		layout.marginHeight = 4;
		layout.marginRight = 10;
		setLayout(layout);
		Font defaultFont = JFaceResources.getDefaultFont();
		FontData[] data = defaultFont.getFontData();
		for (int i = 0; i < data.length; i++) {
			data[i].setStyle(SWT.BOLD);
		}
		toolkit = new FormToolkit(this.getDisplay());
		toolkit.adapt(this);
		boldFont = new Font(this.getDisplay(), data);
		this.addPaintListener(new PaintListener() {

			public void paintControl(PaintEvent e) {
				try {
					Color color = e.display.getSystemColor(SWT.COLOR_GRAY);
					e.gc.setForeground(color);
					e.gc.drawRoundRectangle(1, 1, DetailsComposite2.this.getSize().x - 3, DetailsComposite2.this.getSize().y - 3, 8, 8);
				} catch (Exception e1) {
				}
			}
		});
	}

	protected void createContent() {
		try {
			buildProperties(details, removableBody);
			for (Composite composite : outerComposits) {
				composite.setParent(removableBody);
			}
			int a = 1;
			for (Details child : details.getChildList()) {
				buildChild(child, removableBody, a);
				a++;
			}
			prepareScrollComposite();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void buildProperties(Details details1, Composite parent) {
		for (final DetailProperty property : details1.getProperties()) {
			switch (property.getPropertyType()) {
				case LINE:
					TableWrapData tableSpanWrapData = new TableWrapData(TableWrapData.FILL_GRAB);
					tableSpanWrapData.colspan = 2;
					tableSpanWrapData.maxHeight = 2;
					toolkit.createCompositeSeparator(parent).setLayoutData(tableSpanWrapData);
					break;
				case NAMED_LINE:
					createName(parent, property);
					toolkit.createCompositeSeparator(parent).setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
					break;
				case STRING:
					createName(parent, property);
					Label text = toolkit.createLabel(parent, property.toString(), SWT.WRAP);
					text.setFont(boldFont);
					text.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
					break;
				case ACTION:
					createName(parent, property);
					Hyperlink link = toolkit.createHyperlink(parent, property.toString(), SWT.WRAP);
					link.addHyperlinkListener(new HyperlinkAdapter() {

						public void linkActivated(HyperlinkEvent e) {
							property.getRunAction();
						}
					});
					link.setLayoutData(new TableWrapData());
					break;
				case IMAGE:
					createName(parent, property);
					final ImageData imageData = new ImageData(new ByteArrayInputStream((byte[]) property.getValue()));
					ImageHyperlink hyperlink = toolkit.createImageHyperlink(parent, SWT.LEFT);
					hyperlink.setImage(new Image(this.getDisplay(), imageData));
					hyperlink.addPaintListener(new PaintListener() {

						public void paintControl(PaintEvent e) {
							try {
								if (imageData != null) {
									e.gc.drawRoundRectangle(0, 0, imageData.width, imageData.height, 5, 5);
								}
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
					});
					break;
				case TREE:
					property.setName(property.getName());
					createName(parent, property);
					ObjectTree tree = new ObjectTree(parent, ObjectTree.NODES_LINES | ObjectTree.NO_IMAGES);
					TableWrapData wrapData = new TableWrapData(TableWrapData.FILL_GRAB);
					wrapData.heightHint = ((List<Object>) property.getValue()).size() * (boldFont.getFontData()[0].getHeight() * 2) + 10;
					tree.getControl().setLayoutData(wrapData);
					tree.getViewer().getTree().setFont(boldFont);
					BlindNodeForm root = new BlindNodeForm((List<Object>) property.getValue());
					tree.setInput(root);
					tree.getViewer().expandAll();
					break;
			}
		}
	}

	private void buildChild(Details child, Composite parent, int a) {
		TableWrapData tableSpanWrapData = new TableWrapData(TableWrapData.FILL_GRAB);
		tableSpanWrapData.colspan = 2;
		Section section = toolkit.createSection(parent, Section.TITLE_BAR | Section.TWISTIE);
		section.setFont(boldFont);
		section.setLayoutData(tableSpanWrapData);
		section.setText(a + ".   " + child.getTitle());
		toolkit.createCompositeSeparator(section);
		if ((child.getHeaderActions() != null) && (child.getHeaderActions().length > 0)) {
			Composite textClient = toolkit.createComposite(section);
			textClient.setLayout(new FillLayout());
			for (final Action action : child.getHeaderActions()) {
				Hyperlink link = toolkit.createHyperlink(textClient, action.getText(), SWT.WRAP);
				link.addHyperlinkListener(new HyperlinkAdapter() {

					public void linkActivated(HyperlinkEvent e) {
						action.run();
					}
				});
			}
			section.setTextClient(textClient);
		}
		Composite client = toolkit.createComposite(section);
		section.setClient(client);
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		layout.leftMargin = 0;
		layout.rightMargin = 0;
		layout.horizontalSpacing = 0;
		client.setLayout(layout);
		buildProperties(child, client);
	}

	private void createName(Composite parent, final DetailProperty property) {
		TableWrapData tableWrapData = new TableWrapData();
		tableWrapData.align = TableWrapData.LEFT;
		toolkit.createLabel(parent, property.getName()).setLayoutData(tableWrapData);
	}

	public void addOuterComposite(Composite composite) {
		outerComposits.add(composite);
	}

	public void setDetails(Details details) {
		clear();
		this.details = details;
		if (details.getTableExplorer() != null) {
			createTableContent();
		} else {
			createContent();
		}
	}

	private void clear() {
		for (Control control : removableBody.getChildren()) {
			control.dispose();
		}
	}

	private void createTableContent() {
		try {
			removableBody.setLayout(new FillLayout());
			TablePanel tablePanel = new TablePanel(details.getTableExplorer().getInputClass(), details.getDataList(), Vidge.NO_VIRTUAL | Vidge.NO_ACTIONS);
			tablePanel.createViewer(removableBody);
			// new ObjectChooser(removableBody, ObjectChooser.TOOLKIT | ObjectChooser.NO_NUM_COLUMN, details.getTableExplorer(), details.getDataList());
			prepareScrollComposite();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	private void prepareScrollComposite() {
		if (details.getTitle() == null && title != null) {
			title.setText(details.getTitle());
		}
		removableBody.setSize(removableBody.computeSize(removableBody.getClientArea().width, SWT.DEFAULT));
	}

	public void dispose() {
		boldFont.dispose();
		toolkit.dispose();
	}

	public void setEditAction(BusinessAction editAction) {
		this.editAction = editAction;
	}
}
