//Hosts that are not routers may participate as well.
//
//   - Keep a table with an entry for every possible destination in the
//     system.  The entry contains the distance D to the destination, and
//     the first router G on the route to that network.  Conceptually,
//     there should be an entry for the entity itself, with metric 0, but
//     this is not actually included.
//
//   - Periodically, send a routing update to every neighbor.  The update
//     is a set of messages that contain all of the information from the
//     routing table.  It contains an entry for each destination, with the
//     distance shown to that destination.
//
//   - When a routing update arrives from a neighbor G', add the cost
//     associated with the network that is shared with G'.  (This should
//     be the network over which the update arrived.)  Call the resulting distance D'.
//     Compare the resulting distances with the current
//     routing table entries.  If the new distance D' for N is smaller
//     than the existing value D, adopt the new route.  That is, change
//     the table entry for N to have metric D' and router G'.  If G' is
//     the router from which the existing route came, i.e., G' = G, the
//
// Note one other implication of this strategy: because
//   we don't need to use the 0 entries for anything, hosts that do not
//   function as routers don't need to send any update messages.  Clearly
//   hosts that don't function as routers (i.e., hosts that are connected
//   to only one network) can have no useful information to contribute
//   other than their own entry D(i,i) = 0.  As they have only the one
//   interface, it is easy to see that a route to any other network
//   through them will simply go in that interface and then come right
//   back out it.  Thus the cost of such a route will be greater than the
//   best cost by at least C.  Since we don't need the 0 entries, non-
//   routers need not participate in the routing protocol at all


/**
 * Class for hosts/computers connected to router.
 *
 * From graph perspective: nodes connected to vertices.
 */

import java.util.ArrayList;
import java.util.Hashtable;

public class Host {
    private String ipAddr;
    private Router connectedRouter;
    private boolean isWorking = true;
    boolean isListeningForMessages = true;
    ArrayList <Message> receivedMessages = new ArrayList<Message>();

    public Host(String ip, Router connectedR){
        this.ipAddr = ip;
        this.connectedRouter = connectedR;
    }

    public void getPacket(Message msg){
        receivedMessages.add(msg);
    }

    public void printMessages(){
        if(receivedMessages.size() > 0){
            int counter = 1;
            for(Message m : receivedMessages){
                System.out.println(m.toString());
            }
        }
        else
            System.out.println("No messages received.");

    }

    public boolean isWorking(){
       return this.isWorking;
    }

    public void changeStatus(boolean change){
        this.isWorking = change;
    }

    public String getIpAddr(){
        return this.ipAddr;
    }

    public Router getConnectedRouter(){
        return this.connectedRouter;
    }

    public void disconnectFromRouter(){
        this.connectedRouter = null;
        this.isWorking = false;
    }
    public void changeRouter(Router newRouter){
        if(newRouter != null){
            this.connectedRouter = newRouter;
            this.isWorking = true;
        }

    }

    public String toString(){
        String routerIp;
        if(isWorking)
            routerIp = connectedRouter.getIpAddr();
        else
            routerIp = "Not connected";

        return "IP address: " + ipAddr + "Connected router IP address: " + routerIp ;
    }
}
