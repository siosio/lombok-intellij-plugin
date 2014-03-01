package de.plushnikov.intellij.plugin.agent;

import lombok.patcher.ScriptManager;

import java.lang.instrument.Instrumentation;

/**
 * This is a java-agent that patches some of idea's classes.
 */
public class IdeaPatcher {

  public static void agentmain(String agentArgs, Instrumentation instrumentation) throws Throwable {
    System.out.println("EXECUTED AGENT_MAIN");
    new IdeaPatcher().runAgent(agentArgs, instrumentation, true);
  }

  public static void premain(String agentArgs, Instrumentation instrumentation) throws Throwable {
    System.out.println("EXECUTED AGENT_PRE_MAIN");
    new IdeaPatcher().runAgent(agentArgs, instrumentation, false);
  }

  protected void runAgent(String agentArgs, Instrumentation instrumentation, boolean injected) throws Exception {
    System.out.println("Enter runAgent");
    ScriptManager sm = new ScriptManager();
    sm.registerTransformer(instrumentation);

    System.out.println("Before patching");
    patchIntellij(sm);
    System.out.println("After patching");

    sm.reloadClasses(instrumentation);
    System.out.println("Finished runAgent");
  }

  private static void patchIntellij(ScriptManager sm) {
    // TODO
    System.out.println("patching ....");
  }
}
