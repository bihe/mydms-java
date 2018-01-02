package net.binggl.mydms.shared.files

interface FileService {
    /**
	 * save the given file in the backend
	 * @param file
	 * @return
	 */
    fun saveFile(file: FileItem): Boolean
}