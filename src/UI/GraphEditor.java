package UI;

import Graph.Edge;
import Graph.Files;
import Graph.Graph;
import Graph.Node;
import PathFinder.Dijkstra;
import PathFinder.IntFloatList;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import App.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class GraphEditor {

    private static final int CONTEXT_MENU_COOLDOWN = 1000; // Cooldown period in milliseconds
    private static long lastContextMenuTime = 0; // Last time the context menu was invoked

    private static IntFloatList result;

    public static String osName = System.getProperty("os.name").toLowerCase();
    public static int osPadding = 0;

    private static void addNodeOnClick(JPanel panel, Graph graph, boolean selected) {
        // Change cursor to crosshair
        panel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {

                    super.mouseClicked(e);
                    int x = (int) ((e.getX() - ((GraphPanel) panel).getOffsetX()) / ((GraphPanel) panel).getScale());
                    int y = (int) ((e.getY() - ((GraphPanel) panel).getOffsetY()) / ((GraphPanel) panel).getScale());

                    if (selected) {
                        x = (x + 25) / 50 * 50;
                        y = (y + 25) / 50 * 50;
                    }
                    graph.createANode((float) x, (float) y);

                    // Repaint the panel
                    panel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    panel.removeMouseListener(this);
                    panel.repaint();
                }
            }
        });
    }

    public static class NodeSelectionHelper {
        private static CompletableFuture<Node> nodeSelectionFuture;

        public static CompletableFuture<Node> selectNode(JPanel panel) {
            nodeSelectionFuture = new CompletableFuture<>();

            // Change cursor to crosshair
            panel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        int x = (int) ((e.getX() - ((GraphPanel) panel).getOffsetX())
                                / ((GraphPanel) panel).getScale());
                        int y = (int) ((e.getY() - ((GraphPanel) panel).getOffsetY())
                                / ((GraphPanel) panel).getScale());

                        Node clickedNode = ((GraphPanel) panel).findClickedNode(x, y);

                        panel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        panel.removeMouseListener(this);
                        panel.repaint();

                        nodeSelectionFuture.complete(clickedNode);
                    }
                }
            });

            return nodeSelectionFuture;
        }
    }

    public static void createAndShowGUI(Graph graph) {
        // setup flatlaf
        try {
            FlatMacLightLaf.setup();
        } catch (Exception e) {
            System.err.println("Failed to initialize LaF");
        }
        JFrame frame = new JFrame("Graph Nodes");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        System.out.println(osName);

        if (osName.contains("mac") || osName.contains("Mac")) {
            frame.getRootPane().putClientProperty("apple.awt.windowTitleVisible", false);
            frame.getRootPane().putClientProperty("apple.awt.fullWindowContent", true);
            frame.getRootPane().putClientProperty("apple.awt.transparentTitleBar", true);
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            osPadding = 25;
        }

        GraphPanel panel = new GraphPanel(graph.getNodes());
        panel.setGraph(graph);
        panel.setLayout(null);
        frame.getContentPane().add(panel);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem newItem = new JMenuItem("New");

        newItem.addActionListener(e -> {
            // Handle the "New" action
            App.restartProgram(new Graph());
        });
        fileMenu.add(newItem);

        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(e -> {
            // Handle the "Open" action
            // load the system file chooser
            try {
                JFileChooser fileChooser = new JFileChooser();
                // native file picker
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                    Path path = Paths.get(selectedFile.getAbsolutePath());

                    Files open_file = new Files(path.toString());
                    Graph new_graph = open_file.readFile();

                    App.restartProgram(new_graph);
                    frame.dispose();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        fileMenu.add(openItem);

        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(e -> {
            // Handle the "Save" action
            try {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                    Path path = Paths.get(selectedFile.getAbsolutePath());

                    Files save_file = new Files(path.toString());
                    save_file.writeFile(panel.graph);

                    panel.repaint();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        fileMenu.add(saveItem);

        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);

        JCheckBox snap = new JCheckBox("Snap to grid");
        snap.setBounds(10, 15 + osPadding, 500, 20);
        snap.setSelected(true);
        panel.setSnap(snap.isSelected());
        snap.addActionListener(e -> {
            if (e.getSource() == snap) {
                panel.setSnap(snap.isSelected());
                panel.repaint();
            }
        });
        panel.add(snap);

        JButton fab = new JButton("+ add node");
        fab.setHorizontalTextPosition(SwingConstants.CENTER);
        fab.setFocusPainted(false);
        fab.addActionListener(e -> {
            if (e.getSource() == fab) {
                addNodeOnClick(panel, graph, snap.isSelected());
            }
        });
        // add a checkbox for snapping

        // Add a ComponentListener to reset FAB position on window resize
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int platform;
                int winpadding = 0;
                osName = System.getProperty("os.name");

                if (osName.toLowerCase().contains("mac")) {
                    platform = 25;
                } else {
                    platform = 50;
                    winpadding = 25;
                }
                int fabWidth = 140 + platform;
                int fabHeight = 50;
                int padding = 10;
                int frameWidth = frame.getWidth();
                int frameHeight = frame.getHeight();
                int fabX = frameWidth - fabWidth - padding - winpadding;
                int fabY = frameHeight - fabHeight - padding - platform + osPadding;

                fab.setBounds(fabX, fabY, fabWidth, fabHeight);
            }
        });
        fab.setBounds(10, 10, 100, 20);
        fab.setBackground(new Color(97, 37, 168));
        fab.setForeground(Color.WHITE);
        fab.setFocusPainted(false);
        fab.setFont(new Font("Arial", Font.BOLD, 20));
        // Set the initial position of the FAB
        int fabWidth = 140;
        int fabHeight = 50;
        int padding = 10;
        int frameWidth = frame.getWidth();
        int frameHeight = frame.getHeight();
        int fabX = frameWidth - fabWidth - padding;
        int fabY = frameHeight - fabHeight - padding;

        fab.setBounds(fabX, fabY, fabWidth, fabHeight);

        // add two buttons with an eyedropper icon that allow the user to select a node
        Node[] pathway_nodes = new Node[2];

        JButton selectNode = new JButton("From:");
        JButton selectNode2 = new JButton("To:");

        selectNode.setBounds(10, 50 + osPadding, 120, 30);
        selectNode2.setBounds(10, 80 + osPadding, 120, 30);

        selectNode.addActionListener(e -> NodeSelectionHelper.selectNode(panel).thenAccept(node -> {
            System.out.println("Selected node: " + node);
            pathway_nodes[0] = node;
            selectNode.setText(node.getId().toString());
            if (Objects.equals(selectNode2.getText(), "To:")) {
                selectNode2.doClick();
            }
        }));
        selectNode2.addActionListener(e -> NodeSelectionHelper.selectNode(panel).thenAccept(node -> {
            System.out.println("Selected node: " + node);
            pathway_nodes[1] = node;
            selectNode2.setText(node.getId().toString());
        }));

        panel.add(selectNode);
        panel.add(selectNode2);

        // add an eye dropper icon on the side of the button
        ImageIcon eyeDropperIcon = new ImageIcon("AP4B_project/asset/eyedropper.png"); // Replace "eye_dropper_icon.png"
                                                                                       // with
        // the actual path or resource name
        // scale the icon
        Image img = eyeDropperIcon.getImage();
        Image newimg = img.getScaledInstance(15, 15, java.awt.Image.SCALE_SMOOTH);
        eyeDropperIcon = new ImageIcon(newimg);

        // put the icon to the very right
        selectNode.setHorizontalTextPosition(SwingConstants.LEFT);
        selectNode2.setHorizontalTextPosition(SwingConstants.LEFT);
        // Set the eye dropper icon to the button
        selectNode.setIcon(eyeDropperIcon);
        selectNode2.setIcon(eyeDropperIcon);

        // Create "Go" button
        JButton goButton = new JButton("go");
        goButton.setBackground(new Color(54, 143, 39));
        goButton.setForeground(Color.WHITE);
        goButton.setBounds(10, 110 + osPadding, 70, 50);

        goButton.addActionListener(e -> {
            result = null;
            Dijkstra solver = new Dijkstra();
            try {
                result = solver.findShortestPath(graph, pathway_nodes[0].getId(), pathway_nodes[1].getId());
            } catch (Exception Error) {
                // Handle the exception and show an error message
                String errorMessage = "An error occurred: " + Error.getMessage();

                // Show the error message to the user
                JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(goButton);

        // if result =! null, create a text area and display the result

        panel.add(fab);

        frame.pack();
        frame.setVisible(true);
    }

    protected static class GraphPanel extends JPanel {

        private final HashMap<Integer, Node> nodes;
        // put the hovered nodes in a list
        private final ArrayList<Node> hoveredNodes = new ArrayList<>();
        private final ArrayList<Node> clickednodes = new ArrayList<>();

        private Graph graph;
        private int offsetX = 100; // X offset for dragging
        private int offsetY = 100; // Y offset for dragging
        private double scale = 1.0; // Zoom scale
        private int startX; // Start X position for dragging
        private int startY; // Start Y position for dragging
        private int mouseX;
        private int mouseY;
        private Node selectedNode;

        public GraphPanel(HashMap<Integer, Node> nodes) {
            this.nodes = nodes;
        }

        private void createContextMenu(int mouseX, int mouseY, int X, int Y) {
            // Check if the context menu was invoked recently
            if (System.currentTimeMillis() - lastContextMenuTime < CONTEXT_MENU_COOLDOWN) {
                return;
            }
            lastContextMenuTime = System.currentTimeMillis();
            Node clickedNode = findClickedNode(mouseX, mouseY);
            if (clickedNode == null) {
                return;
            }


            JPopupMenu contextMenu = new JPopupMenu();

            //add a x and y coordinate for the node
            JMenuItem xCoordinate = new JMenuItem("X: " + X);
            JMenuItem yCoordinate = new JMenuItem("Y: " + Y);
            contextMenu.add(xCoordinate);
            contextMenu.add(yCoordinate);

            //add separator
            contextMenu.addSeparator();

            JMenuItem deleteItem = new JMenuItem("Delete Node");
            contextMenu.add(deleteItem);

            // Add the edge editing submenu
            JMenu edgeMenu = new JMenu("Edit edges");

            // Retrieve edges from hashmap
            HashMap<Integer, Edge> edges = clickedNode.getEdges();

            // Iterates through the node's connected edges
            for (Edge edge : edges.values()) {
                // Add en entry for the edge
                JMenu edgeEditMenu = new JMenu(edge.label);

                // Add a submenu to delete the edge
                JMenuItem deleteEdge = new JMenuItem("Delete");
                deleteEdge.addActionListener(e -> {
                    graph.deleteEdge(edge.node_id_from, edge.node_id_to, true);
                    repaint();
                });

                // Add a submenu to rename the edge
                JMenuItem labelEdge = new JMenuItem("Rename");
                labelEdge.addActionListener(e -> {
                    String DialogMessage = "Enter edge name:";
                    String edgeName = JOptionPane.showInputDialog("Input new name for edge " + edge.label + " :",
                            DialogMessage);
                    if (edgeName != null && !edgeName.isEmpty() && !edgeName.equals(DialogMessage)) {
                        edge.label = edgeName;
                        graph.updateEdgeName(edge.node_id_from, edge.node_id_to, edgeName);
                        repaint();
                    } else {
                        private_show_error_message("Invalid input. Please enter a valid name.");
                    }
                });

                // Add a submenu change the weight of the edge
                // Displays current edge weight
                JMenuItem weightEdgename = new JMenuItem("weight: " + edge.weight);
                edgeEditMenu.add(weightEdgename);
                edgeEditMenu.addSeparator();
                JMenuItem weightEdge = new JMenuItem("Change weight");
                weightEdge.addActionListener(e -> {
                    String DialogMessage = edge.weight + ""     ;
                    String weight = JOptionPane.showInputDialog("Input new weight for edge " + edge.label + " :",
                            DialogMessage);
                    try {
                        int number = Integer.parseInt(weight);
                        graph.updateEdgeWeight(edge.node_id_from, edge.node_id_to, number);
                        repaint();
                    } catch (NumberFormatException exception) {
                        private_show_error_message("Invalid input. Please enter a valid integer.");
                    }
                });

                // if no node connected, don't add the rest
                edgeEditMenu.add(deleteEdge);
                edgeEditMenu.add(labelEdge);
                edgeEditMenu.add(weightEdge);
                edgeMenu.add(edgeEditMenu);
            }
            if (edgeMenu.getItemCount() != 0) {
                contextMenu.add(edgeMenu);
            }

            contextMenu.show(this, X, Y);

            deleteItem.addActionListener(e -> {
                if (clickedNode.getId() != null) {
                    if (result != null) {
                        for (Integer num : result.getIntList()) {
                            if (num != null && num.equals(clickedNode.getId())) {
                                result = null;
                                break;
                            }
                        }
                    }

                    graph.deleteNode(clickedNode.getId(), true);
                    repaint();
                }
            });
        }

        private void private_show_error_message(String error_message) {
            JOptionPane.showMessageDialog(null, error_message, "Error", JOptionPane.ERROR_MESSAGE);
        }

        public void setGraph(Graph graph) {
            this.graph = graph;
        }

        protected Node findClickedNode(int mouseX, int mouseY) {
            for (Node node : nodes.values()) {
                float x = node.getPosition()[0];
                float y = node.getPosition()[1];

                // Check if the mouse click is inside the node bounds
                if (Math.abs(mouseX - x) <= 10 && Math.abs(mouseY - y) <= 10) {
                    return node;
                }
            }
            return null;
        }

        public int getOffsetX() {
            return offsetX;
        }

        public int getOffsetY() {
            return offsetY;
        }

        public double getScale() {
            return scale;
        }

        private int dragged;

        public void setDragged(int dragged) {
            this.dragged = dragged;
        }

        // Create a Graph Editor method
        public void GraphEditor() {

            // create a add edge function that uses the graph.connectUnidirectionalNodes()
            // method as argument
            // create a hoveredNodes node list, and store it publically
            // Add mouse listener for hovering over nodes make them bigger and show their
            // name

            addMouseMotionListener(new MouseAdapter() {

                Node big;

                public void mouseMoved(MouseEvent e) {
                    // Check if a node is clicked and assign it to selectedNode
                    selectedNode = findClickedNode(mouseX, mouseY);
                    big = selectedNode;
                    setDragged(0);

                }

                public void mouseDragged(MouseEvent e) {
                    if (big != null) {
                        // setDragged to 1
                        setDragged(1);
                        // System.out.println("dragged");
                        // Calculate the mouse movement delta

                        if (SwingUtilities.isLeftMouseButton(e)) {

                            super.mouseClicked(e);
                            int x = (int) ((e.getX() - getOffsetX()) / getScale());
                            int y = (int) ((e.getY() - getOffsetY()) / getScale());

                            if (getSnap()) {
                                x = (x + 25) / 50 * 50;
                                y = (y + 25) / 50 * 50;
                            }
                            // Repaint the panel
                            removeMouseListener(this);
                            repaint();
                            graph.updatePosition(big.getId(), (float) x, (float) y);
                            clickednodes.clear();
                        }
                    }
                }

            });

            addMouseMotionListener(new MouseMotionAdapter() {

                public void mouseDragged(MouseEvent e) {
                    if (dragged != 1) {

                        int deltaX = e.getX() - startX;
                        int deltaY = e.getY() - startY;

                        offsetX += deltaX;
                        offsetY += deltaY;

                        startX = e.getX();
                        startY = e.getY();

                        repaint();
                    }
                }

                // add a mouse released

                public void mouseMoved(MouseEvent e) {

                    mouseX = (int) ((e.getX() - offsetX) / scale);
                    mouseY = (int) ((e.getY() - offsetY) / scale);

                    Node hoveredNode = findClickedNode(mouseX, mouseY);
                    // if hovered node, make it bigger and show name
                    // if not, remove the bigger node and name
                    if (hoveredNode != null) {
                        hoveredNodes.add(hoveredNode);
                        // System.out.println("hovered" + hoveredNode);
                        // Set the cursor to the hand cursor
                        if (getCursor().getType() != Cursor.CROSSHAIR_CURSOR) {
                            setCursor(new Cursor(Cursor.HAND_CURSOR));
                        }
                    } else {
                        // if cursor is cross hair, set it to default
                        if (getCursor().getType() != Cursor.CROSSHAIR_CURSOR) {
                            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        }
                        // Set the cursor to the move cursor
                    }
                    repaint();
                }
            });
            addMouseListener(new MouseAdapter() {

                public void mousePressed(MouseEvent e) {
                    startX = e.getX();
                    startY = e.getY();
                    mouseX = (int) ((startX - offsetX) / scale);
                    mouseY = (int) ((startY - offsetY) / scale);

                    if (SwingUtilities.isRightMouseButton(e)) {
                        createContextMenu(mouseX, mouseY, startX, startY);
                    }

                    if (SwingUtilities.isLeftMouseButton(e)) {
                        Node clickedNode = findClickedNode(mouseX, mouseY);

                        if (clickedNode != null) {
                            if (getCursor().getType() != Cursor.CROSSHAIR_CURSOR) {
                                if (clickednodes.size() == 0) {
                                    selectedNode = clickedNode;
                                    clickednodes.add(clickedNode);
                                } else if (clickednodes.size() == 1 && selectedNode != clickednodes.get(0)) {
                                    selectedNode = clickedNode;
                                    clickednodes.add(clickedNode);
                                } else if (clickednodes.size() == 1 && selectedNode == clickednodes.get(0)) {
                                    selectedNode = null;
                                    clickednodes.clear();
                                }

                                else if (clickednodes.size() == 2) {
                                    String DialogMessage = "Enter edge name:";
                                    String edgeName = DialogMessage;
                                    while (edgeName.equals(DialogMessage)) {
                                        edgeName = JOptionPane
                                                .showInputDialog(
                                                        "Creating edge from node "
                                                                + clickednodes.get(0).getId().toString()
                                                                + " to node " + clickednodes.get(1).getId().toString(),
                                                        DialogMessage);
                                        if (edgeName == null) {

                                            break;
                                        }
                                    }
                                    if (edgeName != null && !edgeName.isEmpty()) {
                                        System.out.println(edgeName);
                                        graph.connectUnidirectionalNodes(clickednodes.get(0).getId(),
                                                clickednodes.get(1).getId(), edgeName);
                                        repaint();
                                    }
                                    // clear the clicked nodes
                                    clickednodes.clear();
                                    // Reset the selected nodes
                                    selectedNode = null;
                                    // Repaint the graph
                                    repaint();
                                }
                            }
                        }
                    }
                }
            });

            // Add mouse wheel listener for zooming
            addMouseWheelListener(e -> {
                int notches = e.getWheelRotation();
                int mouseX = e.getX();
                int mouseY = e.getY();

                double scaleFactor = Math.pow(1.000023, notches);

                // Apply the quadratic curve to the scale factor
                scaleFactor = Math.pow(scaleFactor, 2.0);

                // scale *= scaleFactor;

                double newScale = scale * scaleFactor;

                // Clamp the scale within the specified range
                double MAX_SCALE = 3.0;
                double MIN_SCALE = 0.6;
                scale = Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale));

                // Limit the scale to a reasonable range
                if (scale < MIN_SCALE || newScale < MIN_SCALE) {
                    scale = MIN_SCALE;
                    newScale = MIN_SCALE;
                } else if (scale > MAX_SCALE || newScale > MAX_SCALE) {
                    scale = MAX_SCALE;
                    newScale = MAX_SCALE;
                }
                // Calculate the offset adjustment based on the mouse position
                int offsetXAdjustment = (int) (mouseX - mouseX * (newScale / scale));
                int offsetYAdjustment = (int) (mouseY - mouseY * (newScale / scale));

                // Update the scale and offset
                scale = newScale;
                offsetX += offsetXAdjustment;
                offsetY += offsetYAdjustment;

                repaint();
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            GraphEditor();
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Apply zoom and offset transformations
            g2d.translate(offsetX, offsetY);
            g2d.scale(scale, scale);

            Draw draw = new Draw(g2d);

            draw.grid();
            draw.origin();

            g2d.setStroke(new BasicStroke(2.0f));

            draw.all_arrows(nodes);
            draw.nodes(nodes);
            draw.node_circle(hoveredNodes);
            draw.node_circle(clickednodes);
            hoveredNodes.clear();

            if (result != null) {
                draw.path(result, nodes);
            }

            g2d.scale(1.0 / scale, 1.0 / scale);
            g2d.translate(-offsetX, -offsetY);
        }

        public Dimension getPreferredSize() {
            return new Dimension(800, 800);
        }

        private boolean SnapToGrid = false;

        public boolean getSnap() {
            return SnapToGrid;
        }

        public void setSnap(boolean selected) {
            SnapToGrid = selected;
            System.out.println("snap to grid: " + SnapToGrid);
        }
    }
}
