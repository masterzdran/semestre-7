package pt.isel.deetc.leic.si;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import pt.isel.deetc.leic.si.hashgenerator.SIHash;

public final class Exercise05 {
    public static void main(String[] args) throws Exception{
    	String path = "C:/config/certificates-and-keys/distr/certs.end.entities";

    	InputStream fIn = new FileInputStream(path+"/Alice_1_all.cer");
    	OutputStream fOut = SIHash.generateHash("SHA-1", fIn);
    	
    	SIHash.printHexa(fOut.toString().getBytes());
    	

    	/**
    	 * Example how to write in file
    	 */
//    	FileOutputStream f = new FileOutputStream(new File(path+"/XPTO.txt"));
//    	PrintStream p = new PrintStream(f);
//    	p.print(fOut.toString().getBytes());
//    	p.close();
//    	f.close();
    	
    	fIn.close();
    	fOut.close();
    
    }


}
