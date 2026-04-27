package hooks;

import base.BaseClass;
import io.cucumber.java.Before;

public class Hooks {

    @Before
    public void setBaseUrl() {
        BaseClass.setup();
    }

}