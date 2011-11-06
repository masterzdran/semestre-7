package cancelo.cipher;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class SICipher implements ICipher{
    
    public OutputStream Cipher(String originalfile, String encryptfile, Key key, String algorihm) throws FileNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException{
        return CipherOrDecipher(true, originalfile, encryptfile, key, algorihm );
        /*FileInputStream input = new FileInputStream(originalfile);
        FileOutputStream ciphertext = new FileOutputStream(encryptfile);
        Cipher cipher = Cipher.getInstance(algothrm);
        
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] b = new byte[4*1024];
        int length;
        while((length = input.read(b)) != -1){
            if(length == b.length) ciphertext.write(cipher.update(b, 0, length));
            else ciphertext.write(cipher.doFinal(b,0,length));
        }
        input.close();
        ciphertext.close();
         */
    }
    
    public OutputStream Decipher(String encryptfile, String result, Key key, String algorihm) throws FileNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException{
        /*FileInputStream ciphertext2 =new FileInputStream(encryptfile);
        FileOutputStream out =new FileOutputStream(result);
        
        Cipher cipher2 = Cipher.getInstance(algothrm);
        
        cipher2.init(Cipher.DECRYPT_MODE, key);
        
        byte[] b = new byte[4*1024];
        int length;
        
        while((length = ciphertext2.read(b)) != -1){
            if(length == b.length) out.write(cipher2.update(b, 0, length));
            else out.write(cipher2.doFinal(b,0,length));
        }
        
        ciphertext2.close();
        out.close();*/
        return CipherOrDecipher(false, encryptfile, result, key, algorihm );
    }
    
    private OutputStream CipherOrDecipher(boolean cipher, String encryptordecryptfile, String outputfile, Key key, String algorihm) throws NoSuchAlgorithmException, FileNotFoundException, InvalidKeyException, IOException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException{
        FileInputStream cipherOrdecipher =new FileInputStream(encryptordecryptfile);
        FileOutputStream output =new FileOutputStream(outputfile);
        
        Cipher cipherfile = Cipher.getInstance(algorihm);
        
        cipherfile.init(cipher?Cipher.ENCRYPT_MODE:Cipher.DECRYPT_MODE, key);
        
        byte[] b = new byte[4*1024];
        int length;
        
        while((length = cipherOrdecipher.read(b)) != -1){
            if(length == b.length) output.write(cipherfile.update(b, 0, length));
            else output.write(cipherfile.doFinal(b,0,length));
        }
        
        cipherOrdecipher.close();
        output.close();
        
        return output;
    }
    
}
