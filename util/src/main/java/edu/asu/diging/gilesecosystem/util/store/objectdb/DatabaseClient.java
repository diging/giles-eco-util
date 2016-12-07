package edu.asu.diging.gilesecosystem.util.store.objectdb;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.util.store.IDatabaseClient;
import edu.asu.diging.gilesecosystem.util.store.IPropertiesCopier;
import edu.asu.diging.gilesecosystem.util.store.IStorableObject;

public abstract class DatabaseClient<T extends IStorableObject> implements IDatabaseClient<T> {

    @Autowired
    protected IPropertiesCopier copier;
    
    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.giles.files.impl.IDatabaseClient#generateFileId()
     */
    @Override
    public String generateId() {
        String id = null;
        while (true) {
            id = getIdPrefix() + generateUniqueId();
            Object existingFile = getById(id);
            if (existingFile == null) {
                break;
            }
        }
        return id;
    }
    
    @Override
    public String generateId(String prefix, Function<String, T> f ) {
        String id = null;
        while (true) {
            id = prefix + generateUniqueId();
            T existing = f.apply(id);
            if (existing == null) {
                break;
            }
        }
        return id;
    }
    
    protected List<T> searchByProperty(String propName, String propValue, Class<? extends T> clazz) {
        List<T> results = new ArrayList<T>();
        TypedQuery<? extends T> query = getClient().createQuery("SELECT t FROM " + clazz.getName()  + " t WHERE t." + propName + " = '" + propValue + "'", clazz);
        query.getResultList().forEach(x -> results.add(x));
        return results;
    }
    
    @Override
    public T store(T element) throws UnstorableObjectException {
        if (element.getId() == null) {
            throw new UnstorableObjectException("The object does not have an id.");
        }
        
        EntityManager em = getClient();
        em.persist(element);
        em.flush();
        return element;
    }
    
    @Override
    public void delete(T element) {
        EntityManager em = getClient();
        em.remove(element);
    }
    
    public T update(T element) {
        T existing = getById(element.getId());
        
        copier.copyObject(element, existing);
        return element;
    }

    protected abstract String getIdPrefix();

    protected abstract T getById(String id);
    
    protected abstract EntityManager getClient();

    /**
     * This methods generates a new 6 character long id. Note that this method
     * does not assure that the id isn't in use yet.
     * 
     * Adapted from
     * http://stackoverflow.com/questions/9543715/generating-human-readable
     * -usable-short-but-unique-ids
     * 
     * @return 12 character id
     */
    protected String generateUniqueId() {
        char[] chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
                .toCharArray();

        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            builder.append(chars[random.nextInt(62)]);
        }

        return builder.toString();
    }
}
