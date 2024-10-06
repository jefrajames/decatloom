package io.jefrajames.loombench;

import java.util.concurrent.Semaphore;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DbPool {

    @Inject
    @ConfigProperty(name = "db.pool.size", defaultValue = "100")
    private int dbPoolSize;

    private Semaphore pool;

    @PostConstruct
    public void postConstruct() {
        pool = new Semaphore(dbPoolSize, true);
        System.out.println("dbPoolSize=" + dbPoolSize);
    }

    public void acquireCon() {
        pool.acquireUninterruptibly();
    }

    public void releaseCon() {
        pool.release();
    }

    public int availableCon() {
        return pool.availablePermits();
    }

    public int getDbPoolSize() {
        return dbPoolSize;
    }

    public int getDBPoolUsed() {
        return dbPoolSize - pool.availablePermits();        
    }

}