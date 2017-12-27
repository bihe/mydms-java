package net.binggl.mydms.features.files

data class FileItem(private val fileName: String, private val mimeType: String,
                    private val payload: Array<Byte>, private val folderName: String) {

    override fun equals(other: Any?): Boolean {
        if (other is FileItem) {
            if (other.fileName == this.fileName &&
                    other.folderName == this.folderName &&
                    other.mimeType == this.mimeType &&
                    other.payload.size == this.payload.size) {
                return true
            }
        }
        return false
    }

    override fun hashCode(): Int {
        var result = fileName.hashCode()
        result = 31 * result + mimeType.hashCode()
        result = 31 * result + folderName.hashCode()
        return result
    }
}