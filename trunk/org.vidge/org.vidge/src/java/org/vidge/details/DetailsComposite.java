package org.vidge.details;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.vidge.controls.chooser.ObjectChooser;
import org.vidge.controls.tree.ObjectTree;
import org.vidge.dialog.RawResourceDialog;
import org.vidge.form.impl.BlindNodeForm;
import org.vidge.util.StringUtil;

public class DetailsComposite extends Composite {

	private static final int _65 = 65;
	private FormToolkit toolkit;
	private Font boldFont;
	private Details details = new Details(StringUtil.NN); //$NON-NLS-1$
	protected Composite removableBody;
	private Label title;
	private List<Composite> outerComposits = new ArrayList<Composite>();
	private ScrolledComposite scrollComposite;
	public static final int NO_HEADER = 30456;

	public DetailsComposite(Composite parent, int style) {
		super(parent, style);
		prepareComposite();
		if ((style & NO_HEADER) == 0) {
			title = toolkit.createLabel(this, StringUtil.NN, SWT.WRAP); //$NON-NLS-1$
			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalIndent = 10;
			title.setLayoutData(gridData);
			title.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
			title.setFont(JFaceResources.getHeaderFont());
			toolkit.createCompositeSeparator(this).setLayoutData(new GridData((parent.getShell().getSize().x / 4) * 2, 2));
		}
		scrollComposite = new ScrolledComposite(this, SWT.V_SCROLL);
		toolkit.adapt(scrollComposite);
		scrollComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		createRemovableBody();
	}

	private void prepareComposite() {
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 4;
		layout.marginHeight = 4;
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

			@Override
			public void paintControl(PaintEvent e) {
				try {
					Color color = e.display.getSystemColor(SWT.COLOR_GRAY);
					e.gc.setForeground(color);
					e.gc.drawRoundRectangle(1, 1, DetailsComposite.this.getSize().x - 3, DetailsComposite.this.getSize().y - 3, 8, 8);
				} catch (Exception e1) {
				}
			}
		});
	}

	protected void createRemovableBody() {
		createBody();
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		layout.leftMargin = 10;
		layout.rightMargin = 10;
		layout.horizontalSpacing = 10;
		removableBody.setLayout(layout);
	}

	protected void createBody() {
		removableBody = toolkit.createComposite(scrollComposite);
		toolkit.adapt(removableBody);
		removableBody.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		removableBody.setFont(JFaceResources.getHeaderFont());
	}

	protected void createContent() {
		clean(StringUtil.NN); //$NON-NLS-1$
		try {
			// createRemovableBody();
			buildProperties(details, removableBody);
			for (Composite composite : outerComposits) {
				composite.setParent(scrollComposite);
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
			if (property.getValue() == null) {
				createName(parent, property);
				Label text = toolkit.createLabel(parent, StringUtil.NN, SWT.WRAP);
				text.setFont(boldFont);
				text.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
				continue;
			}
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
					if (property.getFont() == null) {
						text.setFont(boldFont);
					} else {
						text.setFont(property.getFont());
					}
					text.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
					break;
				case ACTION:
					createName(parent, property);
					Hyperlink link = toolkit.createHyperlink(parent, property.toString(), SWT.WRAP);
					link.addHyperlinkListener(new HyperlinkAdapter() {

						@Override
						public void linkActivated(HyperlinkEvent e) {
							property.getRunAction();
						}
					});
					link.setLayoutData(new TableWrapData());
					break;
				case IMAGE:
					createName(parent, property);
					if (property.getValue() instanceof Image) {
						ImageHyperlink hyperlink = toolkit.createImageHyperlink(parent, SWT.LEFT);
						final Image value = (Image) property.getValue();
						hyperlink.setImage(value);
						hyperlink.addHyperlinkListener(new IHyperlinkListener() {

							@Override
							public void linkExited(HyperlinkEvent e) {
							}

							@Override
							public void linkEntered(HyperlinkEvent e) {
							}

							@Override
							public void linkActivated(HyperlinkEvent e) {
								RawResourceDialog dialog = new RawResourceDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Resource",
									value.getImageData().data, 0);
								dialog.open();
							}
						});
					} else {
						final byte[] value = (byte[]) property.getValue();
						ImageData imageData = new ImageData(new ByteArrayInputStream(value));
						int maxX = 90;
						int maxY = 90;
						if ((imageData.width > maxX) || (imageData.height > maxY)) {
							imageData = imageData.scaledTo(maxX, maxY);
						}
						ImageHyperlink hyperlink = toolkit.createImageHyperlink(parent, SWT.LEFT);
						hyperlink.setImage(new Image(this.getDisplay(), imageData));
						hyperlink.addHyperlinkListener(new IHyperlinkListener() {

							@Override
							public void linkExited(HyperlinkEvent e) {
							}

							@Override
							public void linkEntered(HyperlinkEvent e) {
							}

							@Override
							public void linkActivated(HyperlinkEvent e) {
								RawResourceDialog dialog = new RawResourceDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Resource",
									value, 0);
								dialog.open();
							}
						});
					}
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
		section.addExpansionListener(new IExpansionListener() {

			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				if (scrollComposite != null) {
					scrollComposite.setMinSize(removableBody.computeSize(scrollComposite.getClientArea().width, SWT.DEFAULT));
				}
			}

			@Override
			public void expansionStateChanging(ExpansionEvent e) {
			}
		});
		if ((child.getHeaderActions() != null) && (child.getHeaderActions().length > 0)) {
			Composite textClient = toolkit.createComposite(section);
			textClient.setLayout(new FillLayout());
			for (final Action action : child.getHeaderActions()) {
				Hyperlink link = toolkit.createHyperlink(textClient, action.getText(), SWT.WRAP);
				link.addHyperlinkListener(new HyperlinkAdapter() {

					@Override
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

	public void clean(String header) {
		for (Control cc : removableBody.getChildren()) {
			cc.dispose();
		}
		if (title != null && header != null) {
			title.setText(header);
		}
	}

	public void setDetails(Details details) {
		this.details = details;
		if (details.getTableExplorer() != null) {
			createTableContent();
		} else {
			createContent();
		}
	}

	public void setDetails(String header, Details details) {
		this.details = details;
		if (details.getTableExplorer() != null) {
			createTableContent();
		} else {
			createContent();
		}
		if (title != null && header != null) {
			title.setText(header);
		}
	}

	private void createTableContent() {
		clean(StringUtil.NN); //$NON-NLS-1$
		try {
			// createBody();
			removableBody.setLayout(new FillLayout());
			new ObjectChooser(removableBody, ObjectChooser.TOOLKIT | ObjectChooser.NO_NUM_COLUMN, details.getTableExplorer(), details.getDataList());
			prepareScrollComposite();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	private void prepareScrollComposite() {
		if (scrollComposite != null) {
			scrollComposite.setContent(removableBody);
			scrollComposite.setExpandVertical(true);
			scrollComposite.setExpandHorizontal(true);
			scrollComposite.setMinSize(removableBody.computeSize(scrollComposite.getClientArea().width, _65));
			scrollComposite.addControlListener(new ControlAdapter() {

				@Override
				public void controlResized(ControlEvent e) {
					scrollComposite.setMinSize(removableBody.computeSize(scrollComposite.getClientArea().width, _65));
				}
			});
		}
		if (details.getTitle() != null && title != null) {
			title.setText(details.getTitle());
		}
	}

	@Override
	public void dispose() {
		boldFont.dispose();
		toolkit.dispose();
	}
}
