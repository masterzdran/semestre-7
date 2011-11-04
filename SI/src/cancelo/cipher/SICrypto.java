package cancelo.cipher;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import javax.crypto.Cipher;

import javax.crypto.SecretKey;


public class SICrypto{
    
	private OutputStream mycipher(InputStream input, SecretKey key, String transformationAlgorithm, boolean doCiphet) throws Exception {
		Cipher cipher = Cipher.getInstance(transformationAlgorithm);
		OutputStream ciphertext = new ByteArrayOutputStream();
		cipher.init((doCiphet)?Cipher.ENCRYPT_MODE:Cipher.DECRYPT_MODE, key);
		byte[] b = new byte[16];
                int length;
                while((length = input.read(b)) != -1){
                    if(length == b.length) ciphertext.write(cipher.update(b, 0, length));
                    else ciphertext.write(cipher.doFinal(b,0,length));
                }
		//input.close();
		ciphertext.flush();
		ciphertext.close();
		return ciphertext;
		
	}
	
	public OutputStream cipher(InputStream input, SecretKey key, String transformationAlgorithm) throws Exception 
	{
                Cipher cipher = Cipher.getInstance(transformationAlgorithm);

		//OutputStream ciphertext = new ByteArrayOutputStream();
                String encryptfile = "/home/elvisp/Desktop/Exercise06-cipher.java";
                FileOutputStream ciphertext = new FileOutputStream(encryptfile);

		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] b = new byte[4*1024];
                int length;
                while((length = input.read(b)) != -1){
                    if(length == b.length) ciphertext.write(cipher.update(b, 0, length));
                    else ciphertext.write(cipher.doFinal(b,0,length));
                }

		ciphertext.close();
		return ciphertext;
	}


	public OutputStream decipher(InputStream input,SecretKey key,String transformationAlgorithm)throws Exception
	{
		Cipher cipher = Cipher.getInstance(transformationAlgorithm);
		//OutputStream ciphertext = new ByteArrayOutputStream();
                String encryptfile = "/home/elvisp/Desktop/Exercise06-cipher.java";
                FileOutputStream ciphertext = new FileOutputStream(encryptfile);
                
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] b = new byte[4*1024];
                int length;
                while((length = input.read(b)) != -1){
                    if(length == b.length) ciphertext.write(cipher.update(b, 0, length));
                    else ciphertext.write(cipher.doFinal(b,0,length));
                }

		ciphertext.close();
		return ciphertext;
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
