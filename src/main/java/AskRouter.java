import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xing on 2017/3/10.
 */

public class AskRouter extends UntypedActor {
    private final Router router;

    public AskRouter(int numOfRoutees) {
        List<Routee> routees = new ArrayList(numOfRoutees);
        for (int i = 0; i < numOfRoutees; i++) {
            ActorRef actorRef= getContext().actorOf(Responser.props());
            getContext().watch(actorRef);
            routees.add(new ActorRefRoutee(actorRef));
        }
        router = new Router(new RoundRobinRoutingLogic(), routees);
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof Input) {
            System.out.println("AskRouter receives Input, routes to actor");
            router.route(o, getSender());
        } else {
            System.out.println("AskRouter receives unknow type of message: " + o);
        }
    }

    public static Props props(int numOfRoutees) {
        return Props.create(new AskRouterCreater(numOfRoutees));
    }

    private static class AskRouterCreater implements Creator<AskRouter> {

        private final int numOfRoutees;

        public AskRouterCreater(int numOfRoutees) {
            this.numOfRoutees = numOfRoutees;
        }

        public AskRouter create() throws Exception {
            return new AskRouter(numOfRoutees);
        }
    }
}