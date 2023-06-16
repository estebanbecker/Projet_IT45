package GraphUI.Graph;

/**
 * This class is used to store the data of the nodes of a graph.
 * The data is stored in arrays of the same length.
 * The index of the arrays correspond to the same node.
 * For example, the first element of the ids array is the id of the first node.
 * The first element of the xValues array is the x value of the first node.
 * The first element of the yValues array is the y value of the first node.
 */
public class NodeData {
    public Integer[] ids;
    public Float[] xValues;
    public Float[] yValues;

    /**
     * Constructor of the class.
     * @param ids Array of the ids of the nodes.
     * @param xValues Array of the x values of the nodes.
     * @param yValues Array of the y values of the nodes.
     */
    public NodeData(Integer[] ids, Float[] xValues, Float[] yValues) {
        this.ids = ids;
        this.xValues = xValues;
        this.yValues = yValues;
    }

    public Integer getlength(){
        return ids.length;
    }
}