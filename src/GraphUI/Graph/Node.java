package GraphUI.Graph;

import java.awt.*;
import java.util.HashMap;

public class Node {
    
    private static int lastId = 0;

    Integer id;
    Float[] position = new Float[2]; // [x, y]

    HashMap<Integer, Edge> edges;


    public Node(Float x, Float y) {
        this.id = lastId++;
        this.position[0] = x;
        this.position[1] = y;
        edges = new HashMap<Integer, Edge>();

    }

    public Node(Integer id, Float x, Float y) {
        this.id = id;
        this.position[0] = x;
        this.position[1] = y;
        edges = new HashMap<Integer, Edge>();

    }

    public Integer getId() {
        return id;
    }

    public void addEdge(Integer node_to_id, String label, Float weight, Color color) {
        Edge edge = new Edge(weight, label, this.id, node_to_id, color);
        edges.put(node_to_id, edge);
    }

    public Float[] getPosition() {
        return position;
    }

    public void setPosition(Float[] position) {
        this.position = position;
    }

    public HashMap<Integer, Edge> getEdges() {
        return edges;
    }

    public void setEdges(HashMap<Integer, Edge> edges) {
        this.edges = edges;
    }

    public static void setLastId(Integer lastId) {
        Node.lastId = lastId;
    }

    public void deleteEdge(Integer node_to_id) {
        edges.remove(node_to_id);
    }
}

