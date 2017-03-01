package edu.asu.diging.gilesecosystem.util.files.impl;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.springframework.stereotype.Component;

import edu.asu.diging.gilesecosystem.util.files.IFileContentUtility;

@Component
public class FileContentUtility implements IFileContentUtility {

    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.util.files.impl.IFileContentUtility#getFileContentFromUrl(java.net.URL)
     */
    @Override
    public byte[] getFileContentFromUrl(URL url) throws IOException {
        URLConnection con = url.openConnection();
        
        InputStream input = con.getInputStream();

        byte[] buffer = new byte[4096];
        
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        BufferedOutputStream output = new BufferedOutputStream(byteOutput);
       
        int n = -1;
        while ((n = input.read(buffer)) != -1) {
            output.write(buffer, 0, n);
        }
        input.close();
        output.flush();
        output.close();
        
        byteOutput.flush();
        byte[] bytes = byteOutput.toByteArray();
        byteOutput.close();
        return bytes;
    }
}
