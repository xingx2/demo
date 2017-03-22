import avroService.Mail;
import avroService.Message;
import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.NettyTransceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.avro.util.Utf8;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by xing on 2017/3/21.
 */
public class CustomAvroClient {
    private NettyTransceiver client;
    private String hostname;
    private int port;

    public Mail init(String hostname, int port) throws IOException {
        this.hostname=hostname;
        this.port=port;
        client = new NettyTransceiver(new InetSocketAddress(23333));
        ///获取Mail接口的proxy实现
        Mail proxy = SpecificRequestor.getClient(Mail.class, client);
        System.out.println("Client of Mail Proxy is built");
        return proxy;
    }

    public void sendData(Mail proxy,String[] args) throws AvroRemoteException {

        // fill in the Message record and send it
        Message message = new Message();
        message.setTo(new Utf8(args[0]));
        message.setFrom(new Utf8(args[1]));
        message.setBody(new Utf8(args[2]));
        System.out.println("RPC call with message:  " + message.toString());

        ///底层给服务器发送send方法调用
        System.out.println("Result: " + proxy.send(message));
    }

    public void cleanup(){
        // cleanup
        client.close();
    }
}
