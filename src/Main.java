//for each destination has to keep metric(cost) and neighboring router
//If there are several equally good paths, it is the first router on one of them.
//route is combination of destination, metric and router
//existing metric is kept until smaller one shows up
/*
1) keep a table with an entry for every possible destination in the system (there should be an entry for the entity itself, with metric 0,)
2)Periodically, send a routing update to every neighbor The update
     is a set of messages that contain all of the information from the
     routing table.  It contains an entry for each destination, with the
     distance shown to that destination.
3) When a routing update arrives from a neighbor G', add the cost
     associated with the network that is shared with G'.  (This should
     be the network over which the update arrived.)  Call the resulting distance D'.  Compare the resulting distances with the current
     routing table entries.  If the new distance D' for N is smaller
     than the existing value D, adopt the new route.  That is, change
     the table entry for N to have metric D' and router G'.  If G' is
     the router from which the existing route came, i.e., G' = G, then
     use the new metric even if it is larger than the old one.
 */

// timers:
// - The 30-second updates are triggered by a clock whose rate is not
//     affected by system load or the time required to service the
//     previous update timer.
//
//   - The 30-second timer is offset by a small random time (+/- 0 to 5
//     seconds) each time it is set.  (Implementors may wish to consider
//     even larger variation in the light of recent research results [10])
//
// There are two timers associated with each route, a "timeout" and a
//   "garbage-collection" time.

// Should a new route to this network be established while the garbage-
//   collection timer is running, the new route will replace the one that
//   is about to be deleted.  In this case the garbage-collection timer
//   must be cleared.

// A Response can be received for one of several different reasons:
//
//   - response to a specific query
//   - regular update (unsolicited response)
//   - triggered update caused by a route change

//The basic
//   validation tests are:
//
//   - is the destination address valid (e.g., unicast; not net 0 or 127)
//   - is the metric valid (i.e., between 1 and 16, inclusive)


//Adding a route to the routing
//   table consists of:
//
//   - Setting the destination address to the destination address in the
//     RTE
//
//   - Setting the metric to the newly calculated metric (as described
//     above)
//
//   - Set the next hop address to be the address of the router from which
//     the datagram came
//
//   - Initialize the timeout for the route.  If the garbage-collection
//     timer is running for this route, stop it (see section 3.6 for a
//     discussion of the timers)
//
//   - Set the route change flag
//
//   - Signal the output process to trigger an update (see section 3.8.1)


import javafx.util.Pair;
import sun.nio.ch.Net;

import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        ArrayList<Router> routers = new ArrayList<Router>();
        routers.add(new Router("157.240.6.56", "255.255.255.192"));
        routers.add(new Router("93.84.118.18", "255.255.255.192") );
        routers.add(new Router("15.241.60.93", "255.255.255.192"));
        routers.add(new Router("185.76.232.220","255.255.255.192"));
        routers.add(new Router("181.176.80.18","255.255.255.192"));
        routers.add(new Router("63.100.30.194","255.255.255.192"));
        ArrayList<Link> links = new ArrayList<Link>();
        links.add(new Link( routers.get(0), routers.get(1)));
        links.add(new Link(routers.get(0), routers.get(2)));
        links.add(new Link(routers.get(2), routers.get(3)));
        links.add(new Link(routers.get(1), routers.get(4)));
        links.add(new Link(routers.get(3), routers.get(4)));
        links.add(new Link(routers.get(4), routers.get(5)));
        ArrayList<Host> hosts = new ArrayList<Host>();
        hosts.add(new Host("74.84.196.120", routers.get(0)));
        hosts.add(new Host("81.208.94.0", routers.get(0)));
        hosts.add(new Host("185.152.68.0", routers.get(1)));
        hosts.add(new Host("212.77.12.144", routers.get(3)));
        hosts.add(new Host("57.79.216.0", routers.get(3)));
        hosts.add(new Host("185.152.71.255", routers.get(3)));
        hosts.add(new Host("193.43.131.255", routers.get(2)));
        hosts.add(new Host("57.79.223.255", routers.get(5)));
        hosts.add(new Host("41.60.143.2", routers.get(5)));
        hosts.add(new Host("41.60.241.255", routers.get(5)));
        Network net = new Network(links, routers, hosts);
       // net.printNetwork();
        //routers.get(0).printNeighbors();
        //routers.get(0).printRoutingTable();
        /*routers.get(4).broadcastRoutingTable();
        routers.get(0).broadcastRoutingTable();
        routers.get(1).broadcastRoutingTable();
        routers.get(2).broadcastRoutingTable();
        routers.get(3).broadcastRoutingTable();
        routers.get(5).broadcastRoutingTable();
        routers.get(5).printHosts();
        routers.get(5).printRoutingTable();*/
        new RIPv2Simulation(net);

    }


}
