package net.binggl.mydms.features.files;

public interface FileService {

	/**
	 * save the given file in the backend
	 * @param file
	 * @return
	 */
	boolean saveFile(FileItem file);
}
