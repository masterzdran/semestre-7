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

import cancelo.cipher.SICrypto;
import cancelo.keystore.SIKeyStore;

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

    public static void write2file(String filename, OutputStream fOut) throws Exception {
        FileOutputStream f = new FileOutputStream(new File(filename));
        PrintStream p = new PrintStream(f);
        p.print(fOut);
        p.close();
        f.close();
    }

    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub

        String original = "/home/elvisp/Desktop/Exercise06.java";
        String cipher = "/home/elvisp/Desktop/Exercise06-cipher.java";
        String decipher = "/home/elvisp/Desktop/Exercise06-result.java";
        SecretKey key = SIKeyStore.getSecretKey("AES", 128);

        SICrypto si = new SICrypto();
        FileInputStream fis = new FileInputStream(original);

        OutputStream out = si.cipher(fis, key, "AES/ECB/PKCS5Padding");
        write2file(cipher, out);

        fis.close();

        fis = new FileInputStream(cipher);
        out = si.decipher(fis, key, "AES/ECB/PKCS5Padding");
        write2file(decipher, out);

        fis.close();

        /*String basedPath = "C:/WorkingArea/ISEL/semestre-7/SI/doc/SI-Inv1112-Serie1-Enunciado_Anexos/certificates-and-keys/distr";
        String trustedPath = basedPath+"/trustanchors";
        String intermediaPath = basedPath+"/certs.CA.intermediate";
        String certificatePath = basedPath+"/certs.end.entities";
        String file ="readme.txt";
        String certType = "X.509";

        String transformationAlgorithm = "AES/ECB/PKCS5Padding";
        int keysize = 128;

        Map<String, SIKeyStore> allStores = getTrustedCollection(trustedPath,
        intermediaPath, certType);
        Map<String, Certificate> allCertificate = getCertificateCollection(
        certificatePath, certType);

        SICrypto sc = new SICrypto();
        SecretKey key;

        try {
        Certificate certificate = SIKeyStore.getCertificate(certificatePath+"/Alice_1_cipher.cer", certType);
        FileInputStream readFile = new FileInputStream(basedPath+'/'+file);
        key = SIKeyStore.getSecretKey("AES", keysize);
        for (String store : allStores.keySet()) {
        if (allStores.get(store).isValid(certificate)) {
        OutputStream o = sc.cipher(readFile, "Alice_1_cipher.cer", certificatePath+"/Alice_1_cipher.cer.metadata", key, transformationAlgorithm);
        write2file(basedPath+'/'+file+".cifred", o);
        SICrypto.writeMetadata(key.getEncoded(), "Alice_1_cipher.cer", certificatePath+"/Alice_1_cipher.cer.metadata", transformationAlgorithm);
        //					System.out.println(o);
        //					o.flush();

        break;
        }
        }
        readFile.close();
        FileInputStream readFile2 = new FileInputStream("");
        OutputStream o = sc.decipher(readFile2, "Alice_1_cipher.cer", certificatePath+"/Alice_1_cipher.cer.metadata", key, transformationAlgorithm);
        System.out.println("--->"+o);
        write2file(basedPath+'/'+file+".decifred", o);
        } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        }*/

    }
}
