package net.binggl.mydms.features.shared.crypto;

import org.apache.commons.codec.digest.DigestUtils;

public class HashHelper {

	public static String getSHA(String... params) {
		if (params != null && params.length > 0) {
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < params.length; i++) {
				if (i > 0)
					buffer.append("|");
				buffer.append(params[i]);
			}
			return DigestUtils.sha256Hex(buffer.toString());
		}
		return null;
	}
}
