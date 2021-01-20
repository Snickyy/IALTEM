package impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author V. Englebert
 * @version 2.0 by F. Snickers
 */
public class SharedQueue<E> {
    Logger logger = LogManager.getLogger();
    private List<MyNode> queue=new LinkedList<>();
    private Integer nbOfThread;

    public SharedQueue(Integer nb){
        nbOfThread=nb;
    }


    /**
     * Add an item to the queue
     * @param item the item to add to the queue
     */
    public synchronized void add(MyNode item){
        assert item!=null;
        //Mark it as visited
        item.markVisited(1);
        queue.add(item);
        notify();
    }

    /**
     * Get the first item in the queue and return it
     * @return the first item of the list
     * @throws ExceptionItsAStrike notify the Thread that there is no more item to handle
     */
    public synchronized MyNode getItem() throws ExceptionItsAStrike{
        while (queue.isEmpty()){
            //If the current thread is the only one waiting, then there is no more item that will be coming
            if(nbOfThread==1){
                //Reduce the number of thread, notify all and throw an exception to notify the thread to finish
                nbOfThread--;
                notifyAll();
                throw new ExceptionItsAStrike();
            }
            //If another thread is doing something somewhere, just wait
            try {
                nbOfThread--; //Reduce the number of active thread
                logger.info("Thread n°"+Thread.currentThread().getName()+" falls asleep");
                wait();
                logger.info("Thread n°"+Thread.currentThread().getName()+" wakes up");
                nbOfThread++; //Increase the number of
            } catch (InterruptedException e) {
                assert false;
                throw new UnknownError();
            }
        }

        //NotifyAll threads and return the first node of the queue
        notifyAll();
        return queue.remove(0);
    }

    /**
     *
     * @return the size of the queue
     */
    public synchronized int size() {
        return queue.size();
    }
}
