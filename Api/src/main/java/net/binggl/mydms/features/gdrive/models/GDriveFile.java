package net.binggl.mydms.features.gdrive.models;

import java.util.Arrays;

public class GDriveFile extends GDriveItem {

	private byte[] payload;

	public GDriveFile() {
		super();
	}

	public GDriveFile(String id, String name, String parent, String mimeType, byte[] payload) {
		super(id, name, parent, mimeType);
		this.payload = payload;
	}

	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Arrays.hashCode(payload);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		GDriveFile other = (GDriveFile) obj;
		if (!Arrays.equals(payload, other.payload))
			return false;
		return true;
	}

	@Override
	public String toString() {
		long size = payload != null ? payload.length : 0;
		return "GDriveFile [id=" + id + ", name=" + name + ", mimeType=" + mimeType + ", payloadSize=" + size + "]";
	}
}
