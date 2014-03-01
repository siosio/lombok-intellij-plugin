package de.plushnikov.intellij.plugin.agent.handler;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * @author VISTALL
 * @since 23:44/30.03.13
 */
public interface ExtraExceptionHandler {
  ExtensionPointName<ExtraExceptionHandler> EP_NAME = ExtensionPointName.create("lombok.intellij.plugin.extensions.extraExceptionHandler");

  boolean isHandled(@NotNull PsiClassType type, @NotNull PsiElement element, PsiElement topElement);
}
