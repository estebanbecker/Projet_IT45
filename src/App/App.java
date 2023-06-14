package App;

import Graph.Graph;
import PathFinder.*;

import javax.swing.*;

import static UI.GraphEditor.createAndShowGUI;


public class App {

    public static void main(String[] args) {
        Graph graph = new Graph();
        UI.main();

        //use the getter for coordinates
        double [][] coordinates = UI.getCoordinates();

        //use graph.createANode(null, null); for each node in coordinates
        for(int i = 0; i < coordinates.length; i++) {
            graph.createANode((float)coordinates[i][0], (float)coordinates[i][1]);
        }

        //add an edeg for each node to the next node
        for(int i = 0; i < coordinates.length - 1; i++) {
            graph.connectBidirectionalNodes(i, i+1, i + " <->" + i+1);
        }

        SwingUtilities.invokeLater(() -> {
            createAndShowGUI(graph);
        });
    }

    public static void newProgram() {
        // Update the current graph with the newGraph
        //kill the current createAndShowGUI
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI(new Graph());
        });
    }
    public static void restartProgram(Graph newGraph) {
        // Update the current graph with the newGraph
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI(newGraph);
        });
    }
}