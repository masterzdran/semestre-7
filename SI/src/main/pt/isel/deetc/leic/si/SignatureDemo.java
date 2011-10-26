package pt.isel.deetc.leic.si;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;


public class SignatureDemo {
	public static byte[] generateSignature(
			FileInputStream input,
			PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException
	{
		Signature signatureFunction = 
			Signature.getInstance("SHA256withRSA");
		signatureFunction.initSign(privateKey);
		
		byte[] data = new byte[1024];
		int nread;
		while ( (nread=input.read(data,0,data.length)) != -1 ) {
			signatureFunction.update(data, 0, nread);
		}
		return signatureFunction.sign();
	}
	
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

	 	
	
//	public static PrivateKey getKeyFromPKCS11Provider(Provider provider) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException{
//		PrivateKey pk;
//		KeyStore ks = KeyStore.getInstance("PKCS11",provider);
//		ks.load(null,null);
//		Enumeration<String> alias = ks.aliases();
//		String aliasname;
//		while(alias.hasMoreElements()){
//			aliasname =alias.nextElement();
//			System.out.println(((X509Certificate)ks.getCertificate(aliasname)).getSubjectAlternativeNames()+ "--- " + aliasname);
//		}
//		pk = (PrivateKey)ks.getKey("bla","bla");
//				
//		return pk;
//	}
	
	
	
	public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, IOException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		PrivateKey privateKey = generator.generateKeyPair().getPrivate();
		
		FileInputStream in1 = new FileInputStream("d:/apresentacao1.pptx");
		FileInputStream in2 = new FileInputStream("d:/apresentacao2.pptx");
		
		FileOutputStream out1 = new FileOutputStream("d:/apresentacao1.sign");
		FileOutputStream out2 = new FileOutputStream("d:/apresentacao2.sign");
		
		out1.write(generateSignature(in1, privateKey));
		out1.close();
		
		out2.write(generateSignature(in2, privateKey));
		out2.close();
	}
}


























