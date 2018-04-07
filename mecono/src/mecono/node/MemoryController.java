package mecono.node;

import java.util.ArrayList;

/**
 *
 * @author sabreok
 */
public class MemoryController {

	public MemoryController(SelfNode owner) {
		this.owner = owner;
	}

	public RemoteNode loadRemoteNode(String address) {
		// Check if node is loaded into memory
		for (RemoteNode node : nodes_memory) {
			if (node.getAddress().equals(address)) {
				return node;
			}
		}

		// TODO: Check if node is in saved in the database
		// Return a new blank node with address if none found anywhere else
		RemoteNode new_node = new RemoteNode(address, owner);
		nodes_memory.add(new_node);

		return new_node;
	}

	public PathStats loadPath(Path target) {
		// Check if node is loaded into memory
		for (PathStats path : paths_memory) {
			if (path.getPath().equals(target)) {
				return path;
			}
		}

		// TODO: Check if path is in saved in the database
		// Return a new blank path
		PathStats new_path = new PathStats(target, owner);
		paths_memory.add(new_path);

		return new_path;
	}

	private SelfNode owner;
	private ArrayList<RemoteNode> nodes_memory = new ArrayList<>();
	private ArrayList<PathStats> paths_memory = new ArrayList<>();
	private final int max_loaded_nodes = 1000; // Only keep the X most important nodes.
}
