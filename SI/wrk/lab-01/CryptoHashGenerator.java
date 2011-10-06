import java.security.*;
import java.io.*;

public final class CryptoHashGenerator
{
	public static OutputStream generateCryptoHash(String hashFunction, InputStream is, OutputStream os) 
		throws NoSuchAlgorithmException, IOException
	{
		MessageDigest mDigest = MessageDigest.getInstance(hashFunction);	
		byte[] buffer = new byte[1024];
		while( is.read(buffer)  != -1 ) 
		{
			mDigest.update(buffer);
		}
		os.write(mDigest.digest());
		os.flush();
		return os;
	}
	
	public static void main(String[] args) throws Exception
	{
		/* Usage: <hash_function_name> <filename> */
		
		// Validate args ( NOT DONE )
		
		FileInputStream fis = new FileInputStream(new File(args[1]));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		generateCryptoHash(args[0],fis, bos);
		fis.close();
		System.out.println(bos);
		bos.close();
	}
}
