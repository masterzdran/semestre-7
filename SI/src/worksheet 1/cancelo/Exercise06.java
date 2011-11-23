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
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

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
        String metadataresult = "C:\\Users\\ElvisP\\Desktop\\metadata-result.xml";
        
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
                        System.out.println("Certificate it was validated: " + certificate);
                            
                        System.out.println("Encrypt message on file: " + originalfile);
                        sicipher.Cipher(originalfile, encryptfile, key, transformationAlgorithm);
                        
                        System.out.println("Creating Metadata on file:" + metadata);
                        Metadata.writeMetadata(key.getEncoded(), certificate.toString(), metadata, transformationAlgorithm);
                        
                        System.out.println("Encrypt metadata on file: " + metadataencrypt);
                        sicipher.Cipher(metadata, metadataencrypt, certificate.getPublicKey(), certificate.getPublicKey().getAlgorithm()) ;
                        break;
                    }
                }
                
                
                
                System.out.println("\n\nGet private key of pfx");
                PrivateKey privkey = ValidateCertificates.GetPrivateKey(basedPath+"/pfx/Alice_1_cipher.pfx");
                
                //decifrar metadata
                System.out.println("Decrypt metadata on file: " + metadataresult);
                sicipher.Decipher(metadataencrypt, metadataresult, privkey , certificate.getPublicKey().getAlgorithm()); 
                //metadataresult
                
                System.out.println("Getting key and algorithm.");
                //obter algortimo e chave 
                String alg, symmetrickey;
                FileInputStream fstream = new FileInputStream(metadataresult);
                BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));
                symmetrickey = br.readLine();
                alg = br.readLine();
                
                //decifrar ficheiro original              
                SecretKeySpec sks = new SecretKeySpec(symmetrickey.getBytes(), typeSecretkey);
                                
                //da excepcao com sks: java.security.InvalidKeyException: Invalid AES key length: 26 bytes
                //OutputStream o = sicipher.Decipher(encryptfile, result, sks , alg);
                OutputStream o = sicipher.Decipher(encryptfile, result, key , alg);
                System.out.println("Decrypt message on file: " + result);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
}
