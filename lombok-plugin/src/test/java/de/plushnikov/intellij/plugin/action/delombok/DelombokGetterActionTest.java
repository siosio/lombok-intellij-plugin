package de.plushnikov.intellij.plugin.action.delombok;

import com.intellij.openapi.actionSystem.AnAction;
import de.plushnikov.intellij.plugin.action.LombokLightActionTest;

public class DelombokGetterActionTest extends LombokLightActionTest {

  protected AnAction getAction() {
    return new DelombokGetterAction();
  }

  @Override
  protected String getBasePath() {
    return super.getBasePath() + "/action/delombok/getter";
  }

  public void testGetterFields() throws Exception {
    doTest();
  }

  public void testGetterClass() throws Exception {
    doTest();
  }

}
