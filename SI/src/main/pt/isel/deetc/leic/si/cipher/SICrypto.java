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
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class SICrypto{

	private OutputStream mycipher(InputStream input, String certificateFilename, String metadataFilename, SecretKey key, String transformationAlgorithm, boolean doCiphet) throws Exception {
		Cipher cipher = Cipher.getInstance(transformationAlgorithm);
		OutputStream ciphertext = new ByteArrayOutputStream();

		cipher.init((doCiphet)?Cipher.ENCRYPT_MODE:Cipher.DECRYPT_MODE, key);



		byte[] b = new byte[4*1024];
	    int length;
	    
		while((length = input.read(b)) != -1){
			ciphertext.write(cipher.update(b, 0, length));
		}
		ciphertext.write(cipher.doFinal());
		
		
		//input.close();
		ciphertext.flush();
		ciphertext.close();
		

		return ciphertext;
		
	}
	
	public OutputStream cipher(InputStream input,String certificateFilename, String metadataFilename, SecretKey key, String transformationAlgorithm) throws Exception 
	{
		return mycipher(input, certificateFilename,metadataFilename,key, transformationAlgorithm, true);
	}


	public OutputStream decipher(InputStream input,String certificateFilename, String metadataFilename,SecretKey key,String transformationAlgorithm)throws Exception
	{
		return mycipher(input, certificateFilename,metadataFilename,key, transformationAlgorithm, false);
	}


	public static  void writeMetadata(byte[] symmetricKey,String certificateFilename, String metadataFilename, String transformationAlgorithm) throws Exception
	{
//		Cipher cipher = Cipher.getInstance(algorithm);
//		cipher.init(Cipher.ENCRYPT_MODE, certificate);
//		byte[] b = cipher.doFinal(symmetricKey);

		 Properties config = new Properties();
		 config.put("KEY", new String(symmetricKey));
         config.put("CERTIFICADO", certificateFilename);
         config.put("ALGORITMO", transformationAlgorithm);
         FileOutputStream fileWrite = new FileOutputStream(metadataFilename);
         config.storeToXML(fileWrite,  "Metadata File");
         fileWrite.close();
	}
}
