package cancelo.keystore;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreParameters;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class ValidateCertificates implements IValidate {
    private final CertStore _intCer;
    private final KeyStore _rootCert;
    public ValidateCertificates(CertStore intermediateCertificate, KeyStore rootCertificate){
            _intCer = intermediateCertificate;
            _rootCert = rootCertificate;
    }
    @Override
    public boolean isValid(Certificate certificate) {
            boolean result;
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
            result= true;
            } catch (KeyStoreException e) {
                    result= false;
            } catch (InvalidAlgorithmParameterException e) {
                    result= false;
            } catch (CertPathBuilderException e) {
                    result= false;
            } catch (NoSuchAlgorithmException e) {
                    result= false;
            }
                    return result;
    }
    public static KeyStore getKeyStore(String filePath,String filename, String keyStorePassword, String keyStoreType) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException{
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        FileInputStream keyStoreStream = new FileInputStream(filePath+'/'+filename);
        keyStore.load(keyStoreStream,keyStorePassword.toCharArray());
        return keyStore;
    }
    public static CertStore getCertStore(String type, String path, String certificateNameList[]) 
                    throws CertificateException, FileNotFoundException, InvalidAlgorithmParameterException, NoSuchAlgorithmException{
        CertificateFactory cf = CertificateFactory.getInstance(type);
        List<Certificate> mylist = new ArrayList<Certificate>();
        for (int i = 0; i < certificateNameList.length; i++) {
          InputStream in = new FileInputStream(path+'/'+certificateNameList[i]);
          Certificate c = cf.generateCertificate(in);
          mylist.add(c);
        }
        CertStoreParameters cparam = new CollectionCertStoreParameters(mylist);
        return  CertStore.getInstance("Collection", cparam);
    }
    
    public static Certificate getCertificate(String filePath, String certifcateType) throws CertificateException, FileNotFoundException{
        CertificateFactory Certfactory = CertificateFactory.getInstance(certifcateType);
        Certificate generateCertificate = (Certificate)Certfactory.generateCertificate(new FileInputStream(filePath));
        return generateCertificate;
    }
    
    public static SecretKey GenerateSecretKey(String transformationAlgorithm, int size) throws Exception{
    	KeyGenerator kg = KeyGenerator.getInstance(transformationAlgorithm);
    	kg.init(size);
    	return kg.generateKey();
    }
   
    
    public static PrivateKey GetPrivateKey(String certificate) throws KeyStoreException, FileNotFoundException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException{
        KeyStore store = KeyStore.getInstance("pkcs12");
        FileInputStream pfx = new FileInputStream(certificate);
        char[] password = {'c','h','a','n','g','e','i','t'};
        store.load(pfx, password);
        pfx.close();
        Enumeration<String> e = store.aliases();e.hasMoreElements();
        String alias = e.nextElement();
	return (PrivateKey)store.getKey(alias, password);
    }
}
