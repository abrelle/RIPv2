import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class RIPv2Simulation {

    private Network network = new Network();
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    public RIPv2Simulation(Network net) throws IOException, InterruptedException {
        this.network = net;

        while (true) {
            printMenu();

            int option = 0;
            String keyboardInput = in.readLine();
            if(!Character.isLetter(keyboardInput.charAt(0)))
                 option = getNumber(keyboardInput);
            String ip = null;
            String subnet = null;
            String ip2 = null;
            switch(option){
                case 1:
                    System.out.println("Enter ip of new router:");
                    ip = in.readLine();
                    System.out.println("Enter subnet mask address:");
                    subnet = in.readLine();
                    network.addRouter(new Router(ip,subnet));
                    break;
                case 2:
                    System.out.println("Enter ip address of the first router/host:");
                    ip = in.readLine();;
                    System.out.println("Enter ip address of the second router/host:");
                    ip2 = in.readLine();;
                    network.addLink(ip, ip2);
                    break;
                case 3:
                    System.out.println("Enter ip address of sender:");
                    ip = in.readLine();
                    System.out.println("Enter ip address of receiver:");
                    ip2 = in.readLine();
                    network.sendPacket(ip, ip2);
                    break;
                case 4:
                    System.out.println("Enter ip address of the first router/host:");
                    ip = in.readLine();
                    System.out.println("Enter ip address of the second router/host:");
                    ip2 = in.readLine();
                    network.removeLink(ip, ip2);
                    break;
                case 5:
                    System.out.println("Enter ip address of the router:");
                    ip = in.readLine();
                    network.removeRouter(ip);
                    break;
                case 6:
                    System.out.println("Enter ip address of the router:");
                    ip = in.readLine();
                    network.printRoutingTable(ip);
                    break;
                case 7:
                    System.exit(0);
                default:
                    System.out.println("Wrong input.");

            }
            System.out.println("\nPress enter to continue...");
            ip = in.readLine();


        }
    }
    void printMenu(){

        System.out.println("---Choose an option----:");
        System.out.println("1. Add new router.");
        System.out.println("2. Add new link.");
        System.out.println("3. Send a package host-host.");
        System.out.println("4. Remove a link.");
        System.out.println("5. Remove a router.");
        System.out.println("6. Print router's routing table.");
        System.out.println("7. Exit.");
    }
    private static int getNumber(String str){
        try {
            int temp = Integer.parseInt(str);
            return temp;
        }
        catch (NumberFormatException e)
        {
            System.out.println("Wrong input. Try Again.");
            return -1;
        }
    }

}
