package org.vidge.util;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.vidge.Messages;
import org.vidge.action.ShowViewAction;
import org.vidge.action.ShowWizardAction;

public class AppUtility {

	public static final String PERSPECTIVES_GROUP = "Perspectives"; //$NON-NLS-1$
	public static final String EDITORS_GROUP = "Editors"; //$NON-NLS-1$
	private static final String VIEWS_GROUP = "Views"; //$NON-NLS-1$
	private IPerspectiveDescriptor currentPerspective;
	IMenuManager menuBar;
	ICoolBarManager coolBar;
	IWorkbenchWindow window;
	private MenuManager fileMenu;
	private MenuManager windowMenu;
	private MenuManager showViewMenu;
	private MenuManager wizardMenu;
	private MenuManager editorsMenu;
	private IWorkbenchAction exitAction;
	private IWorkbenchAction aboutAction;
	private IWorkbenchAction helpContentsAction;
	private IWorkbenchAction preferencesAction;
	private ToolBarManager homeBar, toolbar, perspectiveBar, wizardBar, editorsBar;
	private static AppUtility instance;
	private IWorkbenchWindowConfigurer configurer;

	private AppUtility() {
		homeBar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
		toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
		perspectiveBar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
		wizardBar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
		editorsBar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
	}

	public void fillMenu() {
		exitAction = ActionFactory.QUIT.create(window);
		// configurer.register(exitAction);
		aboutAction = ActionFactory.ABOUT.create(window);
		// configurer.register(aboutAction);
		helpContentsAction = ActionFactory.HELP_CONTENTS.create(window);
		// configurer.register(helpContentsAction);
		preferencesAction = ActionFactory.PREFERENCES.create(window);
		// configurer.register(preferencesAction);
		menuBar.removeAll();
		fileMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_FILE, "MyFile"); //$NON-NLS-1$
		windowMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_WINDOW, "MyWindow"); //$NON-NLS-1$
		MenuManager helpMenu = new MenuManager(Messages.ApplicationActionBarAdvisor_HELP, IWorkbenchActionConstants.M_HELP);
		showViewMenu = new MenuManager(Messages.CapActionBarAdvisor_Show_View, "ShowView"); //$NON-NLS-1$
		wizardMenu = new MenuManager(Messages.CapActionBarAdvisor_Wizards, "wizardMenu"); //$NON-NLS-1$
		editorsMenu = new MenuManager(Messages.CapActionBarAdvisor_Wizards, "editorsMenu"); //$NON-NLS-1$
		menuBar.add(fileMenu);
		menuBar.add(windowMenu);
		windowMenu.add(new Separator(VIEWS_GROUP));
		windowMenu.add(showViewMenu);
		windowMenu.add(wizardMenu);
		windowMenu.add(preferencesAction);
		windowMenu.add(new Separator(PERSPECTIVES_GROUP));
		windowMenu.add(editorsMenu);
		windowMenu.add(new Separator(EDITORS_GROUP));
		menuBar.add(helpMenu);
		fileMenu.add(exitAction);
		helpMenu.add(helpContentsAction);
		helpMenu.add(aboutAction);
		setPerspectiveEvironment(currentPerspective);
		configurer.setShowMenuBar(false);
		configurer.setShowMenuBar(true);
	}

	public void setPerspectiveEvironment(IPerspectiveDescriptor perspectiveDescriptor) {
		showViewMenu.removeAll();
		manageToolBars();
		if (perspectiveDescriptor != null) {
			String perspectiveNameSpace = perspectiveDescriptor.getId().substring(0, perspectiveDescriptor.getId().lastIndexOf("."));//$NON-NLS-1$
			for (IExtension extension : Platform.getExtensionRegistry().getExtensions(perspectiveNameSpace)) {
				if (extension.getExtensionPointUniqueIdentifier().equals("org.eclipse.ui.perspectiveExtensions")) {//$NON-NLS-1$
					for (IConfigurationElement config : extension.getConfigurationElements()) {
						for (IConfigurationElement config2 : config.getChildren()) {
							if (config2.getName().equals("view")) { //$NON-NLS-1$
								String viewId = config2.getAttribute("id");//$NON-NLS-1$
								if (viewId != null) {
									IViewDescriptor viewDescriptor = PlatformUI.getWorkbench().getViewRegistry().find(viewId);
									if (viewDescriptor != null) {
										showViewMenu.add(new ShowViewAction(viewDescriptor));
										toolbar.add(new ShowViewAction(viewDescriptor));
									}
								}
							}
						}
					}
				}
			}
		}
		manageWizards(perspectiveDescriptor);
		coolBar.update(true);
	}

	private void manageToolBars() {
		// cleanUnused();
		homeBar.removeAll();
		toolbar.removeAll();
		wizardBar.removeAll();
		perspectiveBar.removeAll();
		// editorsBar.removeAll();
		// for (IPerspectiveDescriptor descriptor : getPerspectives()) {
		// perspectiveBar.add(new PerspectiveAction(descriptor));
		// }
		// homeBar.add(new PerspectiveAction(home));
		addToolBar(homeBar, "home"); //$NON-NLS-1$
		addToolBar(perspectiveBar, "perspectives");//$NON-NLS-1$
		addToolBar(toolbar, "main");//$NON-NLS-1$
		addToolBar(wizardBar, "wizards");//$NON-NLS-1$
		addToolBar(editorsBar, "editors");//$NON-NLS-1$
	}

	private void addToolBar(ToolBarManager manager, String toolBarName) {
		if (coolBar.find(toolBarName) == null) {
			coolBar.add(new ToolBarContributionItem(manager, toolBarName));
		}
	}

	public void cleanUnused() {
		coolBar.remove("org.eclipse.ui.WorkingSetActionSet"); //$NON-NLS-1$
		coolBar.remove("org.eclipse.ui.edit.text.actionSet.annotationNavigation"); //$NON-NLS-1$
		coolBar.remove("org.eclipse.ui.edit.text.actionSet.navigation"); //$NON-NLS-1$
	}

	public void manageWizards(IPerspectiveDescriptor perspectiveDescriptor) {
		wizardMenu.removeAll();
		if (perspectiveDescriptor != null) {
			String perspectiveNameSpace = perspectiveDescriptor.getId().substring(0, perspectiveDescriptor.getId().lastIndexOf("."));//$NON-NLS-1$
			for (IExtension extension : Platform.getExtensionRegistry().getExtensions(perspectiveNameSpace)) {
				if (extension.getExtensionPointUniqueIdentifier().equals("org.eclipse.ui.perspectiveExtensions")) {//$NON-NLS-1$
					for (IConfigurationElement config : extension.getConfigurationElements()) {
						for (IConfigurationElement config2 : config.getChildren()) {
							if (config2.getName().equals("newWizardShortcut")) { //$NON-NLS-1$
								String viewId = config2.getAttribute("id");//$NON-NLS-1$
								if (viewId != null) {
									IWizardDescriptor descriptor = PlatformUI.getWorkbench().getNewWizardRegistry().findWizard(viewId);
									if (descriptor != null) {
										wizardMenu.add(new ShowWizardAction(descriptor));
										wizardBar.add(new ShowWizardAction(descriptor));
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public void setCoolBar(ICoolBarManager coolBar) {
		this.coolBar = coolBar;
	}

	public void setMenuBar(IMenuManager menuBar) {
		this.menuBar = menuBar;
	}

	public void setWindow(IWorkbenchWindow window) {
		this.window = window;
	}

	public static AppUtility getInstance() {
		if (instance == null) {
			instance = new AppUtility();
		}
		return instance;
	}

	public void setPerspective(IPerspectiveDescriptor descriptor) {
		currentPerspective = descriptor;
	}

	public void setConfigurer(IWorkbenchWindowConfigurer configurer) {
		this.configurer = configurer;
	}
}
