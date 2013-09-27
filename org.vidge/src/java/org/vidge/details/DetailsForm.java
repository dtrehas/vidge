package org.vidge.details;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

public class DetailsForm extends ScrolledForm {

	private FormToolkit toolkit;
	//private ScrolledForm form;
	private Font boldFont;
	private Details details = new Details(""); //$NON-NLS-1$
	private Composite body;
	private Composite ibody;
	
	public DetailsForm(Composite parent) {
		super(parent);
		Font defaultFont = JFaceResources.getDefaultFont();
		FontData[] data = defaultFont.getFontData();
		for (int i = 0; i < data.length; i++) {
			data[i].setStyle(SWT.BOLD);
		}

		toolkit = new FormToolkit(parent.getDisplay());
		//form = toolkit.createScrolledForm(parent);
		boldFont = new Font(parent.getDisplay(), data);
		body = this.getBody();
		body.setLayout(new FillLayout());
		createContent();
	}

	private void createContent() {
		cleanBody();
		ibody = toolkit.createComposite(body);
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		ibody.setLayout(layout);
		layout.leftMargin = 10;
		layout.rightMargin = 10;
		layout.horizontalSpacing = 10;
		TableWrapData tableWrapData =  new TableWrapData(TableWrapData.FILL_GRAB);
		tableWrapData.colspan = 2;
		tableWrapData.maxHeight = 2;
		toolkit.createCompositeSeparator(ibody).setLayoutData(tableWrapData);
		
		for (DetailProperty property : details.getProperties()) {
			tableWrapData = new TableWrapData();
			tableWrapData.align = TableWrapData.LEFT;
			toolkit.createLabel(ibody, property.getName()).setLayoutData(tableWrapData);
			Label text = toolkit.createLabel(ibody, property.toString(), SWT.WRAP); //$NON-NLS-1$
			text.setFont(boldFont);
			text.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		}
		setText(details.getTitle());
	}
	
	private void cleanBody() {
		if (ibody != null) {
			ibody.dispose();
		}
	}

	public void setDetails(Details details) {
		this.details = details;
		createContent();
	}
	
	public void disposeInternal() {
		boldFont.dispose();
		toolkit.dispose();
	}
	
	public void clearInternal() {
		cleanBody();
		reflow(true);
	}

}
