package graphs.shortestpaths;

import graphs.Edge;
import priorityqueues.ExtrinsicMinPQ;
import priorityqueues.NaiveMinPQ;
import graphs.BaseEdge;
import graphs.Graph;


import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Computes shortest paths using Dijkstra's algorithm.
 * @see SPTShortestPathFinder for more documentation.
 */
public class DijkstraShortestPathFinder<G extends Graph<V, E>, V, E extends BaseEdge<V, E>>
    extends SPTShortestPathFinder<G, V, E> {



    protected <T> ExtrinsicMinPQ<T> createMinPQ() {
        return new NaiveMinPQ<>();
        /*
        If you have confidence in your heap implementation, you can disable the line above
        and enable the one below.
         */
        // return new ArrayHeapMinPQ<>();

        /*
        Otherwise, do not change this method.
        We override this during grading to test your code using our correct implementation so that
        you don't lose extra points if your implementation is buggy.
         */
    }

    @Override
    protected Map<V, E> constructShortestPathsTree(G graph, V start, V end) {
        // Create a PQ with just the source node, and give it a distance of 0.
        // help wows is clapping me!!!!
        Map<V, E> shortestPathTree = new HashMap<>();
        Map<V, Double> shortestDistance = new HashMap<>();
        if (start == null) {
            E edge = (E) (new Edge<V>(null, null, 0));
            shortestPathTree.put(null, edge);
            System.out.println(shortestPathTree);
            return shortestPathTree;
        }
        ExtrinsicMinPQ<V> priorityQueue = createMinPQ();
        priorityQueue.add(start, 0.0);
        Set<V> known = new HashSet<>();
        known.add(start);
        shortestDistance.put(start, 0.0);
        while (!priorityQueue.isEmpty()) {
            V closestVertex = priorityQueue.removeMin();
            known.add(closestVertex);
            if (closestVertex.equals(end)) {
                return shortestPathTree;
            }
            Collection<E> outgoingEdges = graph.outgoingEdgesFrom(closestVertex);
            for (E currentEdge : outgoingEdges) {
                V currentVertex = currentEdge.to();
                if (!known.contains(currentVertex)) {
                    if (!shortestDistance.containsKey(currentVertex)) {
                        double newDistance = shortestDistance.get(closestVertex) + currentEdge.weight();
                        shortestDistance.put(currentVertex, newDistance);
                        shortestPathTree.put(currentVertex, currentEdge);
                        priorityQueue.add(currentVertex, newDistance);
                    } else {
                        double oldDistance = shortestDistance.get(currentVertex);
                        double newDistance = shortestDistance.get(closestVertex) + currentEdge.weight();
                        if (newDistance < oldDistance) {
                            shortestDistance.put(currentVertex, newDistance);
                            shortestPathTree.put(currentVertex, currentEdge);
                            priorityQueue.add(currentVertex, newDistance);
                        }
                    }
                }
            }
        }
        return shortestPathTree;
    }

    @Override
    protected ShortestPath<V, E> extractShortestPath(Map<V, E> spt, V start, V end) {
        // go backwards from the end to the start since node can't have 2 parents in tree
        LinkedList<E> edgesPath = new LinkedList<>();
        E currentEdge = null;
        if (start == null && end == null || Objects.equals(start, end)) {
            return new ShortestPath.SingleVertex<>(start);
        }
        if (!spt.containsKey(end)) {
            return new ShortestPath.Failure<>();
        }
        while (!end.equals(start) && spt.containsKey(end) && spt.get(end) != null) {
            currentEdge = spt.get(end);
            end = currentEdge.from();
            edgesPath.addFirst(currentEdge);
        } // if there's a hole in the SPT
        if (!end.equals(start)) {
            return new ShortestPath.Failure<>();
        }
        return new ShortestPath.Success<>(edgesPath);
    }


}
