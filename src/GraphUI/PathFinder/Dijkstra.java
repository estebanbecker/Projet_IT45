package GraphUI.PathFinder;

import GraphUI.Graph.Graph;

import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Dijkstra {
    
    /**
     * This method finds the shortest path between two nodes in a graph.
     * @param graph         The graph in which the path will be found
     * @param node_from_id  The id of the node from which the path starts
     * @param node_to_id    The id of the node to which the path ends
     * @return              A list of node ids that represent the shortest path and the distance of the path in a IntFloatList
     */
    public IntFloatList findShortestPath(Graph graph, Integer node_from_id, Integer node_to_id) {
    PriorityQueue<NodeDistance> queue = new PriorityQueue<>();
    HashMap<Integer, Integer> previous = new HashMap<>();
    HashMap<Integer, Boolean> visited = new HashMap<>();

    queue.add(new NodeDistance(node_from_id, 0f));
    previous.put(node_from_id, -1);

    while (!queue.isEmpty()) {
        NodeDistance node = queue.poll();
        visited.put(node.getNodeId(), true);

        if (node.getNodeId() == node_to_id) {
            // Found the shortest path to the destination node
            return new IntFloatList(getPath(previous, node_to_id), node.getDistance());
        }

        Integer[] neighbors = graph.getNeighbors(node.getNodeId());

        for (Integer neighborId : neighbors) {
            if (!visited.containsKey(neighborId)) {
                float distance = node.distance + graph.getEdgeWeight(node.getNodeId(), neighborId);
                if (!previous.containsKey(neighborId) || distance < previous.get(neighborId)) {
                    previous.put(neighborId, node.getNodeId());
                    queue.add(new NodeDistance(neighborId, distance));
                }
            }
        }
    }

    // No path found to the destination node
    throw new RuntimeException("No path found");
}

private Integer[] getPath(HashMap<Integer, Integer> previous, Integer node_to_id) {
    List<Integer> path = new ArrayList<>();
    path.add(node_to_id);
    while (previous.get(node_to_id) != -1) {
        node_to_id = previous.get(node_to_id).intValue();
        path.add(0, node_to_id);
    }
    return path.toArray(new Integer[0]);
}

private class NodeDistance implements Comparable<NodeDistance> {
    private final Integer nodeId;
    private final Float distance;

    public NodeDistance(Integer nodeId, Float distance) {
        this.nodeId = nodeId;
        this.distance = distance;
    }

    public Integer getNodeId() {
        return nodeId;
    }

    public Float getDistance() {
        return distance;
    }

    @Override
    public int compareTo(NodeDistance other) {
        return distance.compareTo(other.getDistance());
    }
}
}
