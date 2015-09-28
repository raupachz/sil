package org.sil.worker;

public interface WorkersMXBean {
    
    public int getActiveCount();

    public long getCompletedTaskCount();
    
    public long getTaskCount();
    
    public int getCorePoolSize();
    
    public int getMaximumPoolSize();
    
    public int getPoolSize();
    
    public int getLargestPoolSize();
    
    
}
