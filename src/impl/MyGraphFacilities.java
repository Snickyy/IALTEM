package impl;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import graph.ExceptionAlreadyConnected;
import graph.ExceptionNotConnected;
import graph.ExceptionNotContained;
import graph.GraphFacilities;
import graph.Node;

public class MyGraphFacilities extends GraphFacilities {
	static private final Logger logger=LogManager.getLogger();
	
	private final Set<Node> nodes=new LinkedHashSet<>();  
	
	@Override
	public Node createNode(Object info) {
		assert info!=null;

		logger.trace("create node: {}",info);

		MyNode myNode = new MyNode(info);
		nodes.add(myNode);
		
		return myNode;
	}

	@Override
	public void removeNode(Node node) throws ExceptionNotContained {
		assert node!=null;

		logger.trace("remove node: {}",node);

		
		((MyNode)node).delete();
	}

	@Override
	public void setVertex(Node from, Node to) throws ExceptionAlreadyConnected {
		assert from!=null;
		assert to!=null;

		logger.trace("create vertex: {} -- {}",from,to);

		
		if (from.getTargets().contains(to)) {
			throw new ExceptionAlreadyConnected();
		} else {
			((MyNode)from).addTarget(to);
			((MyNode)to).addOrigin(from);
		}

	}

	@Override
	public void removeVertex(Node from, Node to) throws ExceptionNotConnected {
		assert from!=null;
		assert to!=null;

		logger.trace("remove vertex: {} -- {}",from,to);

		if (from.getTargets().contains(to)) {
			throw new ExceptionNotConnected();
		} else {
			((MyNode)from).removeTarget(to);
			((MyNode)to).removeOrigin(from);
		}
	}

	@Override
	protected Set<Node> getNodes() {
		return Collections.unmodifiableSet(nodes);
	}

}
