package graph;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class GraphFacilities {	

	static private final Logger logger = LogManager.getLogger(GraphFacilities.class);

	/// @return the set of nodes managed by this
	abstract protected Set<Node> getNodes();
	
	/// Create a node from the graph
	abstract public Node createNode(Object info);

	/// Remove a node from the graph
	abstract public void removeNode(Node node) throws ExceptionNotContained;

	/***
	 *  Create a vertex between 'from' and 'to'
	 * @param from not null
	 * @param to not null
	 * @throws ExceptionAlreadyConnected is 'from' is already connected to 'to'
	 */
	abstract public void setVertex(Node from, Node to) throws ExceptionAlreadyConnected;

	
	/**
	 * Remove THE vertex between 'from' and 'to"
	 * @param from not null
	 * @param to not null
	 * @throws ExceptionNotConnected if 'from' is not connected to 'to'
	 */
	abstract public void removeVertex(Node from, Node to) throws ExceptionNotConnected;

	
	/**
	 * 
	 * @param filename the file name, not null.
	 * @return the first node encountered in the file.
	 * @throws ExceptionSyntaxError if any problem occurs during the loading of the file (file or syntax).
	 */
	public Node loadCSV(String filename) throws ExceptionSyntaxError {
		assert filename != null && !"".equals(filename);

		final Map<String, Node> cache = new HashMap<>();
		Node result = null;

		final Path pathToFile = Paths.get(filename);

		logger.info("Loading from {} : {}", filename, pathToFile.toString());

		long nline = 0;
		try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.US_ASCII)) {
			String line = br.readLine();
			nline++;
			while (line != null) {
				String[] attributes = line.split(",");
				assert attributes != null;

				int ncols = attributes.length;
				assert ncols == 1 || ncols == 2;

				String snode1 = attributes[0];

				Node node1 = cache.get(attributes[0]);
				if (node1 == null) {
					node1 = createNode(snode1);
					cache.put(snode1, node1);
				}
				result = (result == null) ? node1 : result;

				if (ncols == 2) {
					String snode2 = attributes[1];
					Node node2 = cache.get(snode2);
					if (node2 == null) {
						node2 = createNode(snode2);
						cache.put(snode2, node2);
					}
					try {
						setVertex(node1, node2);
					} catch (ExceptionAlreadyConnected e) {
						logger.warn("Vertew already defined between {} and {} at line {}", node1, node2, nline);
					}
				}
				line = br.readLine();
				nline++;
			}

		} catch (IOException ex) {
			ex.printStackTrace(System.err);
			throw new ExceptionSyntaxError();
		}
		assert result != null;
		return result;
	}

	/***
	 * 
	 * @param filename the file name where the graph will be saved.
	 * @throws ExceptionSyntaxError if any error occurs during the save.
	 */
	public void saveCSV(String filename) throws ExceptionSyntaxError {
		assert filename != null && !"".equals(filename);

		Path pathToFile = Paths.get(filename);

		logger.info("saving to {} : {}", filename, pathToFile.toString());

		try (BufferedWriter br = Files.newBufferedWriter(pathToFile, StandardCharsets.US_ASCII)) {
			for (Node node: getNodes()) {
				br.write(node.getInfo().toString());
				br.newLine();
				for (Node targer: node.getTargets()) {
					br.write(String.format("%s,%s",node.getInfo().toString(),targer.getInfo().toString()));
					br.newLine();
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace(System.err);
			throw new ExceptionSyntaxError();
		}
	}

}
