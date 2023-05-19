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
    public File createFolder(String username, String uploadId, String documentId,
            String folderName) {
        String filePath = getAndCreateStoragePath(username, uploadId,
                documentId);
        File folder = new File(filePath + File.separator + folderName);
        if (!folder.exists()) {
            folder.mkdir();
        }
        return folder;
    }
    
    @Override
    public void saveFileInFolder(File folder, String filename, byte[] bytes) throws FileStorageException, IOException {
        
        File fFolder = new File(folder.getAbsolutePath());
        if (!fFolder.exists()) {
            fFolder.mkdir();
        }
        File file = new File(folder.getAbsolutePath() + File.separator + filename);
        if (!file.exists()) {
            file.createNewFile();
        }
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
     *      username of user who uploaded a file
     * @param uploadId
     *      id of upload a file was part of
     * @param fileId
     *      Id of the file to get the path to.
     * @return
     *      path to requested file relative to the base folder
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
    
    @Override
    public String getBaseDirectory() {
        return baseDirectory;
    }
    
    @Override
    public String getBaseDirectoryWithFiletype() {
        return baseDirectory + File.separator + fileTypeFolder;
    }
    
    @Override
    public byte[] getExtractedFileContent(String username, String uploadId, String documentId, int pageNr, String filename) {
        String folderPath = getAndCreateStoragePath(username, uploadId, documentId);
        File fileObject = new File(folderPath + File.separator + "extracted" + File.separator + pageNr + File.separator + "extracted" + File.separator + filename);
        try {
            return getFileContentFromUrl(fileObject.toURI().toURL());
        } catch (IOException e) {
            logger.error("Could not read file.", e);
            return null;
        }
    }
    
    @Override
    public boolean deleteExtractedFile(String username, String uploadId, String documentId, int pageNr,
            String filename, boolean deleteEmptyFolders) {
        String folderPath = getAndCreateStoragePath(username, uploadId, documentId);
        File file = new File(folderPath + File.separator + "extracted" + File.separator + pageNr + File.separator + "extracted" + File.separator + filename);
        
        if (file.exists()) {
            file.delete();
        }
        if (deleteEmptyFolders) {
            boolean deletedDocFolder = deleteExtractedFolderForPage(folderPath, pageNr);
            boolean deletedPageNrFolder = deletePageNrFolder(folderPath, pageNr, deletedDocFolder);
            boolean deleteExtractedFolder = deleteExtractedFolder(folderPath, deletedPageNrFolder);
            boolean deleteDownloadFolder = deleteDocumentFolder(folderPath, deleteExtractedFolder);
            deleteUploadFolder(username, uploadId, deleteDownloadFolder);
        }
        
        return true;
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
    
    private boolean deleteExtractedFolderForPage(String folderPath, int pageNr) {
        File docFolder = new File(folderPath + File.separator + "extracted" + File.separator + pageNr + File.separator + "extracted");
        if (docFolder.isDirectory() && docFolder.list().length == 0) {
            return docFolder.delete();
        }
        return !docFolder.exists();
    }
    
    private boolean deletePageNrFolder(String folderPath, int pageNr, boolean extractedFolderDeleted) {
        if (!extractedFolderDeleted) {
            return false;
        }
        File pageNrFolder = new File(folderPath + File.separator + "extracted" + File.separator + pageNr);
        deleteFilesFromFolder(pageNrFolder);
        if (pageNrFolder.exists() && pageNrFolder.list().length == 0) {
            return pageNrFolder.delete();
        }
        return !pageNrFolder.exists();
    }
    
    private boolean deleteExtractedFolder(String folderPath, boolean pageNrFolderDeleted) {
        if (!pageNrFolderDeleted) {
            return false;
        }
        File extractedFolder = new File(folderPath + File.separator + "extracted");
        if (extractedFolder.exists() && extractedFolder.list().length == 0) {
            return extractedFolder.delete();
        }
        return !extractedFolder.exists();
    }

    private boolean deleteDocumentFolder(String folderPath, boolean extractedFolderDeleted) {
        if (!extractedFolderDeleted) {
            return false;
        }
        File documentFolder = new File(folderPath);
        if (documentFolder.exists()) {
            deleteFilesFromFolder(documentFolder);
            return documentFolder.delete();
        }
        return !documentFolder.exists();
    }
    
    private boolean deleteUploadFolder(String username, String uploadId, boolean documentFolderDeleted) {
        if (!documentFolderDeleted) {
            return false;
        }
        File uploadFolder = new File(baseDirectory + File.separator
                + getFileFolderPathInBaseFolder(username, uploadId, null));
        if (uploadFolder.exists() && uploadFolder.list().length == 0) {
            return uploadFolder.delete();
        }
        return !uploadFolder.exists();
    }
    
    private void deleteFilesFromFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }
}
