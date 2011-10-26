package pt.isel.deetc.leic.si;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

public final class Exercise06 {
	
	
	private static final String _ALGORITHM="AES";
	private static final short _BLOCKSIZE= 1024;

	public static void decypher(String inputFile) throws FileNotFoundException {
		if (inputFile == null)
			return;
	}

	
	
	
	
	
	public static void cypher(String inputFile,X509Certificate certificate) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		if (inputFile == null)
			return;

		InputStream bio = new FileInputStream(inputFile);
		OutputStream metadata = new FileOutputStream(inputFile + ".metadata");
		OutputStream ciphertext = new FileOutputStream(inputFile + ".ciphertext");

		Cipher cipher = Cipher.getInstance(_ALGORITHM);
		KeyGenerator kg = KeyGenerator.getInstance(_ALGORITHM);
		PublicKey sk = certificate.getPublicKey();
		
		cipher.init(Cipher.ENCRYPT_MODE, sk);
		
		byte[] b = new byte[1024];
	    int length;
		while((length =bio.read(b)) != -1){
			cipher.update(b, 0, length);
		}
		ciphertext.write(cipher.doFinal());
		
		
		bio.close();
		metadata.close();
		ciphertext.close();
	}

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
