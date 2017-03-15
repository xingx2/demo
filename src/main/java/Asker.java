import akka.pattern.Patterns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

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