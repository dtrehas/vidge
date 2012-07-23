package org.vidge.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.eclipse.ui.PlatformUI;
import org.langcom.io.TransferUtils;
import org.langcom.log.LookLog;

/**
 * Purposed for total update of RCP applications Creates in application folder 'temp folder where copied from network whole new application folder ('eclipse') and file TotalUpdate.jar - tool for
 * replacing an old application by new one and starting a new application (if executable path is not null)
 * 
 * @author nemo
 * 
 */
public class TotalUpdateManager extends Thread {

	private final ServerSocket serverSocket;
	private String pathToProject, pathToTemp, pathToTempNew, pathToTotalUpdate, executablePath;
	private Socket accept;

	/**
	 * 
	 * @param serverSocket
	 * @param pathToProject
	 * @param executablePath
	 */
	public TotalUpdateManager(ServerSocket serverSocket, String pathToProject, String executablePath) {
		this.serverSocket = serverSocket;
		this.pathToProject = pathToProject;
		this.executablePath = executablePath;
		pathToTemp = pathToProject + File.separator + "tt";
		start();
	}

	@Override
	public void run() {
		LookLog.info(this.getClass().getName(), "***************Starting update listener  on : " + serverSocket.getLocalPort() + "  path to temp: " + pathToTemp);
		try {
			accept = serverSocket.accept();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			File directory = new File(pathToTemp);
			if (!directory.exists()) {
				directory.mkdirs();
			} else {
				FileUtils.cleanDirectory(directory);
			}
			InputStream in = accept.getInputStream();
			TransferUtils.receive(in, pathToTemp);
			File[] listFiles = directory.listFiles();
			for (File file : listFiles) {
				if (file.isDirectory()) {
					pathToTempNew = file.getPath();
				} else {
					pathToTotalUpdate = file.getPath();
				}
			}
			Logger.getLogger(this.getClass().getName()).info("***************Download completed - to  " + pathToTemp);
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
					runUpdate();
				}
			});
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			LookLog.error(this.getClass().getName(), "**888Err", ex);
		}
	}

	private void runUpdate() {
		try {
			PlatformUI.getWorkbench().close();
			Runtime.getRuntime().exec(new String[] {
					"javaw", "-jar", pathToTotalUpdate, pathToProject, pathToTemp, pathToTempNew, executablePath
			});
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			LookLog.error(this.getClass().getName(), "**8Err", e);
		}
	}
}
