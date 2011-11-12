package cancelo.cipher;

import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public interface ICipher {
	public OutputStream Cipher(String originalfile, String encryptfile, Key key, String algorihm) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException;
	public OutputStream Decipher(String originalfile, String encryptfile, Key key, String algorihm) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException;
}
