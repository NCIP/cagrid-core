package org.cagrid.tests.data.styles.cacore42;

import java.util.List;
import java.util.Map;

public class ExecutableCommand {

    private List<String> commandLine = null;
    private Map<String, String> environment = null;
    
    public ExecutableCommand(List<String> command, Map<String, String> environment) {
        this.commandLine = command;
        this.environment = environment;
    }
    
    
    public List<String> getCommandLine() {
        return commandLine;
    }
    
    
    public Map<String, String> getEnvironment() {
        return environment;
    }
    
    
    public String[] getCommandArray() {
        return commandLine.toArray(new String[commandLine.size()]);
    }
    
    
    public String[] getEnvironmentArray() {
        String[] envp = new String[environment.size()];
        int index = 0;
        for (String env : environment.keySet()) {
            envp[index] = env + "=" + environment.get(env);
            index++;
        }
        return envp;
    }
}
