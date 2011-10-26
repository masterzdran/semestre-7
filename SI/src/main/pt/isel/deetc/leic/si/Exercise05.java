package pt.isel.deetc.leic.si;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Exercise05 {
	private final static int _blockSize = 1024;

	public static void printHexa(byte[] array){
	    // Calculate the digest for the given file.
		StringBuffer hexString = new StringBuffer();
		int i;
		for (i=0;i<array.length-1;i++) {
			hexString.append(Integer.toHexString(0xFF & array[i])+':');
		}
		hexString.append(Integer.toHexString(0xFF & array[i]));
		System.out.println(hexString);
	}
	
	
	public static OutputStream generateHash(String algoritm, InputStream message) throws NoSuchAlgorithmException, IOException{
		MessageDigest m = MessageDigest.getInstance(algoritm);
		OutputStream o = new ByteArrayOutputStream();
		
		byte[] b = new byte[_blockSize];
	    int length;
		while((length = message.read(b)) != -1){
			m.update(b, 0, length);
		}
		byte[] r = m.digest();
		o.write(r);
		o.close();
		return o;
	}
    public static void main(String[] args) throws Exception{

    	InputStream fIn = new FileInputStream("C:/WorkingArea/Computer Security/certificates-and-keys/distr/certs.end.entities/Alice_1_all.cer");
    	OutputStream fOut = generateHash("SHA-1", fIn);
    	
    	printHexa(fOut.toString().getBytes());
    	    	
    	fIn.close();
    	fOut.close();
    
    }


}
