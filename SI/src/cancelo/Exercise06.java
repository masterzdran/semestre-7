package cancelo;

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

import javax.crypto.SecretKey;

import cancelo.cipher.Metadata;
import cancelo.cipher.SICipher;
import cancelo.keystore.ValidateCertificates;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.security.PrivateKey;

public final class Exercise06 {

    private static HashMap<String, Certificate> getCertificateCollection(
            String certificatePath, String certType) {
        HashMap<String, Certificate> hm = new HashMap<String, Certificate>();

        File[] files = loadFiles(certificatePath, "cer");

        for (File f : files) {
            try {
                Certificate certificate = ValidateCertificates.getCertificate(
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

    private static HashMap<String, ValidateCertificates> getTrustedCollection(
            String trustedPath, String intermediaPath, String certType) {
        HashMap<String, ValidateCertificates> hm = new HashMap<String, ValidateCertificates>();
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
                KeyStore rootCertificate = ValidateCertificates.getKeyStore(trustedPath,
                        f.getName(), "changeit", jks);
                CertStore intermediateCertificate = ValidateCertificates.getCertStore(
                        certType, intermediaPath, larr);
                ValidateCertificates siks = new ValidateCertificates(intermediateCertificate,
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

    public static void write2file(String filename, OutputStream fOut) throws Exception {
        FileOutputStream f = new FileOutputStream(new File(filename));
        PrintStream p = new PrintStream(f);
        p.print(fOut);
        p.close();
        f.close();
    }

    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        String basedPath = "D:/ISEL/semestre-7/SI/src";//;"C:/WorkingArea/ISEL/semestre-7/SI/doc/SI-Inv1112-Serie1-Enunciado_Anexos/certificates-and-keys/distr";
        String trustedPath = basedPath+"/trustanchors";
        String intermediaPath = basedPath+"/certs.CA.intermediate";
        String certificatePath = basedPath+"/certs.end.entities";
        
        String originalfile = "C:\\Users\\ElvisP\\Desktop\\Exercise06.java";//"/home/elvisp/Desktop/Exercise06.java";
        String encryptfile = "C:\\Users\\ElvisP\\Desktop\\Exercise06-cipher.java";//"/home/elvisp/Desktop/Exercise06-cipher.java";
        String result = "C:\\Users\\ElvisP\\Desktop\\Exercise06-result.java";//"/home/elvisp/Desktop/Exercise06-result.java";
        
        String metadata = "C:\\Users\\ElvisP\\Desktop\\metadata.xml";
        String metadataencrypt = "C:\\Users\\ElvisP\\Desktop\\metadata-cipher.xml";
        String metadataresult = "C:\\Users\\ElvisP\\Desktop\\metadata-cipher.xml";
        
        String certType = "X.509";
        String typeSecretkey = "AES", transformationAlgorithm = "AES/ECB/PKCS5Padding";
        int keysize = 128;

        Map<String, ValidateCertificates> allStores = getTrustedCollection(trustedPath,
        intermediaPath, certType);
        
        SICipher sicipher = new SICipher();
        SecretKey key;

        try {
                Certificate certificate = ValidateCertificates.getCertificate(
                            certificatePath+"/Alice_1_cipher.cer", certType);
                key = ValidateCertificates.GenerateSecretKey(typeSecretkey, keysize);
                for (String store : allStores.keySet()) {
                    if (allStores.get(store).isValid(certificate)) {
                        sicipher.Cipher(originalfile, encryptfile, key, transformationAlgorithm);
                        Metadata.writeMetadata(key.getEncoded(), certificate.toString(), metadata, transformationAlgorithm);
                        sicipher.Cipher(metadata, metadataencrypt, certificate.getPublicKey(), certificate.getPublicKey().getAlgorithm()) ;
                        break;
                    }
                }
                
                PrivateKey privkey = ValidateCertificates.GetPrivateKey(certificatePath+"/Alice_1_cipher.cer");
                
                //decifrar metadata
                sicipher.Decipher(metadataencrypt, metadataresult, privkey , certificate.getPublicKey().getAlgorithm()); 
                //metadataresult
                
                //obter algortimo e chave 
                String alg, symmetrickey;
                FileInputStream fstream = new FileInputStream(metadataresult);
                BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));
                symmetrickey = br.readLine();
                alg = br.readLine();
                
                //decifrar ficheiro original              
                
                OutputStream o = null;//sicipher.Decipher(encryptfile, result, , alg);
                System.out.println("--->"+o);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
}
