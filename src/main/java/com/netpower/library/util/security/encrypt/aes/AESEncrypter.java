package com.netpower.library.util.security.encrypt.aes;

/**
 * Encrypt.
 *
 * @author hk
 */
public interface AESEncrypter {

	public void encrypt( byte[] in, int length );

	public byte[] getSalt();

	public byte[] getPwVerification();

	public byte[] getFinalAuthentication();

}
