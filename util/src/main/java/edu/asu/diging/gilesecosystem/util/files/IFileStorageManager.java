package edu.asu.diging.gilesecosystem.util.files;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import edu.asu.diging.gilesecosystem.util.exceptions.FileStorageException;

public interface IFileStorageManager {

    public abstract void saveFile(String username, String uploadId,
            String fileId, String filename, byte[] bytes)
            throws FileStorageException;

    /**
     * Method that returns the path of a file in the digilib folder structure.
     * Note, this method does not return an absolute path and it does not
     * include the digilib base directory.
     * 
     * @param username 
     *      username of user who uploaded a file
     * @param uploadId
     *      id of upload a file was part of
     * @param documentId
     *      id of document
     * 
     */
    public abstract String getFileFolderPathInTypeFolder(String username, String uploadId,
            String documentId);

    /**
     * Method to get the absolute path to a file directory. This method makes
     * sure that the path exists and all necessary directories are created.
     * 
     * @param username
     *            username of user who uploaded a file
     * @param uploadId
     *            id of upload a file was part of
     * @param documentId
     *            id of document
     * @return absolute path to the file directory
     */
    public abstract String getAndCreateStoragePath(String username,
            String uploadId, String documentId);

    /**
     * This method returns the path to requested file relative to the base folder
     * for all uploaded files (including the file type folder). This method should be used
     * in most cases.
     * 
     * @param username 
     *          username of user who uploaded a file
     * @param uploadId
     *          id of upload a file was part of
     * @param fileId
     *          id of file
     */
    public abstract String getFileFolderPathInBaseFolder(String username, String uploadId,
            String fileId);

    public abstract byte[] getFileContent(String username, String uploadId, String documentId,
            String filename);

    public abstract boolean deleteFile(String username, String uploadId, String documentId,
            String filename, boolean deleteEmptyFolders);

    /**
     * Get content of a file by its URL.
     * 
     * @param url URL of file to get content of
     * 
     * @return byte array of file 
     */
    public abstract byte[] getFileContentFromUrl(URL url) throws IOException;

    public abstract void saveFileInFolder(File folder, String filename, byte[] bytes)
            throws FileStorageException, IOException;

    public abstract File createFolder(String username, String uploadId, String documentId,
            String folderName);

    /**
     * Return configured base directory.
     * 
     * @return configured base directory
     */
    public abstract String getBaseDirectory();

    /**
     * Return configured base directory with configured file type folder.
     * @return configured base directory with configured file type folder
     */
    public abstract String getBaseDirectoryWithFiletype();

}