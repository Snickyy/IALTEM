package impl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import graph.Node;

public class MyNode implements Node {
	static private final Logger logger = LogManager.getLogger(MyNode.class);

	LinkedList<MyNode> targets = new LinkedList<>();
	LinkedList<MyNode> origins = new LinkedList<>();
	private int info;


	/*
		Manage the magic number
	*/

	private volatile int magicNumber = 0;
	private static int UpperMagicNumber = 1;

	
	//---------------------------------------------------------------------------
	
	/**
	* Return a fresh magic number, all magic numbers in the nodes are then < to this result.
	*/
	static synchronized int refreshMagic() {
		return ++UpperMagicNumber;
	}

	/**
	* mark this node as visited 
	*/
	synchronized void markVisited(int MN) {
		magicNumber = MN;
	}

	/**
	* @return true iff this node has been visited
	*/
	boolean isVisited(int MN) {
		return magicNumber == MN;
	}

	//--------------------------------------------------------------------------

	public MyNode(Object info) {
		assert info != null;

		this.info = Integer.parseInt((String) info);
	}

	@Override
	public List<Node> getTargets() {
		return Collections.unmodifiableList(targets);
	}

	@Override
	public List<Node> getOrigins() {
		return Collections.unmodifiableList(origins);
	}

	@Override
	public Object getInfo() {
		return info;
	}

	@Override
	public boolean looksFor(Object data) {
		return data != null && data.equals(info);
	}

	public void delete() {
		targets.clear();
		origins.clear();
	}

	public void addTarget(Node to) {
		assert to != null;
		assert targets.contains(to) == false;
		targets.add((MyNode) to);
	}

	/**
	 * Increase the info of MyNode
	 */
	public void update(){
		logger.trace("update {} by {}", info, Thread.currentThread().getId());
		//Wait 1 sec
		try {
			Thread.sleep(1000);

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		info++;

	}

	public void addOrigin(Node from) {
		assert from != null;
		assert origins.contains(from) == false;
		origins.add((MyNode) from);

	}

	public void removeTarget(Node to) {
		assert to != null;
		assert targets.contains(to);
		targets.remove((MyNode) to);
	}

	public void removeOrigin(Node from) {
		assert from != null;
		assert origins.contains(from);
		origins.remove((MyNode) from);
	}


	@Override
	public String toString() {
		return String.format("Node %d", info);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + info;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MyNode other = (MyNode) obj;
		if (info != other.info) {
			return false;
		}
		return true;
	}



}
