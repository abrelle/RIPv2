import java.util.concurrent.CopyOnWriteArrayList;

public class Packet {
    private String senderIP;
    private String receiverIP;
    private Router lastRouter = null;
    CopyOnWriteArrayList<String> path = new CopyOnWriteArrayList<>();

    public Packet(String sender, String receiver){
        this.senderIP = sender;
        this.receiverIP = receiver;
        this.addToPath(sender);
    }

    public boolean addToPath(String r){
        if(path.contains(r))
            return false;
        path.add(r);
        return true;
    }



    public String getReceiverIP(){
        return this.receiverIP;
    }

    public String getSenderIPerIP(){
        return this.senderIP;
    }

    public void printPath(){
        System.out.print("Packet path: ");
        boolean first = true;
        for (String r : path){
            if(first){
                System.out.print(r);
                first = false;
            }
            else
                System.out.print(" -> " + r);
        }
        System.out.println();
    }
}
