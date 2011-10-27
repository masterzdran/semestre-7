package pt.isel.deetc.leic.si.cipher;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

import pt.isel.deetc.leic.si.keystore.SIKeyStore;

public class SICrypto implements ICipher {

	private OutputStream cipher(InputStream input, Certificate certificate, String algorihm, boolean doCiphet) 
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(algorihm);
		OutputStream ciphertext = new ByteArrayOutputStream();
		cipher.init((doCiphet)?Cipher.ENCRYPT_MODE:Cipher.DECRYPT_MODE, certificate.getPublicKey());
		
		byte[] b = new byte[1024];
	    int length;
		while((length =input.read(b)) != -1){
			cipher.update(b, 0, length);
		}
		ciphertext.write(cipher.doFinal());
		
		
		input.close();
		ciphertext.close();
		
		return ciphertext;
		
	}
	
	@Override
	public OutputStream cipher(InputStream input, Certificate certificate, String algorihm) 
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException 
	{
		return cipher(input, certificate, algorihm, true);
	}


	@Override
	public OutputStream decipher(InputStream input, Certificate certificate,String algorihm) 	
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException
	{
		return cipher(input, certificate, algorihm, false);
	}


	public OutputStream getMetadata(String filePath, Certificate certificate,String algorithm)
	{
		return null;
	}
}
