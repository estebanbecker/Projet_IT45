package Graph;

import java.awt.*;

public class Edge {
    
    public float weight;
    
    public String label;

    public Integer node_id_from;

    public Integer node_id_to;

    public Color color;

    public Edge(float w, String l, Integer node_id_from, Integer node_id_to, Color color) {
        this.weight = w;
        this.label = l;
        this.node_id_from = node_id_from;
        this.node_id_to = node_id_to;
        this.color = color;
    }

    public Integer getNodeToId() {
        return node_id_to;
    }

    public void setNodeToId(Integer node_id_to) {
        this.node_id_to = node_id_to;
    }

    public String getLabel() {
        return label;
    }

    public Color getColor() {
        return color;
    }
}

