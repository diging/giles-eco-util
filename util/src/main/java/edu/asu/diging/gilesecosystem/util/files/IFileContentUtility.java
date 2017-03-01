package edu.asu.diging.gilesecosystem.util.files;

import java.io.IOException;
import java.net.URL;

public interface IFileContentUtility {

    public abstract byte[] getFileContentFromUrl(URL url) throws IOException;

}