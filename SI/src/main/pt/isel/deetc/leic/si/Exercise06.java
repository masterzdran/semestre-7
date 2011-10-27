package pt.isel.deetc.leic.si;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
    	p.print(fOut.toString().getBytes());
    	p.close();
    	f.close();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String basedPath = "C:/WorkingArea/ISEL/semestre-7/SI/doc/SI-Inv1112-Serie1-Enunciado_Anexos/certificates-and-keys/distr";
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
			
			for (String store : allStores.keySet()) {
			if (allStores.get(store).isValid(certificate)) {
				OutputStream o = sc.cipher(readFile, "Alice_1_cipher.cer", "Alice_1_cipher.cer.metadata", certificate, "AES");
				write2file("readme.txt.cipher", o);
				//sc.writeMetadata(symKey, "Alice_1_cipher.cer", "Alice_1_cipher.cer.metadata", certificate, "AES");        
				break;
			}
			}
		
			
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
