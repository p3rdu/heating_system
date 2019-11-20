package distributedhello;

import java.util.*;

public class NodeInfo {
    HashMap<Integer, Node> nodes = new HashMap<Integer, Node>();

    public void registerNode(int id, String host, int port) {
        nodes.put(id, new Node(host, port));
    }

    public Node getNodeInfo(int id) {
        return this.nodes.get(id);
    }
    
    public String getNodeHostname(int id) {
        return this.nodes.get(id).hostname;
    }
    
    public int getNodePort(int id) {
        return this.nodes.get(id).port;
    }

    public LinkedList<Node> getRemainingNodes(int id) {
        LinkedList<Node> resultSet = new LinkedList<Node>();
        for (int curr_id : this.nodes.keySet()) {
            if (curr_id != id) resultSet.add(this.nodes.get(curr_id));
        }
        return resultSet;
    }

}
