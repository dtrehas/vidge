package org.vidge.job;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.vidge.dialog.VidgeErrorDialog;

public abstract class AbstractJob extends Job {

	private int count = 300;
	private int worked = 0;
	private String taskName;
	private String errorMessage;
	private boolean internalComplete = false;
	private boolean internalError = false;
	protected IProgressMonitor progressMonitor;
	protected Shell shell;
	private boolean isChangedCount = false;

	public AbstractJob(String name, String taskName, String errorMessage) {
		super(name);
		this.taskName = taskName;
		this.errorMessage = errorMessage;
		// setUser(true);
	}

	public AbstractJob(String name) {
		super(name);
	}

	protected void subTask(final String string) {
		progressMonitor.subTask(string);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		progressMonitor = monitor;
		Display display = PlatformUI.getWorkbench().getDisplay();
		try {
			progressMonitor.beginTask(taskName, IProgressMonitor.UNKNOWN);
			runWithProgress();
			if ((internalError) || (monitor.isCanceled())) {
				return Status.CANCEL_STATUS;
			}
			if ((display != null) && (!display.isDisposed())) {
				try {
					display.asyncExec(new Runnable() {

						public void run() {
							try {
								processShow();
							} catch (Throwable e) {
								showErrorDialog(e);
							} finally {
								try {
									finalizeJob();
								} catch (Throwable e) {
									e.printStackTrace();
								}
							}
						}
					});
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		} finally {
			if ((display != null) && (!display.isDisposed())) {
				try {
					display.asyncExec(new Runnable() {

						public void run() {
							progressMonitor.done();
						}
					});
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
		return Status.OK_STATUS;
	}

	private void defaultProgress() {
		if (!isChangedCount) {
			new InnerJobThread().start();
			while ((!internalComplete) && (!progressMonitor.isCanceled()) && (!internalError)) {
				try {
					Thread.sleep(100);
					worked++;
					progressMonitor.worked(1);
					if (worked == count) {
						worked = 0;
						progressMonitor.beginTask(taskName, count);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {
		}
	}
	private class InnerJobThread extends Thread {

		public InnerJobThread() {
			super();
		}

		@Override
		public void run() {
			runWithProgress();
		}
	}

	private void runWithProgress() {
		try {
			runInternal();
		} catch (final Throwable e) {
			internalError = true;
			progressMonitor.setCanceled(true);
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

				public void run() {
					try {
						finalizeJob();
					} catch (Throwable e1) {
						e1.printStackTrace();
					}
					showErrorDialog(e);
				}
			});
		}
		internalComplete = true;
	}

	private void showErrorDialog(Throwable e) {
		if (PlatformUI.getWorkbench() != null) {
			if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null) {
				shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				if (shell != null) {
					VidgeErrorDialog.open(shell, taskName, errorMessage, e);
					return;
					// ErrorDialog.openError(shell, taskName, e.getMessage(),
					// getStatus(e));
				}
			}
		}
		System.out.print(e);
	}

	protected abstract void runInternal() throws Exception;

	protected abstract void processShow();

	protected void finalizeJob() {
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public void setCount(int count) {
		this.count = count;
		isChangedCount = true;
	}

	public void worked() {
		worked = worked + 1;
		progressMonitor.worked(1);
	}

	public void worked(int value) {
		worked = worked + value;
		progressMonitor.worked(value);
	}
}
