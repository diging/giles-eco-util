package edu.asu.diging.gilesecosystem.util.files.impl;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.asu.diging.gilesecosystem.util.exceptions.FileStorageException;
import edu.asu.diging.gilesecosystem.util.files.IFileStorageManager;

public class FileStorageManager implements IFileStorageManager {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String baseDirectory;
    
    private String fileTypeFolder;

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.giles.files.impl.IFileSystemManager#saveFile(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, byte[])
     */
    @Override
    public void saveFile(String username, String uploadId, String documentId,
            String filename, byte[] bytes) throws FileStorageException {
        String filePath = getAndCreateStoragePath(username, uploadId,
                documentId);

        File file = new File(filePath + File.separator + filename);
        BufferedOutputStream stream;
        try {
            stream = new BufferedOutputStream(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            throw new FileStorageException("Could not store file.", e);
        }
        try {
            stream.write(bytes);
            stream.close();
        } catch (IOException e) {
            throw new FileStorageException("Could not store file.", e);
        }
    }

    @Override
    public String getAndCreateStoragePath(String username, String uploadId,
            String documentId) {
        String path = baseDirectory + File.separator 
                + getFileFolderPathInBaseFolder(username, uploadId, documentId);
        createDirectory(path);
        return path;
    }

    /**
     * This method returns the path to a file relative to the file type folder,
     * which is inside the base folder. This method should be used when creating 
     * paths for Digilib (assuming that Digilib is configured to read from the 
     * image folder).
     */
    @Override
    public String getFileFolderPathInTypeFolder(String username, String uploadId,
            String fileId) {
        StringBuffer filePath = new StringBuffer();
        filePath.append(username);
        if (uploadId != null) {
            filePath.append(File.separator);
            filePath.append(uploadId);
        }
        if (fileId != null) {
            filePath.append(File.separator);
            filePath.append(fileId);
        }

        return filePath.toString();
    }
    
    /**
     * This method returns the path to requested file relative to the base folder
     * for all uploaded files (including the file type folder). This method should be used
     * in most cases.
     * 
     * @param username
     * @param uploadId
     * @param fileId
     * @return
     */
    @Override
    public String getFileFolderPathInBaseFolder(String username, String uploadId,
            String fileId) {
        StringBuffer filePath = new StringBuffer();
        filePath.append(fileTypeFolder);
        filePath.append(File.separator);
        filePath.append(username);
        if (uploadId != null) {
            filePath.append(File.separator);
            filePath.append(uploadId);
        }
        if (fileId != null) {
            filePath.append(File.separator);
            filePath.append(fileId);
        }
        
        return filePath.toString();
    }
    
    @Override
    public byte[] getFileContent(String username, String uploadId, String documentId, String filename) {
        String folderPath = getAndCreateStoragePath(username, uploadId, documentId);
        File fileObject = new File(folderPath + File.separator + filename);
        try {
            return getFileContentFromUrl(fileObject.toURI().toURL());
        } catch (IOException e) {
            logger.error("Could not read file.", e);
            return null;
        }
    }
    
    @Override
    public boolean deleteFile(String username, String uploadId, String documentId,
            String filename, boolean deleteEmptyFolders) {
        String path = baseDirectory + File.separator
                + getFileFolderPathInBaseFolder(username, uploadId, documentId);
        File file = new File(path + File.separator + filename);
        
        if (file.exists()) {
            file.delete();
        }
        
        if (deleteEmptyFolders) {
            File docFolder = new File(baseDirectory + File.separator
                    + getFileFolderPathInBaseFolder(username, uploadId, documentId));
            if (docFolder.isDirectory() && docFolder.list().length == 0) {
                boolean deletedDocFolder = docFolder.delete();
                if (deletedDocFolder) {
                    File uploadFolder = new File(baseDirectory + File.separator
                            + getFileFolderPathInBaseFolder(username, uploadId, null));
                    // we now this is a folder because we just deleted docfolder from it
                    // so no need to check
                    if (uploadFolder.exists() && uploadFolder.list().length == 0) {
                        uploadFolder.delete();
                    }
                    
                    uploadFolder = new File(baseDirectory + File.separator
                            + getFileFolderPathInBaseFolder(username, null, null));
                    if (uploadFolder.exists() && uploadFolder.list().length == 0) {
                        uploadFolder.delete();
                    }
                }
            } 
        }
        
        return true;
    }
    
    private byte[] getFileContentFromUrl(URL url) throws IOException {
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
    
    public String getBaseDirectory() {
        return baseDirectory;
    }

    public void setBaseDirectory(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    private boolean createDirectory(String dirPath) {

        File dirFile = new File(dirPath);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        return true;
    }

    public void setFileTypeFolder(String fileTypeFolder) {
        this.fileTypeFolder = fileTypeFolder;
    }

}
