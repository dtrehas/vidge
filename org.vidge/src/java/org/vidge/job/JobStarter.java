package org.vidge.job;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

public class JobStarter {

	public static void startJob(AbstractJob abstractJob) {
		IProgressService service = PlatformUI.getWorkbench().getProgressService();
		service.showInDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), abstractJob);
		abstractJob.schedule();
	}

	public static void startJobSilent(AbstractJob abstractJob) {
		abstractJob.schedule();
	}
}
