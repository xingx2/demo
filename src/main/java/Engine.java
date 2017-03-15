import akka.actor.ActorRef;
import akka.actor.ActorSystem;

/**
 * Created by xing on 2017/3/10.
 */
public class Engine {
    ActorSystem actorSystem= ActorSystem.create();
    final ActorRef askRouter = actorSystem.actorOf(AskRouter.props(3));

    private static final Engine engine = new Engine();

    public static Engine getEngine() {
        return engine;
    }
}
