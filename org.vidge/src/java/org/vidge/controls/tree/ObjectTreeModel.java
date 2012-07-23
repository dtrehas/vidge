package org.vidge.controls.tree;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeItem;
import org.vidge.VidgeResources;
import org.vidge.SharedImages;
import org.vidge.inface.INodeForm;

public class ObjectTreeModel {

	private final ObjectTree tree;
	private LabelProvider labelProvider;
	private ITreeContentProvider contentProvider;
	private ImageDescriptor decoreImage;
	private SelectionListener selectionListener;
	private TreeListener treeListener;
	private final boolean showImages;

	public ObjectTreeModel(ObjectTree tree, boolean showImages) {
		this.tree = tree;
		this.showImages = showImages;
		contentProvider = new ObjectTreeContentProvider();
		labelProvider = new ObjectTreeLabelProvider();
		decoreImage = VidgeResources.getInstance().getImageDescriptor(SharedImages.RED_SQUARE);
		selectionListener = new NodeSelectionListener();
		treeListener = new TreeListener() {

			@Override
			public void treeCollapsed(TreeEvent e) {
			}

			@Override
			public void treeExpanded(TreeEvent e) {
				((INodeForm) ((TreeItem) e.item).getData()).setItem((TreeItem) e.item);
			}
		};
	}
	private class ObjectTreeContentProvider implements ITreeContentProvider {

		public Object[] getElements(Object inputElement) {
			((INodeForm) inputElement).setViewer(tree.getViewer());
			INodeForm[] children = ((INodeForm) inputElement).getChildren();
			for (INodeForm form : children) {
				form.setViewer(tree.getViewer());
			}
			return children;
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getChildren(Object parentElement) {
			((INodeForm) parentElement).setViewer(tree.getViewer());
			INodeForm[] children = ((INodeForm) parentElement).getChildren();
			for (INodeForm form : children) {
				form.setViewer(tree.getViewer());
			}
			return children;
		}

		public Object getParent(Object element) {
			return ((INodeForm) element).getParent();
		}

		public boolean hasChildren(Object element) {
			return ((INodeForm) element).getChildCount() > 0;
		}
	}
	private class ObjectTreeLabelProvider extends LabelProvider {

		@Override
		public Image getImage(Object element) {
			if (!showImages) {
				return null;
			}
			Image image = ((INodeForm) element).getImage();
			if (image == null) {
				image = VidgeResources.getInstance().getImage(SharedImages.DEFAULT_NODE);
			}
			try {
				if ((((INodeForm) element).getInput() != null) && (tree.getInput().getInput() != null)) {
					if (((INodeForm) element).getInput().equals(tree.getInput().getInput())) {
						image = new DecorationOverlayIcon(image, decoreImage, IDecoration.TOP_LEFT).createImage();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return image;
		}
	}
	private static class NodeSelectionListener implements SelectionListener {

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			if (e.detail == SWT.CHECK) {
				((INodeForm) ((TreeItem) e.item).getData()).setChecked((TreeItem) e.item);
			}
		}
	}

	public void setVisibleObject(INodeForm obj) {
		tree.getViewer().setExpandedElements(new Object[] {
			obj
		});
	}

	IContentProvider getContentProvider() {
		return contentProvider;
	}

	LabelProvider getLabelProvider() {
		return labelProvider;
	}

	public SelectionListener getSelectionListener() {
		return selectionListener;
	}

	public TreeListener getTreeListener() {
		return treeListener;
	}
}
