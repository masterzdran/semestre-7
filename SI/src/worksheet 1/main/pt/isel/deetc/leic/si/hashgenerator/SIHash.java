package pt.isel.deetc.leic.si.hashgenerator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SIHash {
	private final static int _blockSize = 1024;
	public static void printHexa(byte[] array){
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

}
