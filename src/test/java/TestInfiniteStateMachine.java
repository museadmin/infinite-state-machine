import com.github.museadmin.infinite_state_machine.core.InfiniteStateMachine;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestInfiniteStateMachine {

    protected InfiniteStateMachine infiniteStateMachine;

    @Before
    public void setup() {
        infiniteStateMachine = new InfiniteStateMachine();
    }
    @Test
    public void testInfiniteStateMachineReadsRdbms() {
        assertEquals("sqlite3", infiniteStateMachine.getRdbms());
    }
}
