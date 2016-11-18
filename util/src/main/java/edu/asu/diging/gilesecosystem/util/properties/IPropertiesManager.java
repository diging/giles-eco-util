package edu.asu.diging.gilesecosystem.util.properties;

import java.util.Map;

import edu.asu.diging.gilesecosystem.util.exceptions.PropertiesStorageException;

public interface IPropertiesManager {
    
    public abstract void setProperty(String key, String value) throws PropertiesStorageException;

    public abstract String getProperty(String key);

    public abstract void updateProperties(Map<String, String> props)
            throws PropertiesStorageException;

}
