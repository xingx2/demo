import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;

/**
 * Created by xing on 2017/3/10.
 */

public class Responser extends UntypedActor {
    String response;

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof Input) {
            System.out.println("Responser receives Input");
            Input input = (Input) o;
            response="Hi "+input.senderID+", I'm fine, thank you.";
            getSender().tell(new AskResult(response), getSelf());
        } else {
            System.out.println("Responser receives unknown type message: " + o);
        }
    }

    public static Props props() {
        return Props.create(new ResponserCreator());
    }

    private static class ResponserCreator implements Creator<Responser> {
        public Responser create() throws Exception {
            return new Responser();
        }
    }
}