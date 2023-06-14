package Graph;

import java.util.HashMap;
import java.util.ArrayList;

public class Graph {
    HashMap<Integer, Node> nodes;

    /**
     * Initializes a graph
     */
    public Graph() {
        nodes = new HashMap<Integer, Node>();
    }

    /**
     * Initializes a graph with a list of nodes
     * @param nodes    A Hashmap of nodes
     */
    public Graph(HashMap<Integer, Node> nodes) {
        this.nodes = new HashMap<Integer, Node>(nodes);
    }

    /**
     * Initializes a graph with a list of nodes
     * @param id   A list of ids
     * @param x     A list of x coordinates
     * @param y    A list of y coordinates
     */
    public Graph(Integer[] id, Float[] x, Float[] y) {
        nodes = new HashMap<Integer, Node>();
        Integer maximum_id = 0;
        for (int i = 0; i < id.length; i++) {
            Node node = new Node(id[i], x[i], y[i]);
            addNode(node);

            if (id[i] > maximum_id) {
                maximum_id = id[i];
            }
        }
        Node.setLastId(maximum_id + 1);
    }

    private void addNode(Node node) {
        nodes.put(node.getId(), node);
    }

    /**
     * Connects two nodes with an edge in both directions
     * @param node_from_id  The id of the node from which the edge starts
     * @param node_to_id         The id of the node to which the edge ends
     * @param label         The label of the edge
     */
    public void connectUnidirectionalNodes(Integer node_from_id, Integer node_to_id, String label) {
        Node node_from = nodes.get(node_from_id);
        Node node_to = nodes.get(node_to_id);

        Float weight = (float) Math.sqrt(Math.pow(node_from.position[0] - node_to.position[0], 2)
                + Math.pow(node_from.position[1] - node_to.position[1], 2));

        node_from.addEdge(node_to_id, label, weight);
    }

    /**
     * Connects two nodes with an edge in both directions
     * @param node1     The id of one node that will be connected
     * @param node2     The id of the other node that will be connected
     * @param label     The label of the edge
     */
    public void connectBidirectionalNodes(Integer node1, Integer node2, String label) {
        Node node_from = nodes.get(node1);
        Node node_to = nodes.get(node2);

        Float weight = (float) Math.sqrt(Math.pow(node_from.position[0] - node_to.position[0], 2)
                + Math.pow(node_from.position[1] - node_to.position[1], 2));

        node_from.addEdge(node2, label, weight);
        node_to.addEdge(node1, label, weight);
    }

    /**
     * Creates edges from a list of node ids and labels
     * @param node_from_id      The ids of the nodes from which the edges start
     * @param node_to_id    The ids of the nodes to which the edges end
     * @param label        The labels of the edges
     */
    public void createEdges(Integer[] node_from_id, Integer[] node_to_id, String[] label) {
        for (int i = 0; i < node_from_id.length; i++) {
            connectUnidirectionalNodes(node_from_id[i], node_to_id[i], label[i]);
        }
    }

    /**
     * Creates nodes from a list of positions
     * @param positions     A list of positions in the form of [x, y]
     */
    public void createNodes(Float[][] positions) {
        for (Float[] position : positions) {
            createANode( position[0], position[1]);
        }
    }
    
    /**
     * Creates a node
     * @param x     The x coordinate of the node
     * @param y     The y coordinate of the node
     */
    public void createANode(Float x, Float y) {
        Node node = new Node(x, y);
        addNode(node);
    }
    /**
     * Prints the graph
     */
    public void print() {
        for (Node node : nodes.values()) {
            System.out.println("Node " + node.getId() + " at position (" + node.position[0] + ", " + node.position[1]
                    + ") has edges:");
            for (Edge edge : node.edges.values()) {
                System.out.println("    Connected with Node: " + edge.node_id_to + " with label " + edge.label
                        + " and weight " + edge.weight);
            }
        }
    }

    /**
     * Prints a node
     * @param node_id       The id of the node
     */
    public void printNode(Integer node_id) {
        Node node = nodes.get(node_id);
        System.out.println("Node " + node.getId() + " at position (" + node.position[0] + ", " + node.position[1]
                + ") has edges:");
        for (Edge edge : node.edges.values()) {
            System.out.println("    " + edge.label + " with weight " + edge.weight);
        }
    }

    public void printEdge(Integer node_from_id, Integer node_to_id) {
        Node node_from = nodes.get(node_from_id);
        Edge edge = node_from.edges.get(node_to_id);
        System.out.println("Edge from " + node_from_id + " to " + node_to_id + " with label " + edge.label
                + " and weight " + edge.weight);
    }

    /**
     * Returns the neighbors of a node
     * @param node_id       The id of the node
     * @return              An array of the ids of the neighbors
     */
    public Integer[] getNeighbors(Integer node_id) {
        Node node = nodes.get(node_id);
        return node.edges.keySet().toArray(new Integer[0]);
    }

    /**
     * Returns the weight of an edge
     * @param node_from_id  The id of the node from which the edge starts
     * @param node_to_id    The id of the node to which the edge ends
     * @return              The weight of the edge
     */
    public String getEdgeLabel(Integer node_from_id, Integer node_to_id) {
        Node node_from = nodes.get(node_from_id);
        Edge edge = node_from.edges.get(node_to_id);
        return edge.label;
    }

    /**
     * Returns the weight of an edge
     * @param node_from_id  The id of the node from which the edge starts
     * @param node_to_id    The id of the node to which the edge ends
     * @return              The weight of the edge
     */
    public Float getEdgeWeight(Integer node_from_id, Integer node_to_id) {
        Node node_from = nodes.get(node_from_id);
        Edge edge = node_from.edges.get(node_to_id);
        return edge.weight;
    }

    /**
     * methods that returns the nodes hashmap
     */
    public HashMap<Integer, Node> getNodes(){
        return nodes;
    }

    /**
     * Update the position of a node
     * @param node_id  The id of the node
     * @param x       The new x coordinate
     * @param y     The new y coordinate
     */
    public void updatePosition(Integer node_id, Float x, Float y){
        Node node = nodes.get(node_id);
        Float [] position = {x,y};
        node.setPosition(position);
        //Update the distance for each edge

        for (Edge edge : node.edges.values()) {
            Node node_to = nodes.get(edge.node_id_to);
            Float weight = (float) Math.sqrt(Math.pow(node.position[0] - node_to.position[0], 2)
                    + Math.pow(node.position[1] - node_to.position[1], 2));
            edge.weight = weight;
        }
    }

    /**
     * Delete a node
     * @param node_id The id of the node
     * @param force  If true, delete the node even if it has edges, if false, throw an exception if the node has edges
     */
    public void deleteNode(Integer node_id, boolean force){
       
        Node node = nodes.get(node_id);
        if (node.edges.size() == 0 || force){
            nodes.remove(node_id);
        }else if(!force){
            throw new IllegalArgumentException("The node has edges, use force = true to delete it");
        }

        for (Node node_to : nodes.values()) {
            node_to.deleteEdge(node_id);
        }
    }

    /**
     * Delete an edge, defaults to unidirectional
     * @param node_id_from  The id of the node from which the edge starts       
     * @param node_id_to    The id of the node to which the edge ends
     */
    public void deleteEdge(Integer node_id_from, Integer node_id_to){
        deleteEdge(node_id_from, node_id_to, false);
    }


    /**
     * Delete an edge
     * @param node_id_from  The id of the node from which the edge starts       
     * @param node_id_to    The id of the node to which the edge ends
     * @param bidirectional Will remove edges both way if true
     */
    public void deleteEdge(Integer node_id_from, Integer node_id_to, Boolean bidirectional){
        Node node_from = nodes.get(node_id_from);
        node_from.deleteEdge(node_id_to);
        if(bidirectional){
            Node node_to = nodes.get(node_id_to);
            node_to.deleteEdge(node_id_to);
        }
    }

    /**
     * Checks of an edge is bidirectional
     * @param node_id_from  The id of the node from which the edge starts       
     * @param node_id_to    The id of the node to which the edge ends
     * @return True if bidirectional, False is there is no edge or unidirectional 
     */
    public Boolean isEdgeBidirectional(Integer node_id_from, Integer node_id_to){
        Node node_to = nodes.get(node_id_to);
        HashMap<Integer, Edge> node_to_edges = node_to.getEdges();
        Node node_from = nodes.get(node_id_from);
        HashMap<Integer, Edge> node_from_edges = node_from.getEdges();
        
        if(node_from_edges.containsKey(node_id_to) && node_to_edges.containsKey(node_id_from)){
            return true;
        } else {
            return false;
        }
    }

    /**
     * Update the label of an edge
     * @param node_id_from  The id of the node from which the edge starts       
     * @param node_id_to    The id of the node to which the edge ends
     * @param label        The new label
     */
    public void updateEdgeName(Integer node_id_from, Integer node_id_to, String label){
        Node node_from = nodes.get(node_id_from);
        Edge edge_from = node_from.edges.get(node_id_to);
        edge_from.label = label;
    }

    /**
     * Update the label of an edge
     * @param node_id_from  The id of the node from which the edge starts       
     * @param node_id_to    The id of the node to which the edge ends
     * @param weight        The new label
     */
    public void updateEdgeWeight(Integer node_id_from, Integer node_id_to, Integer weight){
        Node node_from = nodes.get(node_id_from);
        Edge edge_from = node_from.edges.get(node_id_to);
        edge_from.weight = weight;
    }

    /**
     * Return the the list of nodes ids
     * @return list of nodes ids
     */
    public Integer[] getAllNodesId (){
        return nodes.keySet().toArray(new Integer[0]);
    }

    /**
     * Return all the data of the nodes
     * @return NodeData object
     */
    public NodeData getNodesData() {
        Integer[] ids = nodes.keySet().toArray(new Integer[0]);
        Float[] xValues = new Float[ids.length];
        Float[] yValues = new Float[ids.length];
        for (int i = 0; i < ids.length; i++) {
            Node node = nodes.get(ids[i]);
            xValues[i] = node.getPosition()[0];
            yValues[i] = node.getPosition()[1];
        }
        return new NodeData(ids, xValues, yValues);
    }

    /**
     * Return all the data of the edges
     * @return EdgeData object
     */
    public EdgeData getEdgesData() {
        ArrayList<Integer> nodeFrom = new ArrayList<>();
        ArrayList<Integer> nodeTo = new ArrayList<>();
        ArrayList<Float> weights = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        for (Node node : nodes.values()) {
            for (Edge edge : node.edges.values()) {
                nodeFrom.add(node.id);
                nodeTo.add(edge.node_id_to);
                weights.add(edge.weight);
                labels.add(edge.label);
            }
        }
        return new EdgeData(nodeFrom.toArray(new Integer[0]), nodeTo.toArray(new Integer[0]), weights.toArray(new Float[0]), labels.toArray(new String[0]));
    }
}

