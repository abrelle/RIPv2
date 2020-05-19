/*Each entry contains at least the following information:
    - The IPv4 address of the destination.
 
    - A metric, which represents the total cost of getting a datagram
      from the router to that destination.  This metric is the sum of the
      costs associated with the networks that would be traversed to get
      to the destination.
 
    - The IPv4 address of the next router along the path to the
      destination (i.e., the next hop).  If the destination is on one of
      the directly-connected networks, this item is not needed.
 
    - A flag to indicate that information about the route has changed
      recently.  This will be referred to as the "route change flag."
 
    - Various timers associated with the route.  See section 3.6 for more
      details on timers.
*/


public class RoutingTableEntry {
    
    private String dest_addr;
    private String next_hop_addr;
    private int metric;
    private boolean flag = true;
    private String subnetMask; //do subnetting
    //timers?


 public RoutingTableEntry(String dest, String next_hop, int new_metric, String subMask){
     this.dest_addr = dest;
     this.next_hop_addr = next_hop;
     this.metric = new_metric;
     this.subnetMask = subMask;
 }

    public int get_metric(){
        return this.metric;
    }
    public void set_metric(int new_metric){
        this.metric = new_metric;
    }

    public void set_dest_addr( String new_addr){
        this.dest_addr = new_addr;
    }

    public String get_dest_addr(){
        return this.dest_addr;
    }

    public void set_next_hop_addr( String new_addr){
        this.next_hop_addr = new_addr;
    }

    public String get_next_hop_addr(){
        return this.next_hop_addr;
    }

    public void set_flag(boolean value){
        this.flag = value;
    }
    public boolean get_flag(){
        return this.flag;
    }

    public void set_subnetMask( String new_subMask){
        this.subnetMask = new_subMask;
    }

    @Override
    public String toString() {
        return dest_addr + '\t' + subnetMask + '\t' + next_hop_addr + '\t'+ metric + '\t'+ flag ;
    }

    public String get_subnetMask(){
        return this.subnetMask;
    }

    public void subnetting(){

        String[] mask      = subnetMask.split("\\.");
        String[] ipAddress = dest_addr.split("\\.");
        StringBuffer ipSubnet  = new StringBuffer();
        for(int i=0; i<4; i++)
            try{
                if(ipSubnet.length()>0)
                    ipSubnet.append('.');
                ipSubnet.append(Integer.parseInt(ipAddress[i]) & Integer.parseInt(mask[i]));
            }catch(Exception x){
                //Integer parsing exception, wrong ipaddress or mask
                break;
            }
    }
}
