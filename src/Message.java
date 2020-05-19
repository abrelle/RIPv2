


public class Message{
   private String destinationAddr;
   private String sourceAddr;
   private String msgBody;




    public Message(String src, String dest, String text) {
        this.destinationAddr = dest;
        this.sourceAddr = src;
        this.msgBody = text;

    }

    public String getDestinationAddr(){
        return destinationAddr;
    }

    public String getSourceAddr(){
        return sourceAddr;
    }

    public String getMsgBody(){
        return msgBody;
    }

    @Override
    public String toString() {
        return  "Source address: " + sourceAddr + '\n' + "Destination address: " + sourceAddr + '\n'
                + "----------------Message body---------------" + '\n' +msgBody;
    }
}
