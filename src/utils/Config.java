package utils;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.Nullable;

public class Config implements PersistentStateComponent<Config.State> {
    private State state = new State();

    public static Config getInstance() {
        return ServiceManager.getService(Config.class);
    }

    @Nullable
    @Override
    public State getState() {
        return this.state;
    }

    @Override
    public void loadState(State state) {
        XmlSerializerUtil.copyBean(state, this.state);
    }

    public Config() {
        state.user = "";
    }
    public void init(){
        State read= PropertyUtil.readState();
        if(!TextUtils.isEmpty(read.user)){
            state.user=read.user;
        }
    }



    public static class State {
        public String user;
    }
}
