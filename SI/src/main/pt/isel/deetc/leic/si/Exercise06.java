package pt.isel.deetc.leic.si;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String trustedPath = "C:/config/certificates-and-keys/distr/trustanchors";
		String intermediaPath = "C:/config/certificates-and-keys/distr/certs.CA.intermediate";
		String certificatePath = "C:/config/certificates-and-keys/distr/certs.end.entities";
		String certType = "X.509";

		Map<String, SIKeyStore> allStores = getTrustedCollection(trustedPath,
				intermediaPath, certType);
		Map<String, Certificate> allCertificate = getCertificateCollection(
				certificatePath, certType);

		for (String certificateName : allCertificate.keySet()) {
			Certificate certificate = allCertificate.get(certificateName);
			for (String store : allStores.keySet()) {
				if (allStores.get(store).isValid(certificate)) {
					System.out.println(certificateName + " is valid in "
							+ store);
					break;
				}
				System.out.println(certificateName + " is not valid in "
						+ store);
			}
		}

	}

}
