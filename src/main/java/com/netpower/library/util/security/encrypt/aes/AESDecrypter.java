package com.netpower.library.util.security.encrypt.aes;

/**
 * Decrypt.
 *
 * @author hk
 */
public interface AESDecrypter {

	public void decrypt( byte[] in, int length );

	public byte[] getFinalAuthentication();

}
