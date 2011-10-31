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
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class SICrypto{
	private static byte[] iv = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F,0x10 };
	
//	private static byte[] encrypt(byte[] inpBytes,SecretKey key, String xform) throws Exception {
//		    //Cipher cipher = Cipher.getInstance(xform);
//		    //IvParameterSpec ips = new IvParameterSpec(iv);
//		    cipher.init(Cipher.ENCRYPT_MODE, key, ips);
//		    return cipher.doFinal(inpBytes);
//		  }
//
//		  private static byte[] decrypt(byte[] inpBytes,SecretKey key, String xform) throws Exception {
//		    //Cipher cipher = Cipher.getInstance(xform);
//		    //IvParameterSpec ips = new IvParameterSpec(iv);
//		    cipher.init(Cipher.DECRYPT_MODE, key, ips);
//		    return cipher.doFinal(inpBytes);
//		  }


	private OutputStream mycipher(InputStream input, String certificateFilename, String metadataFilename, SecretKey key, String transformationAlgorithm, boolean doCiphet) throws Exception {
		Cipher cipher = Cipher.getInstance(transformationAlgorithm);
		IvParameterSpec ips = new IvParameterSpec(iv);
		OutputStream ciphertext = new ByteArrayOutputStream();
		cipher.init((doCiphet)?
						Cipher.ENCRYPT_MODE:
						Cipher.DECRYPT_MODE, 
						key,ips);

		

		
		byte[] b = new byte[128];
	    int length;
		while((length = input.read(b)) != -1){
			cipher.update(b,0,length);
		}
		
		ciphertext.write(cipher.doFinal());

		input.close();
		ciphertext.close();
		
		//writeMetadata(_key, certificateFilename, metadataFilename, certificate, transformationAlgorithm);
		return ciphertext;
		
	}
	
	public OutputStream cipher(InputStream input,String certificateFilename, String metadataFilename, SecretKey key, String transformationAlgorithm)throws Exception 
	{
		return mycipher(input, certificateFilename,metadataFilename,key, transformationAlgorithm, true);
	}


	public OutputStream decipher(InputStream input,String certificateFilename, String metadataFilename, SecretKey key,String transformationAlgorithm)throws Exception
	{
		return mycipher(input, certificateFilename,metadataFilename,key, transformationAlgorithm, false);
	}
	public static SecretKey getSecretKey(String keyGeneratorAlgorithm, int keySize) throws Exception{
		KeyGenerator kg = KeyGenerator.getInstance(keyGeneratorAlgorithm);
		kg.init(keySize);
		return kg.generateKey();		
	}

	public static  void writeMetadata(String symmetricKey,String certificateFilename, String metadataFilename, String transformationAlgorithm)throws Exception
	{

		 Properties config = new Properties();
		 config.put("KEY",symmetricKey);
         config.put("CERTIFICADO", certificateFilename);
         config.put("TRANSFORMATIONALGORITHM", transformationAlgorithm);;
         
         FileOutputStream fileWrite = new FileOutputStream(metadataFilename);
         config.storeToXML(fileWrite,  "Metadata File");
         fileWrite.close();
	}
}
