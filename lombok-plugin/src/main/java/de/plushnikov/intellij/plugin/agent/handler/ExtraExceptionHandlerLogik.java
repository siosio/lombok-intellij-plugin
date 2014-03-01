package de.plushnikov.intellij.plugin.agent.handler;

import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;

/**
 * Date: 11.08.13 Time: 15:11
 */
public class ExtraExceptionHandlerLogik {
  static {
    System.out.println("STATIC INIT!");
  }

  public static boolean wrapReturnValue(boolean originalHandled, PsiElement element, PsiClassType exceptionType, PsiElement topElement) {
    System.out.println("wrapReturnValue Called!!!!");
    if (originalHandled) {
      return originalHandled;
    }

    if (element == null || element.getParent() == topElement || element.getParent() == null) {
      return false;
    }

    return new SneakyTrowsExceptionHandler().isHandled(exceptionType, element, topElement);

//    for (ExtraExceptionHandler handler : Extensions.getExtensions(ExtraExceptionHandler.EP_NAME)) {
//      if (handler.isHandled(exceptionType, element, topElement)) {
//        return true;
//      }
//    }
//    return false;
  }
//
//  public static boolean decisionMethod(PsiElement element, PsiClassType exceptionType, PsiElement topElement) {
//    System.out.println("decisionMethod Called!!!!");
//
//    if (element == null || element.getParent() == topElement || element.getParent() == null) {
//      return false;
//    }
//
//    for (ExtraExceptionHandler handler : ExtraExceptionHandler.EP_NAME.getExtensions()) {
//      if (handler.isHandled(exceptionType, element, topElement)) {
//        return true;
//      }
//    }
//    return false;
//  }
//
//  public static boolean valueMethod(PsiElement element, PsiClassType exceptionType, PsiElement topElement) {
//    System.out.println("valueMethod Called!!!!");
//
//    return true;
//  }

}
