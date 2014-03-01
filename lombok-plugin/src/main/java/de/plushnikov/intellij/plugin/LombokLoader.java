package de.plushnikov.intellij.plugin;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import de.plushnikov.intellij.plugin.agent.IdeaPatcher;
import lombok.patcher.ClassRootFinder;
import lombok.patcher.inject.LiveInjector;
import org.jetbrains.annotations.NotNull;

/**
 * Main application component, that loads Lombok support
 */
public class LombokLoader implements ApplicationComponent {
  private static final Logger LOG = Logger.getInstance(LombokLoader.class.getName());

  @NotNull
  @Override
  public String getComponentName() {
    return "Lombok plugin for IntelliJ";
  }

  @Override
  public void initComponent() {
    LOG.info("Lombok plugin initialized for IntelliJ");

    LOG.info("pre injection");
    System.setProperty("lombok.patcher.safeInject", "true");
    LiveInjector liveInjector = new LiveInjector();
    liveInjector.inject(ClassRootFinder.findClassRootOfClass(IdeaPatcher.class));
    LOG.info("post injection");
  }

  @Override
  public void disposeComponent() {
    LOG.info("Lombok plugin disposed for IntelliJ");
  }
}
