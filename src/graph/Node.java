package graph;

import java.util.List;

/// A Node in a graph.

public interface Node {
	/// @return the unmodifiable list of nodes downstream of this
	List<Node> getTargets();

	/// @return the unmodifiable list of nodes upstream of this
	List<Node> getOrigins();

	/// returns the information contained in this Node.
	Object getInfo();

	/// @return true iff info is equals to data and data is not null
	boolean looksFor(Object data);
}
