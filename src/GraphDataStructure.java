import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;

//This class describes a directed graph using adjacent nodes
public class GraphDataStructure {
    private Map<String, LinkedHashSet<String>> map = new HashMap();

    //function to add edges between 2 vertices
    public void addEdge(String node1, String node2) {
        LinkedHashSet<String> adjacent = map.get(node1);
        if (adjacent == null) {
            adjacent = new LinkedHashSet();
            map.put(node1, adjacent);
        }
        adjacent.add(node2);
    }

    //Return the nodes which have an incident edge in common with node
    public LinkedList<String> adjacentNodes(String last) {
        LinkedHashSet<String> adjacent = map.get(last);
        if (adjacent == null) {
            return new LinkedList();
        }
        return new LinkedList<String>(adjacent);
    }
}