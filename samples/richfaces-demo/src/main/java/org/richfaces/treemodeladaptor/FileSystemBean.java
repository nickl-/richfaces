package org.richfaces.treemodeladaptor;

import java.io.Serializable;

public class FileSystemBean implements Serializable {
    private static final long serialVersionUID = 1L;
	private static String SRC_PATH = "/WEB-INF/src";
	
	private FileSystemNode[] srcRoots;
	
	public synchronized FileSystemNode[] getSourceRoots() {
		if (srcRoots == null) {
			srcRoots = new FileSystemNode(SRC_PATH).getNodes();
		}
		
		return srcRoots;
	}
}
