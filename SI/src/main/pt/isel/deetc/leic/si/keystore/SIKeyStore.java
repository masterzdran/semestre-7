package pt.isel.deetc.leic.si.keystore;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;


public class SIKeyStore implements IKeyStore {
	private final CertStore _intCer;
	private final KeyStore _rootCert;
	
	public SIKeyStore(CertStore intermediateCertificate, KeyStore rootCertificate){
		_intCer = intermediateCertificate;
		_rootCert = rootCertificate;
	}

	@Override
	public boolean isValid(Certificate certificate) {
		X509CertSelector certToValidate= new X509CertSelector();
		PKIXBuilderParameters builderParams;
		certToValidate.setCertificate((X509Certificate)certificate);
		try {
			builderParams = new PKIXBuilderParameters(_rootCert, certToValidate);
			builderParams.addCertStore(_intCer);
			builderParams.setRevocationEnabled(false);
	        CertPathBuilder builder;
	        builder = CertPathBuilder.getInstance("PKIX");
	        builder.build(builderParams);
	        return true;
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertPathBuilderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
		
	}

	
	public static KeyStore getKeyStore(String filePath, String keyStorePassword, String keyStoreType) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException{
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        FileInputStream keyStoreStream = new FileInputStream(filePath);
        keyStore.load(keyStoreStream,keyStorePassword.toCharArray());
        return keyStore;
    }
    public static Certificate getPublicCertificate(String filePath, String certifcateType) throws CertificateException, FileNotFoundException{
        CertificateFactory Certfactory = CertificateFactory.getInstance(certifcateType);
        Certificate generateCertificate = (Certificate)Certfactory.generateCertificate(new FileInputStream(filePath));
        return generateCertificate;
    }	
	


}
