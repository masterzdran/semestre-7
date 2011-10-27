package pt.isel.deetc.leic.si.cipher;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Properties;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class SICrypto{

	private OutputStream mycipher(InputStream input, String certificateFilename, String metadataFilename, Certificate certificate, String algorithm, boolean doCiphet) 
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(algorithm);
		OutputStream ciphertext = new ByteArrayOutputStream();
		byte[] _key = new byte[certificate.getPublicKey().l];
		new Random().nextBytes(_key);
		SecretKeySpec key = new SecretKeySpec(_key, "AES");
		cipher.init((doCiphet)?Cipher.ENCRYPT_MODE:Cipher.DECRYPT_MODE, key);
		
		byte[] b = new byte[1024];
	    int length;
		while((length = input.read(b)) != -1){
			cipher.update(b, 0, length);
		}
		ciphertext.write(cipher.doFinal());
		
		
		input.close();
		ciphertext.close();
		
		writeMetadata(_key, certificateFilename, metadataFilename, certificate, algorithm);
		return ciphertext;
		
	}
	
	public OutputStream cipher(InputStream input,String certificateFilename, String metadataFilename, Certificate certificate, String algorihm) 
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException 
	{
		return mycipher(input, metadataFilename,certificateFilename,certificate, algorihm, true);
	}


	public OutputStream decipher(InputStream input,String certificateFilename, String metadataFilename, Certificate certificate,String algorihm) 	
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException
	{
		return mycipher(input, certificateFilename,metadataFilename,certificate, algorihm, false);
	}


	private  void writeMetadata(byte[] symmetricKey,String certificateFilename, String metadataFilename, Certificate certificate,String algorithm) 
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException
	{
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.ENCRYPT_MODE, certificate);
		byte[] b = cipher.doFinal(symmetricKey);

		 Properties config = new Properties();
		 config.put("KEY", b);
         config.put("CERTIFICADO", certificateFilename);
         
         FileOutputStream fileWrite = new FileOutputStream(metadataFilename);
         config.storeToXML(fileWrite,  "Metadata File");
         fileWrite.close();
	}
}
