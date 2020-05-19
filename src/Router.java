
//in RIP every router that
//   participates in routing sends an update message to all its neighbors
//   once every 30 seconds. we wait for 180
//   seconds before timing out

// A
//   specific metric value is chosen to indicate an unreachable
//   destination; that metric value is larger than the largest valid
//   metric that we expect to see.

//Implementations should allow the system
//   administrator to ArrayList the metric of each network.
//In addition to the
//   metric, each network will have an IPv4 destination address and subnet
//   mask associated with it.  These are to be ArrayList by the system
//   administrator in a manner not specified in this protocol.

//
//To support the extensions detailed in this document, each entry must
//   additionally contain a subnet mask.
//Implementors may also choose to allow the system administrator to
//   enter additional routes.  These would most likely be routes to hosts
//   or networks outside the scope of the routing system.  They are
//   referred to as "static routes."  Entries for destinations other than
//   these initial ones are added and updated by the algorithms described
//   in the following sections.

//RIP is a UDP-based protocol.  Each router that uses RIP has a routing
//   process that sends and receives datagrams on UDP port number 520, the
//   RIP-1/RIP-2 port

//receives datagrams on UDP port number 520

import javafx.util.Pair;

import java.util.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.HashMap;
import static java.lang.Thread.sleep;

public class Router implements Runnable{
    private CopyOnWriteArrayList<RoutingTableEntry> routingTable = new CopyOnWriteArrayList<RoutingTableEntry>();
    private CopyOnWriteArrayList<RoutingTableEntry> deletedRoutes = new CopyOnWriteArrayList<RoutingTableEntry>();
    private ArrayList<Host> nodes = new ArrayList<Host>();
    private String ipAddr;
    private String subnetMask;
    private boolean isWorking = true;
    private CopyOnWriteArrayList<Router> neighbors = new CopyOnWriteArrayList<Router>();   //neighbors
    private HashMap<Router, Long> updateTimes = new HashMap<Router, Long>(); //neighbors and update times
    public static final int INFINITY = 16;
    public static final int COST = 1;
    public static final int RESPONSE = 2;
    public static final int REQUEST = 1;
    public static final int BROADCASTTIME = 10000;
    public static final int GARBAGECOLLECTORTIME = 200;
    private Long garbageCollectorTime;
    private boolean hasRecvPacket = false;
    private Packet recvPacket;
    private Thread t;



    public Router(String newIpAddr, String newSubnetMask) {
        this.ipAddr = newIpAddr;
        this.subnetMask = newSubnetMask;
        routingTable.add(new RoutingTableEntry(this.ipAddr, this.ipAddr, 0, this.subnetMask));
        garbageCollectorTime = System.currentTimeMillis();
        t = new Thread(this);
        t.start();

    }


    /**________________Begin BROADCASTING table___________________*/

    @Override
    public void run() {
        int counter = 0;
        while (isWorking) {
            if(!neighbors.isEmpty())
            {
                broadcastRoutingTable(); //send neighbors routers table

            }
            selfUpdate();
            try {
                sleep(BROADCASTTIME);
                //count how many times 30 seconds have passed :D, after 6 counts (180sec) checks if router is unreachable
                ++counter;
                if(counter == 2){
                    checkForTooLongUpdates();
                    counter = 0;
                }
                //----------------------------------
            } catch (InterruptedException e) {
                System.out.println("Sleep error.");
            }
        }
    }


    /**Sends all neighbors routing table*/

    public void broadcastRoutingTable(){
        for(Router r : neighbors){

            r.updateRouterTable(this);  //sending routing table to all neighbors //NESIUNCIA IR NEGAUNA IS ISTRINTO
        }
        this.checkToDeleteMarkedRoutes();
    }

    /**If no respose after 180 sec => marks route as unreachable and stops checking*/
    public void checkForTooLongUpdates(){
        Long end = System.currentTimeMillis();
        ArrayList<Router> routersToDelete = new ArrayList<>();
        for(Map.Entry<Router, Long> entry : updateTimes.entrySet()){
            Long difference = (end - entry.getValue())/1000L;
            //System.out.println(this.ipAddr + " TIKRINA " + entry.getKey().getIpAddr() + " tarpas " + difference);
            if(difference >= 20){
                //System.out.println(this.ipAddr + " RADO " + entry.getKey().getIpAddr() + " LAIKAS " + difference);
                routersToDelete.add(entry.getKey());
                markRouteAsExpired(entry.getKey().getIpAddr());  //marks routes to be deleted
            }
        }
        for(Router r: routersToDelete){
            updateTimes.remove(r);  //deletes router from array for check times
        }
    }

    void selfUpdate(){
        deleteExpiredRoutes();
        if(neighbors.isEmpty()){
            routingTable.clear();
            routingTable.add(new RoutingTableEntry(this.ipAddr,this.ipAddr,0, this.subnetMask));
            for(Host h : nodes){
                routingTable.add((new RoutingTableEntry(h.getIpAddr(),this.ipAddr,1, this.subnetMask)));
            }

        }
    }


    /**_____________________________________________METHODS FOR ROUTER TABLE________________________________*/
    public void updateRouterTable(Router source) {
        this.updateTime(source);
        boolean timeToDeleteExpired = false;
        synchronized (routingTable) {
            CopyOnWriteArrayList<RoutingTableEntry> neighborsTable = source.getRoutingTable();
            CopyOnWriteArrayList<RoutingTableEntry> newDestinations = new CopyOnWriteArrayList<>();
            boolean hasFindDestination;

            for (RoutingTableEntry sourceTableEntry : neighborsTable) {
                hasFindDestination = false;  //if entry is not in the table
                for (RoutingTableEntry currentTableEntry : routingTable) {

                    //System.out.println(this.ipAddr + " gavo " + sourceTableEntry.get_dest_addr() + " metric " + sourceTableEntry.get_metric());
                    if (sourceTableEntry.get_dest_addr().equals(currentTableEntry.get_dest_addr())) {

                        boolean isNode = false;
                        int currentMetric = currentTableEntry.get_metric();
                        int sourceMetric = sourceTableEntry.get_metric();

                        if(sourceMetric >= INFINITY && currentMetric < INFINITY){
                            markRouteAsExpired(currentTableEntry.get_dest_addr());
                        }

                        else if (sourceMetric < currentMetric && currentMetric < INFINITY) {
                                sourceMetric++;
                                currentTableEntry.set_metric(sourceMetric);
                                currentTableEntry.set_next_hop_addr(source.getIpAddr());
                        }
                        hasFindDestination = true;
                    }
                }

                if (!sourceTableEntry.get_dest_addr().equals(this.ipAddr) && !hasFindDestination && sourceTableEntry.get_metric() < INFINITY)
                    newDestinations.add(new RoutingTableEntry(sourceTableEntry.get_dest_addr(), source.getIpAddr(), sourceTableEntry.get_metric() + COST, sourceTableEntry.get_subnetMask()));

            }
            this.routingTable.addAll(newDestinations);
        }
    }

    /**Updates last time routing table was received from neighbor*/
    public void updateTime(Router source){
        Long noUpdateTimes = updateTimes.get(source);
        long difference = (System.currentTimeMillis() - noUpdateTimes)/1000L;
        //System.out.println("Praejo laiko " + difference);
        updateTimes.replace(source, System.currentTimeMillis());
    }



    /**___________________________________________________________________________________________________________________*/


    /**-------------------------------------------METHODS TO DEAL WITH EXPIRED ROUTES--------------------------------------------------*/



    /**Delete routes from array of recently deleted routes*/
    public void checkToDeleteMarkedRoutes(){
        Long end = System.currentTimeMillis();

        if((end - garbageCollectorTime)/1000L >= GARBAGECOLLECTORTIME){
            deletedRoutes.clear();
            garbageCollectorTime = System.currentTimeMillis();
        }
        deleteExpiredRoutes();
    }

    /**If received table entry has unreachable destination, mark this Route expired*/
    public void markRouteAsExpired(String destAddr){
        for(RoutingTableEntry entry : routingTable){
            if(entry.get_dest_addr().equals(destAddr)){
                if(!deletedRoutes.contains(entry))
                    entry.set_metric(INFINITY);
                    deletedRoutes.add(entry);
                    removeNeighborRouter(entry.get_dest_addr());
                //System.out.println(this.ipAddr + " pazymejo inf -> " + entry.get_dest_addr());
            }
        }
    }

    /**Deletes routes from table*/
    public void deleteExpiredRoutes(){

        for(RoutingTableEntry m : deletedRoutes){
            for(RoutingTableEntry r : routingTable){
                if(r.get_next_hop_addr().equals(m.get_dest_addr())){
                    removeEntry(r);
                }
            }
            //System.out.println(this.ipAddr +  " IStrina: " + m.get_next_hop_addr());
            removeEntry(m);
            removeNeighborRouter(m.get_dest_addr());
        }
        //doublecheck
        routingTable.removeIf(r -> r.get_metric() >= INFINITY);
    }


    /**______________________________METHODS FOR NEIGHBORS___________________________*/

    /**Add new neighbor */
    public  void addNeighborRouter(Router r) {
        this.neighbors.add(r);
        RoutingTableEntry entry = new RoutingTableEntry(r.getIpAddr(),this.ipAddr,1,r.getSubnetMask());
        routingTable.add(entry);
        updateTimes.put(r, System.currentTimeMillis());
    }

    /**Remove new neighbor */
    public void removeNeighborRouter(Router r) {
        if(neighbors.contains(r)){
            this.neighbors.remove(r);
            //System.out.println("is " + this.ipAddr + " isrtrintas kaimynas: " + r.getIpAddr());
        }

    }

    /**Remove new neighbor */
    public void removeNeighborRouter(String ipAddr) {
        Router temp = null;
        for(Router r : neighbors){
            if(r.getIpAddr().equals(ipAddr)){
                temp = r;
            }
        }
        neighbors.remove(temp);
    }

    /**Print all neighbors */
    public void printNeighbors(){
        for(Router r: neighbors){
            System.out.println(r.toString());
        }
    }

    /** True if neighbor exists*/
    public boolean isNeighbor(String ipAddr){
        for(Router r : neighbors){
            if(r.getIpAddr().equals(ipAddr))
                return true;
        }
        return false;
    }

    /**Return all neighbors*/
    public CopyOnWriteArrayList<Router> getNeighbors() {
        return this.neighbors;
    }
    /**____________________________________________________________________________________________*/



    /**________________________________________METHODS FOR HOSTS-----------------------------------*/

    /**True if host exist */
    public boolean isHost(String ipAddr){
        for(Host h : nodes){
            if(h.getIpAddr().equals(ipAddr))
                return true;
        }
        return false;
    }

    /**Add new host*/
    public void addHost(Host newHost) {
        this.nodes.add(newHost);
        routingTable.add(new RoutingTableEntry(newHost.getIpAddr(), this.ipAddr, 1,this.subnetMask));
    }

    /**Add arrayList of hosts*/
    public void addHosts(ArrayList<Host> newHosts) {
        this.nodes.addAll(newHosts);
    }

    /**Remove host*/
    public void removeHost(Host oldHost) {
        if(nodes.contains(oldHost))
            this.nodes.remove(oldHost);
    }

    /**Print info about all hosts*/
    public void printHosts(){
        for(Host h: nodes){
            System.out.println(h.toString());
        }
    }

    /**Disconnects all hosts*/
    public void disconnectAllHosts(){
        for(Host h : nodes)
            h.disconnectFromRouter();

        //for(Host h : nodes)
        // System.out.println(h.isWorking());
    }


    /**_____________________________________________________________________________________________________________*/


    /**____________________________________METHODS FOR ROUTER INFO-------------------------------------*/


    public String getIpAddr() {
        return this.ipAddr;
    }

    public void setIpAddr(String newIpAddr) {
        this.ipAddr = newIpAddr;
    }

    public String getSubnetMask() {
        return this.subnetMask;
    }

    public void setSubnetMask(String newSubnetMask) {
        this.subnetMask = newSubnetMask;
    }

    public CopyOnWriteArrayList<RoutingTableEntry> getRoutingTable() {
        return this.routingTable;
    }


    /** Change router working status  */
    public void changeStatus(boolean change){
        this.isWorking = change;
        changeSelfEntry(change);
        //changeDependencies();
    }


    /**   If router is not working, then set own metric to INFINITY     */
    public void changeSelfEntry(boolean isWorking){
        if(!isWorking){
            for (RoutingTableEntry e:routingTable) {
                if(e.get_dest_addr().equals(this.ipAddr)){
                    e.set_metric(INFINITY);
                }
                for(Host h : nodes){
                    if(e.get_dest_addr().equals(h.getIpAddr()))
                        e.set_metric(INFINITY);
                }
            }
        }
    }

    /**Changes flag status of routers or hosts which singly depends on removed router*/
    public void changeDependencies(){
        if(!isWorking){
            for(Host h : nodes)
                h.changeStatus(false);


        }
    }

    /**Removes routing table entry*/
    public void removeEntry(RoutingTableEntry oldEntry){
        routingTable.remove(oldEntry);

    }

    /**Removes routing table entry*/
    public void removeEntry(Router oldRouter){
        int index = 0;
        for(RoutingTableEntry e : routingTable){
            if(e.get_dest_addr().equals(oldRouter.getIpAddr()))
                break;
            ++index;
        }

        routingTable.remove(index);
        //System.out.println("turejo istrinti " + oldRouter.getIpAddr());
        //this.printRoutingTable();
    }


    /**  Get thread status  */
    public Thread.State getState(){
        return t.getState();
    }

    /**Prints routing table*/
    public void printRoutingTable(){
         System.out.println("Destination IP address \t Subnet mask \t Next hop address \t Metric \t Is working  ");
        for(RoutingTableEntry r : routingTable)
            System.out.println(r.toString());
    }

    public String toString(){
        return "IP: " + this.ipAddr + " Subnet Mask: " + this.subnetMask + " Is working: " + this.isWorking;
    }


    /**______________________________________________________________________________________________________*/

    public void forwardPacket(Packet p){
        for(Host h : nodes){
            if(h.getIpAddr().equals(p.getReceiverIP())){
                System.out.println("Packet has been received");
                p.addToPath(this.ipAddr);
                p.addToPath(h.getIpAddr());
                p.printPath();
                return;
            }
        }
        boolean error = true;

        for(RoutingTableEntry entry : routingTable){
            if(entry.get_dest_addr().equals(p.getReceiverIP())){
                for(Router r : neighbors){
                    if(entry.get_next_hop_addr().equals(r.ipAddr) && entry.get_metric() < INFINITY){
                        if(r != null)
                        {
                            System.out.println(r.getIpAddr());
                            if(p.addToPath(this.ipAddr)){
                                break;
                            }
                            r.forwardPacket(p);
                            error = false;
                            break;
                        }

                    }
                }
            }
        }

        if(error){
            System.out.println("Error. Could not forward package.");
            p.printPath();
        }
    }

}
