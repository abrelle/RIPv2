//check ports when response, if source addresses match

import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Network {
    private CopyOnWriteArrayList<String> usedNames = new CopyOnWriteArrayList<>();
    private ArrayList<Link> links = new ArrayList<Link>();
    private ArrayList<Router> routers = new ArrayList<Router>();
    private ArrayList<Host> hosts = new ArrayList<Host>();
    private ArrayList<Thread> threads = new ArrayList<Thread>();

    public Network(ArrayList<Link> routerLink, ArrayList<Router> routerList, ArrayList<Host> hostList) throws InterruptedException {

        for(Router r: routerList ){
            addRouter(r);
        }

        for(Host h: hostList ){
            addHost(h);
        }

        for(Link l: routerLink){
            addLink(l);
        }

    }

    public Network(){
        this.links = null;
    }

    public void addLink(Link newLink){

        if (newLink != null) {
            links.add(newLink);
            //if new link added, both routers must "know" new neighbor
            newLink.getValue0().addNeighborRouter(newLink.getValue1());
            newLink.getValue0().changeStatus(true);
            newLink.getValue1().addNeighborRouter(newLink.getValue0());
            newLink.getValue1().changeStatus(true);

        }
    }
    public void addLink(String addr1, String addr2) {
        Router r1 = findRouter(addr1);
        Router r2 = findRouter(addr2);
        if (r1 != null && r2 != null) {
            Link newLink = new Link(r1, r2);
            links.add(newLink);
            //if new link added, both routers must "know" new neighbor
            r1.addNeighborRouter(r2);
            r1.changeStatus(true);
            r2.addNeighborRouter(r1);
            r2.changeStatus(true);
        }

    }

    public void broad(){
        for(Router r: routers)
            r.broadcastRoutingTable();
    }
    public void removeLink(String ip1, String ip2){
        Router r1 = findRouter(ip1);
        Router r2 = findRouter(ip2);
        if(r1 != null && r2 != null){
            Link oldLink = new Link(r1, r2);
            links.remove(oldLink);
            r1.removeNeighborRouter(r2);
            r2.removeNeighborRouter(r1);
            changeRouterConnectivity(oldLink);
        }

    }

    private void changeRouterConnectivity(Link oldLink) {
        Router router1 = oldLink.getValue0();
        Router router2 = oldLink.getValue1();
        boolean router1HasLinks = false;
        boolean router2HasLinks = false;
        for(Link l : links){
            if(l.getValue1() == router1 || l.getValue0() == router1)
                router1HasLinks = true;
            if(l.getValue1() == router2 || l.getValue0() == router2)
                router2HasLinks = true;
            if(router1HasLinks && router2HasLinks)
                return;
        }
        if(!router1HasLinks)
            router1.changeStatus(false);

        if(!router2HasLinks)
            router2.changeStatus(false);
    }

    public void removeLink(Router oldRouter){
        ArrayList<Link> tempLinks = new ArrayList<Link>();
        for(Link l: links){
            if(l.getValue1() == oldRouter || l.getValue0() == oldRouter)
                tempLinks.add(l);
        }

        for(Link l: tempLinks){
            links.remove(l);
            //System.out.println("Istrinamas linkas: "+l.toString());
        }
    }

    public void addRouter(Router newRouter) throws InterruptedException {

        if(!usedNames.contains(newRouter.getIpAddr())){
            routers.add(newRouter);
            usedNames.add(newRouter.getIpAddr());
        }
        else
            System.out.println("Router with this ip address exist.");
    }


    public void removeRouter(String addr){
        Router temp = null;
        for(Router r: routers){
            if(r.getIpAddr().equals(addr)){
                temp = r;
                break;
            }
        }
        if(temp != null){
            removeRouter(temp);
            usedNames.remove(temp.getIpAddr());
        }


    }

    public void removeRouter(Router oldRouter){

        if(oldRouter != null){
            oldRouter.changeStatus(false);
            routers.remove(oldRouter);
            removeLink(oldRouter);
            oldRouter.disconnectAllHosts();
            oldRouter.broadcastRoutingTable();
        }

    }

    public ArrayList<Router> getRouters(){
        return routers;
    }

    public void addHost(Host newHost){

        if(!usedNames.contains(newHost.getIpAddr())){
            hosts.add(newHost);
            usedNames.add(newHost.getIpAddr());
            newHost.getConnectedRouter().addHost(newHost);
        }
        else
            System.out.println("Host with this ip address exist.");

    }

    public void removeHost(Host oldHost){
        if(hosts.contains(oldHost)){
            hosts.remove(oldHost);
            usedNames.remove(oldHost.getIpAddr());
            oldHost.getConnectedRouter().removeHost(oldHost); //removing host from router host list
        }
    }

    public boolean isLink(Pair<Router, Router> newLink){
        return links.contains(newLink);
    }

    public ArrayList<Link> returnAllLink(){
        return links;
    }

    public void printNetwork(){
        for(Link l : links){
            l.printRoutersIp();
        }
    }

    public Router findRouter(String ipAddr){
        for(Router r: routers){
            if(r.getIpAddr().equals(ipAddr))
                return r;
        }
        return null;
    }

    public Host findHost(String ipAddr){
        for(Host h: hosts){
            if(h.getIpAddr().equals(ipAddr))
                return h;
        }
        return null;
    }

    public void sendPacket(String sender, String receiver){
        String s = null;
        String r = null;
        Router firstRouter = null;
        for(Host h : hosts){
            if(h.getIpAddr().equals(sender)){
                s = sender;
                firstRouter = h.getConnectedRouter();
            }
            else if(h.getIpAddr().equals(receiver))
                r = receiver;
        }
        if(r != null && s!= null && !r.equals(s) && firstRouter != null) {
            Packet p = new Packet(sender, receiver);
            firstRouter.forwardPacket(p);
        }
        else{
            System.out.println("Wrong input.");
        }
    }

    public void printRoutingTable(String ipAddr){
        Router temp = findRouter(ipAddr);
        if(temp != null)
            temp.printRoutingTable();
        else
            System.out.println("Router does not exist.");
    }
}
