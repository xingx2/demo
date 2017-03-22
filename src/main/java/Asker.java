import akka.pattern.Patterns;
import avroService.Mail;
import avroService.Message;
import org.apache.avro.AvroRemoteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by xing on 2017/3/10.
 */
public class Asker implements Runnable{
    int seq;
    String serviceName;
    private static final Logger LOG = LoggerFactory.getLogger(Asker.class);

    Asker(int seq, String serviceName){
        this.seq=seq;
        this.serviceName=serviceName;
    }

    public void run(){
        String str = "How are you.";
        Input input=new Input(seq, str);
        LOG.info(seq+" : "+ input.words);

        /*flume build in client
        FlumeAvroClient client = new FlumeAvroClient();
        // Initialize client with the remote Flume agent's host and port
        client.init("127.0.0.1", 23333);

        // Send 10 events to the remote Flume agent. That agent should be
        // configured to listen with an AvroSource.
        String sampleData = seq+" : Hello Flume!";
        for (int i = 0; i < 10; i++) {
            client.sendDataToFlume(sampleData);
        }

        client.cleanUp();
        */

        /*custom avro client*/

        CustomAvroClient customAvroClient = new CustomAvroClient();
        try {
            Mail proxy =customAvroClient.init("127.0.0.1",23333);
            String[] args = {"Alice","Bob","How are you?"};
            customAvroClient.sendData(proxy,args);
            customAvroClient.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Engine engine=Engine.getEngine();
        scala.concurrent.Future<Object> result = Patterns.ask(engine.askRouter, input, 5000);
        try {
            AskResult askResult = (AskResult) Await.result(result, Duration.create(5, TimeUnit.SECONDS));
            LOG.info(seq+" : I receive :'"+askResult.getResponse()+"'.");
        } catch (Exception e) {
            System.out.println(seq+" : Ask processes fail: " + e.getMessage());
        }
    }

    public static void main(String[] args){
        for (int i=1;i<=5;i++){
            Thread thread = new Thread(new Asker(i, "hello-service"));
            thread.start();
        }
    }
}