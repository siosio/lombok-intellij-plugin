package de.plushnikov.intellij.plugin.provider;

import com.intellij.codeInsight.daemon.ImplicitUsageProvider;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import de.plushnikov.intellij.plugin.extension.UserMapKeys;

/**
 * Provides implicit usages of lombok fields
 */
public class LombokImplicitUsageProvider implements ImplicitUsageProvider {
  private static final Logger log = Logger.getInstance(LombokImplicitUsageProvider.class.getName());

  @Override
  public boolean isImplicitUsage(PsiElement element) {
    log.info("ImplicitUsage for " + element.toString());
    final Boolean userData = element.getUserData(UserMapKeys.USAGE_KEY);
    return (null != userData && userData) || isImplicitRead(element) || isImplicitWrite(element);
  }

  @Override
  public boolean isImplicitRead(PsiElement element) {
    log.info("ImplicitRead for " + element.toString());
    final Boolean userData = element.getUserData(UserMapKeys.READ_KEY);
    return null != userData && userData;
  }

  @Override
  public boolean isImplicitWrite(PsiElement element) {
    log.info("ImplicitWrite for " + element.toString());
    final Boolean userData = element.getUserData(UserMapKeys.WRITE_KEY);
    return null != userData && userData;
  }
}
