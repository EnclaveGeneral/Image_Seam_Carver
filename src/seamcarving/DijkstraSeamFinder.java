package seamcarving;

import graphs.Edge;
import graphs.Graph;
import graphs.shortestpaths.DijkstraShortestPathFinder;
import graphs.shortestpaths.ShortestPath;
import graphs.shortestpaths.ShortestPathFinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DijkstraSeamFinder implements SeamFinder {
    private final ShortestPathFinder<Graph<Vertex, Edge<Vertex>>, Vertex, Edge<Vertex>> pathFinder;

    public DijkstraSeamFinder() {
        this.pathFinder = createPathFinder();
    }

    private class Vertex {
        private final int outerIndex;
        private final int innerIndex;
        // private double energy;

        public Vertex(int outerIndex, int innerIndex) {
            this.outerIndex = outerIndex;
            this.innerIndex = innerIndex;
            //this.energy = energy;
        }

        /*
        public boolean equals(Vertex v) {
            if (outerIndex == v.getOuterIndex() && innerIndex == v.getInnerIndex()) {
                return true;
            }
            return false;
        }
         */

        public int getOuterIndex() {
            return outerIndex;
        }

        public int getInnerIndex() {
            return innerIndex;
        }

    }

    private class VerticalGraph implements Graph<Vertex, Edge<Vertex>> {
        private double[][] newEnergies;
        private HorizontalGraph verticalGraph;

        public VerticalGraph(double[][] energies, Vertex start, Vertex end) {
            if (energies == null) {
                throw new IllegalArgumentException();
            }
            newEnergies = new double[energies[0].length][energies.length];
            for (int i = 0; i < newEnergies.length; ++i) {
                for (int j = 0; j < newEnergies[i].length; ++j) {
                    newEnergies[i][j] = energies[newEnergies[i].length - 1 - j][i];
                }
            }
            verticalGraph = new HorizontalGraph(newEnergies, start, end);
        }

        public Collection<Edge<Vertex>> outgoingEdgesFrom(Vertex vertex) {
            return verticalGraph.outgoingEdgesFrom(vertex);
        }
    }

    private class HorizontalGraph implements Graph<Vertex, Edge<Vertex>> {
        //private Vertex current;
        private Map<Vertex, Set<Edge<Vertex>>> ourMap;
        private Vertex[][] verticesInventory;

        public HorizontalGraph(double[][] energies, Vertex start, Vertex end) {
            // Create graph with energies given.
            if (energies == null) {
                throw new IllegalArgumentException();
            }
            ourMap = new HashMap<>();
            verticesInventory = new Vertex[energies.length][energies[0].length];
            //current = start;
            for (int i = 0; i < energies.length; ++i) {
                for (int j = 0; j < energies[i].length; ++j) {
                    Vertex newVertex = new Vertex(i, j);
                    verticesInventory[i][j] = newVertex;
                }
            }
            Set<Edge<Vertex>> starterSet = new HashSet<>();
            ourMap.put(start, starterSet);
            for (int i = 0; i < energies[0].length; ++i) {
                Vertex currentVertex = verticesInventory[0][i];
                Edge<Vertex> newEdge = new Edge<>(start, currentVertex, energies[0][i]);
                ourMap.get(start).add(newEdge);

            }
            for (int i = 0; i < energies.length - 1; ++i) {
                for (int j = 0; j < energies[i].length; ++j) {
                    // add current vertex to the graph
                    Vertex current = verticesInventory[i][j];
                    Set<Edge<Vertex>> newSet = new HashSet<>();
                    ourMap.put(current, newSet);
                    Vertex parallel = verticesInventory[i + 1][j];
                    Edge<Vertex> parallelEdge = new Edge<>(current, parallel, energies[i + 1][j]);
                    ourMap.get(current).add(parallelEdge);
                    if (j == 0) {
                        Vertex bottom = verticesInventory[i + 1][j + 1];
                        Edge<Vertex> bottomEdge = new Edge<>(current, bottom, energies[i + 1][j + 1]);
                        ourMap.get(current).add(bottomEdge);
                    } else if (j == energies[i].length - 1) {
                        Vertex top = verticesInventory[i + 1][j - 1];
                        Edge<Vertex> topEdge = new Edge<>(current, top, energies[i + 1][j - 1]);
                        ourMap.get(current).add(topEdge);
                    } else {
                        Vertex top = verticesInventory[i + 1][j - 1];
                        Edge<Vertex> topEdge = new Edge<>(current, top, energies[i + 1][j - 1]);
                        Vertex bottom = verticesInventory[i + 1][j + 1];
                        Edge<Vertex> bottomEdge = new Edge<>(current, bottom, energies[i + 1][j + 1]);
                        ourMap.get(current).add(topEdge);
                        ourMap.get(current).add(bottomEdge);
                    }

                }
            }
            for (int i = 0; i < energies[energies.length - 1].length; ++i) {
                Vertex current = verticesInventory[energies.length - 1][i];
                Set<Edge<Vertex>> newSet = new HashSet<>();
                ourMap.put(current, newSet);
                Edge<Vertex> newEdge = new Edge<>(current, end, 1);
                ourMap.get(current).add(newEdge);
            }

        }

        @Override
        public Collection<Edge<Vertex>> outgoingEdgesFrom(Vertex vertex) {
            if (!ourMap.containsKey(vertex)) {
                throw new IllegalArgumentException();
            }
            return ourMap.get(vertex);
        }
    }

    protected <G extends Graph<V, Edge<V>>, V> ShortestPathFinder<G, V, Edge<V>> createPathFinder() {
        /*
        We override this during grading to test your code using our correct implementation so that
        you don't lose extra points if your implementation is buggy.
        */
        return new DijkstraShortestPathFinder<>();
    }

    @Override
    public List<Integer> findHorizontalSeam(double[][] energies) {
        // Create a graph with each non-edge vertex having a down left, down center, and down right child
        // Run Dijkstra's Algorithm on the graph with the right start and end point.
        if (energies == null) {
            return null;
        }
        Vertex starting = new Vertex(-1, -1);
        Vertex ending = new Vertex(-2, -2);
        HorizontalGraph horizontalGraph = new HorizontalGraph(energies, starting, ending);
        ShortestPath<Vertex, Edge<Vertex>> result = pathFinder.findShortestPath(horizontalGraph, starting, ending);
        if (result.exists()) {
            List<Integer> answer = new ArrayList<>();
            for (Vertex current : result.vertices()) {
                // Don't include the start and end vertex
                if (current.getOuterIndex() >= 0) {
                    answer.add(current.getOuterIndex(), current.getInnerIndex());
                }
            }
            return answer;
        }
        return null;
    }

    @Override
    public List<Integer> findVerticalSeam(double[][] energies) {
        if (energies == null) {
            return null;
        }
        Vertex starting = new Vertex(-1, -1);
        Vertex ending = new Vertex(-2, -2);
        VerticalGraph verticalGraph = new VerticalGraph(energies, starting, ending);
        ShortestPath<Vertex, Edge<Vertex>> result = pathFinder.findShortestPath(verticalGraph, starting, ending);
        if (result.exists()) {
            List<Integer> answer = new ArrayList<>();
            for (Vertex current : result.vertices()) {
                if (current.getInnerIndex() >= 0) {
                    int previous = energies.length - 1;
                    answer.add(previous - current.getInnerIndex());
                }
            }
            return answer;
        }
        return null;
    }



}
