import com.github.museadmin.infinite_state_machine.core.InfiniteStateMachine;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;

public class TestInfiniteStateMachine {

    protected InfiniteStateMachine infiniteStateMachine;

    @Before
    public void setup() {
        infiniteStateMachine = new InfiniteStateMachine();
    }
    @Test
    public void testInfiniteStateMachineReadsRdbms() {
        assertTrue(infiniteStateMachine.getRdbms().equalsIgnoreCase("sqlite3") ||
            infiniteStateMachine.getRdbms().equalsIgnoreCase("postgres"));
    }
}
