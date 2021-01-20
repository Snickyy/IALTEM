package impl;
import graph.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;


public class MyThread extends Thread{
    Logger logger = LogManager.getLogger();
    private static SharedQueue sharedQueue = null;

    public MyThread(String name, SharedQueue q){
        super(name);
        if(sharedQueue == null){
            sharedQueue = q;
        }
    }

    @Override
    public void run(){
        while(true){
            //Get the first node of the shared queue
            MyNode mynode;
            try{
                mynode = sharedQueue.getItem();
            } catch (ExceptionItsAStrike e){
                //No more item to handle, end of the thread
                logger.info("Thread n°" + this.getName() + " is out !");
                return;
            }

            //Update the node
            mynode.update();
            //Get its targets
            List<MyNode> targets = mynode.targets;
            //For each node in targets, if the node haven't been visited yet, add it to the shared queue
            for(MyNode node : targets){
                if(!node.isVisited(1)){
                    sharedQueue.add(node);
                }
            }
        }
    }
}
