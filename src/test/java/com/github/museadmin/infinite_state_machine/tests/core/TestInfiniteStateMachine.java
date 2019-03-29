package com.github.museadmin.infinite_state_machine.tests.core;

import com.github.museadmin.infinite_state_machine.core.action_pack.ISMCoreActionPack;
import com.github.museadmin.infinite_state_machine.ism.InfiniteStateMachine;
import java.net.URL;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

class TestInfiniteStateMachine {

    protected InfiniteStateMachine infiniteStateMachine;
    public final String PROPERTIES = "infinite_state_machine.properties";

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Before
    public void setup() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL is = loader.getResource(PROPERTIES);
        ISMCoreActionPack ismCoreActionPack = new ISMCoreActionPack();
        infiniteStateMachine = new InfiniteStateMachine(is.getPath());
        infiniteStateMachine.importActionPack(ismCoreActionPack);
    }

}
