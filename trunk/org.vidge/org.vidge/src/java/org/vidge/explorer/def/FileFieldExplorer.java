package org.vidge.explorer.def;

import java.io.File;

public class FileFieldExplorer extends AbstractFieldExplorer<File> {

	public FileFieldExplorer(File obj, String labelIn) {
		super(obj, labelIn);
	}

	@Override
	public Class<?> getPropertyClass() {
		return File.class;
	}
}