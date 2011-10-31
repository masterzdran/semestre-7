package pt.isel.deetc.leic.si;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.security.cert.X509Certificate;

import pt.isel.deetc.leic.si.cipher.SICrypto;
import pt.isel.deetc.leic.si.keystore.SIKeyStore;

public final class Exercise06 {

	private static HashMap<String, Certificate> getCertificateCollection(
			String certificatePath, String certType) {
		HashMap<String, Certificate> hm = new HashMap<String, Certificate>();

		File[] files = loadFiles(certificatePath, "cer");

		for (File f : files) {
			try {
				Certificate certificate = SIKeyStore.getCertificate(
						f.getAbsolutePath(), certType);
				hm.put(f.getName(), certificate);
			} catch (CertificateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return hm;
	}

	private static File[] loadFiles(String path, final String filter) {
		File dir = new File(path);
		FileFilter myFileFilter = new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.getName().endsWith(filter);
			}
		};
		return dir.listFiles(myFileFilter);
	}

	private static HashMap<String, SIKeyStore> getTrustedCollection(
			String trustedPath, String intermediaPath, String certType) {
		HashMap<String, SIKeyStore> hm = new HashMap<String, SIKeyStore>();
		final String jks = "jks";
		final String intermedia = "intermedia.cer";

		// Load JKS FILES
		File[] jksfiles = loadFiles(trustedPath, jks);

		// Load InterMedia FILES
		File[] intermediafiles = loadFiles(intermediaPath, intermedia);

		String larr[] = new String[intermediafiles.length];

		int i = 0;
		for (File f : intermediafiles) {
			larr[i++] = f.getName();
		}

		for (File f : jksfiles) {
			try {
				KeyStore rootCertificate = SIKeyStore.getKeyStore(trustedPath,
						f.getName(), "changeit", jks);
				CertStore intermediateCertificate = SIKeyStore.getCertStore(
						certType, intermediaPath, larr);
				SIKeyStore siks = new SIKeyStore(intermediateCertificate,
						rootCertificate);
				hm.put(f.getName(), siks);
			} catch (CertificateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidAlgorithmParameterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return hm;
	}

	
	
	
	public static void cifer(){}
	public static void decifer(){}
	
	
	public static void write2file(String filename, OutputStream fOut) throws Exception{
    	FileOutputStream f = new FileOutputStream(new File(filename));
    	PrintStream p = new PrintStream(f);
    	p.print(fOut);
    	p.close();
    	f.close();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//String basedPath = "C:/WorkingArea/ISEL/semestre-7/SI/doc/SI-Inv1112-Serie1-Enunciado_Anexos/certificates-and-keys/distr";
		String basedPath = "C:/config/certificates-and-keys/distr";
		String trustedPath = basedPath+"/trustanchors";
		String intermediaPath = basedPath+"/certs.CA.intermediate";
		String certificatePath = basedPath+"/certs.end.entities";
		String file ="readme.txt";
		String certType = "X.509";
		String symKey = "123asd123asd";

		Map<String, SIKeyStore> allStores = getTrustedCollection(trustedPath,
				intermediaPath, certType);
		Map<String, Certificate> allCertificate = getCertificateCollection(
				certificatePath, certType);
		
		SICrypto sc = new SICrypto();
		
		try {
			Certificate certificate = SIKeyStore.getCertificate(certificatePath+"/Alice_1_cipher.cer", certType);
			FileInputStream readFile = new FileInputStream(basedPath+'/'+file);
			SecretKey sk = SICrypto.getSecretKey("AES", 128);
			for (String store : allStores.keySet()) {
				if (allStores.get(store).isValid(certificate)) {
					OutputStream o = sc.cipher(readFile, "Alice_1_cipher.cer", "Alice_1_cipher.cer.metadata", sk, "AES/CBC/PKCS5Padding");
					write2file(basedPath+'/'+"readme.txt.cipher", o);
					System.out.println(o);
					SICrypto.writeMetadata(sk.getEncoded().toString(), "Alice_1_cipher.cer",certificatePath+'/'+"Alice_1_cipher.cer.metadata", "AES/CBC/PKCS5Padding");        
					break;
				}
			}
			
			
			
			FileInputStream inputFile = new FileInputStream(basedPath+'/'+"readme.txt.cipher"); 
			//SecretKey sk = new SecretKeySpec("[B@5f7a8a02".getBytes(), "AES"); // SICrypto.getSecretKey("AES", 128);
			System.out.println(inputFile.toString());
			OutputStream o = sc.decipher( inputFile, "Alice_1_cipher.cer", "Alice_1_cipher.cer.metadata", sk, "AES/CBC/PKCS5Padding");
		
			System.out.println(o);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
		
//		for (String certificateName : allCertificate.keySet()) {
//			Certificate certificate = allCertificate.get(certificateName);
//			for (String store : allStores.keySet()) {
//				if (allStores.get(store).isValid(certificate)) {
//					System.out.println(certificateName + " is valid in "
//							+ store);
//					break;
//				}
//				System.out.println(certificateName + " is not valid in "
//						+ store);
//			}
//		}

	}

}
