package Graph;

/**
 * This class is used to store the data of the edges of a graph.
 * The data is stored in arrays of the same length.
 * The index of the arrays correspond to the same edge.
 * For example, the first element of the node_from_ids array is the id of the node from which the first edge starts.
 * The first element of the node_to_ids array is the id of the node to which the first edge ends.
 * The first element of the weights array is the weight of the first edge.
 * The first element of the labels array is the label of the first edge.
 */
public class EdgeData {
    public Integer[] node_from_ids;
    public Integer[] node_to_ids;
    public Float[] weights;
    public String[] labels;

    /**
     * Constructor of the class.
     * @param node_from_ids Array of the ids of the nodes from which the edges start.
     * @param node_to_ids Array of the ids of the nodes to which the edges end.
     * @param weights Array of the weights of the edges.
     * @param labels Array of the labels of the edges.
     */
    public EdgeData(Integer[] node_from_ids, Integer[] node_to_ids, Float[] weights, String[] labels) {
        this.node_from_ids = node_from_ids;
        this.node_to_ids = node_to_ids;
        this.weights = weights;
        this.labels = labels;
    }
}