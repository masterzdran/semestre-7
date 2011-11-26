package cancelo.cipher;

import java.io.FileOutputStream;
import java.util.Properties;

public class Metadata{
    
    public static void writeMetadata(byte[] symmetricKey, String certificateFilename, String metadataFilename, String transformationAlgorithm) throws Exception
    {
         FileOutputStream fileWrite = new FileOutputStream(metadataFilename);
         
//         fileWrite.write((certificateFilename+"\n").getBytes());
         fileWrite.write(symmetricKey);
         fileWrite.write("\n".getBytes());
         fileWrite.write((transformationAlgorithm+"\n").getBytes());
         
         fileWrite.close();
         /*Properties config = new Properties();
         config.put("KEY", new String(symmetricKey));
         config.put("CERTIFICADO", certificateFilename);
         config.put("ALGORITMO", transformationAlgorithm);
         FileOutputStream fileWrite = new FileOutputStream(metadataFilename);
         config.storeToXML(fileWrite,  "Metadata File");
         fileWrite.close();*/
    }
}
