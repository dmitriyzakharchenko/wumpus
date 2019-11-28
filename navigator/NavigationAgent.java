import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.List;
import java.util.Optional;

public class NavigationAgent extends Agent {
	HybridWumpusAgent navigator;

	protected void setup() {

		ServiceRegistration registrar = new ServiceRegistration(this, "navigation");
		registrar.registerService();

		addBehaviour(new OfferNavigation());
		addBehaviour(new AcceptNavigation());
	}


	protected void takeDown() {

		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		System.out.println("Navigation "+getAID().getName()+" terminating.");
	}

	private class OfferNavigation extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				String title = msg.getContent();
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.PROPOSE);
				reply.setContent("Free for navigation");
				System.out.println("Free for navigation");
				myAgent.send(reply);
			}
			else {
				block();
			}
		}

	}  //

	private class AcceptNavigation extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				System.out.println("Ready to start navigation");
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.INFORM);
				System.out.println("Navigation started " + msg.getSender().getName());
				myAgent.send(reply);
				addBehaviour(new StatusWaiter());
			}
			else {
				block();
			}
		}
	}
	private class StatusWaiter extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage msg = myAgent.receive(mt);

			if (msg != null) {
				System.out.println("Request for navigation");
				String content = msg.getContent();
				NavigatorMessage nm = new NavigatorMessage(content);
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.CFP);
				if (navigator == null) {
					navigator = new HybridWumpusAgent();
				}
				Optional<WumpusAction> action = navigator.act(nm);

				System.out.println("Action " + action);
				WumpusAction a = action.get();
				reply.setContent(a.toString());
				myAgent.send(reply);
				if ("CLIMB" == a.toString()) {
					myAgent.doDelete();
				}
			} else {
				block();
			}
		}
	}
}
