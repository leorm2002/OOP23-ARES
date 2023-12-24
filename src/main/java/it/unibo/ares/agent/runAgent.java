package it.unibo.ares.agent;

import java.util.Random;

import it.unibo.ares.utils.Pos;
import it.unibo.ares.utils.PosImpl;
import it.unibo.ares.utils.State;
import it.unibo.ares.utils.StateImpl;

public class runAgent {
    
    private void getRandom(State s){
        ConcreteAgentFactory factory = new ConcreteAgentFactory();

        Random r = new Random();

        int x = r.nextInt(s.getDimensions().getFirst());
        int y = r.nextInt(s.getDimensions().getFirst());

        Pos pos = new PosImpl(x, y);
        //Random 1 or 2
        int type = r.nextInt(3) + 1;

        if(!s.getAgentAt(pos).isPresent()){
            s.addAgent(pos,  factory.getSchellingSegregationModelAgent(type, 0.8, 1));
        }
    }
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void main(String[] args) throws InterruptedException {
        ConcreteAgentFactory factory = new ConcreteAgentFactory();
        runAgent r = new runAgent();
        //Let's create a 5x5 state
        StateImpl state = new StateImpl(15, 15);
        
        for(int i = 0; i < 113; i++) {
            r.getRandom(state);
        }
            

        System.out.println("--Initial state--");


        state.debugPring();
        System.out.println("");

        System.out.println("--After 10 iteration--");

        
        for(int i = 0; i < 10; i++) {
            var agents = state.getAgents();
            for(var agent : agents) {

                agent.getSecond().tick(state, agent.getFirst());
                if(!state.getAgentAt(agent.getFirst()).isPresent()){
                }

            }
        }
        state.debugPring();


        System.out.println("--After 20 iteration--");

        
        for(int i = 0; i < 10; i++) {
            var agents = state.getAgents();
            for(var agent : agents) {

                agent.getSecond().tick(state, agent.getFirst());
                if(!state.getAgentAt(agent.getFirst()).isPresent()){
                }

            }
        }
        state.debugPring();


    }
}
