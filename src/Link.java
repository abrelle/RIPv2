
/** Class for connecting routers. Could be modified for links to have various costs

 From graph perspective: Edges between nodes in a graph **/

public class Link {
    private Router router1;
    private Router router2;

    public Link(Router r1, Router r2){
        this.router1 = r1;
        this.router2 = r2;
    }

    public Router getValue0(){
        return this.router1;
    }

    public Router getValue1(){
        return this.router2;
    }

    public void setValue0(Router r1){
        this.router1 = r1;
    }

    public void setValue1(Router r2){
        this.router1 = r2;
    }

    public void printRoutersIp(){
        System.out.println("[ " +router1.getIpAddr() + " ] -- [ " + router2.getIpAddr() + " ]");
    }

    public String toString(){
        return router1.getIpAddr() + ',' + router2.getIpAddr();
    }
}
