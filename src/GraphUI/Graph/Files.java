package GraphUI.Graph;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class is used to manage files, including reading and writing.
 * The processed format is .txt, so that information can be read in plain text.
 * The data is stored in arrays of size of elements funded ( that we will write or read).
 */

public class Files {
    private String filePath;
    private String flag1 = "***";
    private String flag2 = "*";
    private ArrayList<Integer> id = new ArrayList<>();
    private ArrayList<Float> x = new ArrayList<>();
    private ArrayList<Float> y = new ArrayList<>();
    private ArrayList<Integer> id_node_from = new ArrayList<>();
    private ArrayList<Integer> id_node_to = new ArrayList<>();
    private ArrayList<String> label = new ArrayList<>();

    private ArrayList<Color> color = new ArrayList<>();



    public Files(String filePath) {
        this.filePath = filePath;
    }



    /** Get information from a file
     * The file has a specific formatting detailed in the report
     * @return a graph
     **/
    public Graph readFile() {

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));   //Create a reader with the library BufferedReader
            String line;

            line = reader.readLine();
            if (line.equals(flag1)) {

                line = reader.readLine();

                while (!line.equals(flag1)) {                                            //The first bloc of information are the nodes
                    if (!line.equals(flag2)) {
                        id.add(Integer.parseInt(line));
                        line = reader.readLine();
                    }
                    if (!line.equals(flag2)) {
                        x.add(Float.parseFloat(line));
                        line = reader.readLine();
                    }

                    if (!line.equals(flag2)) {
                        y.add(Float.parseFloat(line));
                        line = reader.readLine();

                    } else {
                        line = reader.readLine();
                    }

                } 

                line = reader.readLine();
                while (!line.equals(flag1)) {                                           //The second bloc has the information of the edges
                    if (!line.equals(flag2) && !line.equals(flag1)) {
                        id_node_from.add(Integer.parseInt(line));
                        line = reader.readLine();

                        if (!line.equals(flag2)) {
                            id_node_to.add(Integer.parseInt(line));
                            line = reader.readLine();
                        }

                        if (!line.equals(flag2)) {
                            label.add(line);
                            line = reader.readLine();
                        }
                    } else {
                        line = reader.readLine();
                    }
                }
            }
            reader.close();

        } catch (IOException e){
            e.printStackTrace();
        }

        Integer[] id_array = id.toArray(new Integer[id.size()]);        //Converting from ArrayList to simple arrays (of the proper type)
        Float[] x_array = x.toArray(new Float[x.size()]);
        Float[] y_array = y.toArray(new Float[y.size()]);

        Integer[] id_node_from_array = id_node_from.toArray(new Integer[id_node_from.size()]);
        Integer[] id_node_to_array = id_node_to.toArray(new Integer[id_node_to.size()]);
        String[] label_array = label.toArray(new String[label.size()]);
        Color[] color_array = (Color[]) color.toArray()[color.size()];

        Graph read_graph = new Graph(id_array,x_array,y_array);                     //Creating a new graph with the values read
        read_graph.createEdges(id_node_from_array,id_node_to_array,label_array, color_array);

        return read_graph;
    }

    /** Write a file with the current graph
     * @param graph     the graph that will be stored in the file
     */
    public void writeFile(Graph graph) {
        NodeData node = graph.getNodesData();           //Retrieve information of the current graph
        EdgeData edge = graph.getEdgesData();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));    //Create a writer with the library BufferedWriter

            writer.write(flag1);            //Adding the first flag
            writer.newLine();

            for (int i = 0; i < node.getlength(); i++) {                //Writing information about the nodes
                writer.write(String.valueOf(node.ids[i]));
                writer.newLine();
                writer.write(String.valueOf(node.xValues[i]));
                writer.newLine();
                writer.write(String.valueOf(node.yValues[i]));
                writer.newLine();
                writer.write(flag2);                                    //Adding the other flag when it's done
                writer.newLine();
            }

            writer.write(flag1);            //Separating blocs
            writer.newLine();

            for (int i = 0; i < edge.node_from_ids.length; i++) {       //Writing information about the edges
                writer.write(String.valueOf(edge.node_from_ids[i]));
                writer.newLine();
                writer.write(String.valueOf(edge.node_to_ids[i]));
                writer.newLine();
                writer.write(edge.labels[i]);
                writer.newLine();
                writer.write(flag2);                                    //Adding the other flag when it's done
                writer.newLine();
            }

            writer.write(flag1);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
};
