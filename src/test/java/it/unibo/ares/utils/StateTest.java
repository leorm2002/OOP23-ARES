package it.unibo.ares.utils;

import org.junit.jupiter.api.Test;

public class StateTest {
    @Test
    public void testState() {
        State state = new StateImpl(5, 5);
        assert state.getDimensions().getFirst() == 5;
        assert state.getDimensions().getSecond() == 5;
    }

    @Test
    public void testRadius(){
        State state = new StateImpl(5, 5);
        assert state.getPosByPosAndRadius(new PosImpl(0, 0), 1).size() == 3;
        assert state.getPosByPosAndRadius(new PosImpl(0, 0), 2).size() == 8;
        assert state.getPosByPosAndRadius(new PosImpl(0, 0), 3).size() == 15;
        assert state.getPosByPosAndRadius(new PosImpl(0, 0), 4).size() == 24;
        assert state.getPosByPosAndRadius(new PosImpl(1, 1), 1).size() == 8;

    }
}
