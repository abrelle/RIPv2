//format: command 1 bytes, version 1 bytes, must be zero 2 bytes, rip entry 20 bytes
//rip-2 format: 0                   1                   2                   3 3
//    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
//   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
//   | Address Family Identifier (2) |        Route Tag (2)          |
//   +-------------------------------+-------------------------------+
//   |                         IP Address (4)                        |
//   +---------------------------------------------------------------+
//   |                         Subnet Mask (4)                       |
//   +---------------------------------------------------------------+
//   |                         Next Hop (4)                          |
//   +---------------------------------------------------------------+
//   |                         Metric (4)                            |
//   +---------------------------------------------------------------+

//Response message contains the complete routing table
// own address
// he metric is greater
//   than infinity, ignore the entry but log the event.  The basic
//   validation tests are:
//
//   - is the destination address valid (e.g., unicast; not net 0 or 127)
//   - is the metric valid (i.e., between 1 and 16, inclusive)

//This section describes how a Response message is generated for a
//   particular directly-connected network:
//
//   Set the version number to either 1 or 2.  The mechanism for deciding
//   which version to send is implementation specific; however, if this is
//   the Response to a Request, the Response version should match the
//   Request version.  Set the command to Response.  Set the bytes labeled
//   "must be zero" to zero.  Start filling in RTEs.  Recall that there is
//   a limit of 25 RTEs to a Response; if there are more, send the current
//   Response and start a new one.  There is no defined limit to the
//   number of datagrams which make up a Response.
//
//   To fill in the RTEs, examine each route in the routing table.  If a
//   triggered update is being generated, only entries whose route change
//   flags are set need be included.  If, after Split Horizon processing,
//   the route should not be included, skip it.  If the route is to be
//   included, then the destination address and metric are put into the
//   RTE.  Routes must be included in the datagram even if their metrics
//   are infinite.

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CopyOnWriteArrayList;

public class Packet2{
    private int command;
    private String destinationAddr;
    private String subnet;
    private String nextHop;
    private int metric;
    private byte[] header;
    private byte[] body;
    private int  version = 2;



    public Packet2(int new_command, String new_destinationAddr, String new_subMask, String new_next_hop, int new_metric) throws IOException {
        this.command = new_command;
        this.destinationAddr = new_destinationAddr;
        this.subnet = new_subMask;
        this.nextHop = new_next_hop;
        this.metric = new_metric;
        formatPacketHeader();
    }

    private void formatRoutingTable() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write((byte)command );
        outputStream.write((byte)version);
        outputStream.write((byte)0);
        outputStream.write((byte)10);
        outputStream.write(address_to_bytes(destinationAddr));
        outputStream.write(address_to_bytes(subnet));
        outputStream.write(address_to_bytes(nextHop));
        outputStream.write((byte)metric); // if metric
        header = outputStream.toByteArray();

    }

    private byte[] address_to_bytes(String address) throws IOException {
        byte[] addrByte ;
        String[] ipNumbers = address.split("\\.");
        int intByte1 = Integer.parseInt(String.valueOf(ipNumbers[0]));
        int intByte2 = Integer.parseInt(String.valueOf(ipNumbers[1]));
        int intByte3 = Integer.parseInt(String.valueOf(ipNumbers[2]));
        int intByte4 = Integer.parseInt(String.valueOf(ipNumbers[3]));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write((byte)intByte1);
        outputStream.write((byte)intByte2);
        outputStream.write((byte)intByte3);
        outputStream.write((byte)intByte4);
        addrByte = outputStream.toByteArray();
        return addrByte;
    }

    public void addPacketBody(CopyOnWriteArrayList<RoutingTableEntry> r){
       //add maximum 25 table entries

    }


    private void formatPacketHeader() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write((byte)command );
        outputStream.write((byte)version);
        outputStream.write((byte)0);//idk about flags
        outputStream.write((byte)0);//idk about flags
        outputStream.write(address_to_bytes(destinationAddr));
        outputStream.write(address_to_bytes(subnet));
        outputStream.write(address_to_bytes(nextHop));
        outputStream.write((byte)metric); // if metric
        header = outputStream.toByteArray();
    }

    private void printPacket(){

    }

    public int getCommand(){
        return this.command;
    }

    public String getPacketHeader(){
        return command+ " " + 0 + " "+ 0+ " " + version + " " +destinationAddr+" "+subnet+" "+nextHop+ " " +metric;
    }

    public String getDestinationAddr(){
        return destinationAddr;
    }
}
