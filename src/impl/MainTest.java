package impl;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import graph.ExceptionSyntaxError;
import graph.GraphFacilities;
import impl.SharedQueue;
import impl.MyThread;
import java.util.LinkedList;
import java.util.List;

public class MainTest {
	static public void main(String args[]) {
		Configurator.setRootLevel(Level.TRACE);
		Logger logger = LogManager.getLogger();

		System.out.println("Usage: filein.csv fileout.csv nbrthreads");

		// check arguments

		int i = 0;
		for (String arg : args) {
			logger.trace("Arg {}: {}", ++i, arg);
		}

		if (args.length != 3) {
			logger.error("Bad arguments. Usage: filein.csv fileout.csv nbrthreads");
			System.exit(1);
		}

		final String filenameIn = args[0];
		assert "".equals(filenameIn) == false;

		final String filenameOut = args[1];
		assert "".equals(filenameOut) == false;

		int nbrThreads = -1;
		try {
			nbrThreads = Integer.parseInt(args[2]);
			if (nbrThreads < 0) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			logger.error("Bad syntax for NbrThreads argument");
			System.exit(1);
		}

		// start the business

		GraphFacilities facility = new MyGraphFacilities();
		try{
			MyNode root = (MyNode) facility.loadCSV(filenameIn);
			
			/////////////////////////////////////////
      	    //  your code should be placed here    //
		    //  you must process the graph : root  //
			/////////////////////////////////////////

			// 1) Make a new sharedqueue and add root inside
			SharedQueue sharedqueue = new SharedQueue(nbrThreads);
			sharedqueue.add(root);

			// 2) Make the threads
			List<Thread> threadList = new LinkedList<>();
			for (i = 1 ; i<=nbrThreads ; i++){
				threadList.add(new MyThread(String.valueOf(i), sharedqueue));
			}
			// 3) Start each thread
			for(Thread t : threadList){
				logger.info("Thread n°"+ t.getName() +" has number " + t.getId());
				t.start();
			}
			// 4) wait for each thread to finish
			for(Thread t : threadList){
				try{
					t.join();
				} catch (Exception e) {
					//
				}
			}

			/////////////////////////////////////////
			
			logger.info("Process is finished.");

			facility.saveCSV(filenameOut);

			logger.info("Result is saved in file: \"{}\".",filenameOut);
			
			
		} catch (ExceptionSyntaxError e) {
			logger.error("SyntaxError");
			System.exit(1);
		}

	}
}
