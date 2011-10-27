package pt.isel.deetc.leic.si;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import pt.isel.deetc.leic.si.hashgenerator.SIHash;

public final class Exercise05 {
    public static void main(String[] args) throws Exception{

    	InputStream fIn = new FileInputStream("C:/WorkingArea/Computer Security/certificates-and-keys/distr/certs.end.entities/Alice_1_all.cer");
    	OutputStream fOut = SIHash.generateHash("SHA-1", fIn);
    	
    	SIHash.printHexa(fOut.toString().getBytes());
    	    	
    	fIn.close();
    	fOut.close();
    
    }


}
