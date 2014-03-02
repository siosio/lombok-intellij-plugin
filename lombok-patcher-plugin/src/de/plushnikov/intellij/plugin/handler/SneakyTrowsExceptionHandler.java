package de.plushnikov.intellij.plugin.handler;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiArrayInitializerMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassObjectAccessExpression;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SneakyTrowsExceptionHandler {
  public static boolean wrapReturnValue(boolean originalHandled, PsiElement element, PsiClassType exceptionType, PsiElement topElement) {
    if (originalHandled) {
      return originalHandled;
    }

    if (element == null || element.getParent() == topElement || element.getParent() == null) {
      return false;
    }

    return new SneakyTrowsExceptionHandler().isHandled(exceptionType, element, topElement);
  }


  protected boolean isHandled(@NotNull PsiClassType type, @NotNull PsiElement element, PsiElement topElement) {
    final PsiMethod parent = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
    if (parent == null) {
      return false;
    }

    final PsiAnnotation psiAnnotation = AnnotationUtil.findAnnotation(parent, "lombok.SneakyThrows");
    if (psiAnnotation == null) {
      return false;
    }

//    Collection<PsiType> sneakedExceptionTypes = PsiAnnotationUtil.getAnnotationValues(psiAnnotation, PsiAnnotation.DEFAULT_REFERENCED_METHOD_NAME, PsiType.class);

    final PsiAnnotationMemberValue attributeValue = psiAnnotation.findAttributeValue(PsiAnnotation.DEFAULT_REFERENCED_METHOD_NAME);
    if (attributeValue == null) {
      return false;
    }

    List<PsiClass> classes = new ArrayList<PsiClass>();
    collectClassesFromAnnotationValue(attributeValue, classes);
    if (classes.isEmpty()) {
      return true;
    } else {
      PsiClass target = type.resolve();
      if (target == null) {
        return false;
      }

      for (PsiClass psiClass : classes) {
        if (psiClass == target || target.isInheritor(psiClass, true)) {
          return true;
        }
      }
    }
    return false;
  }

  private static void collectClassesFromAnnotationValue(@NotNull PsiAnnotationMemberValue value, List<PsiClass> classes) {
    if (value instanceof PsiArrayInitializerMemberValue) {
      final PsiAnnotationMemberValue[] initializers = ((PsiArrayInitializerMemberValue) value).getInitializers();
      for (PsiAnnotationMemberValue initializer : initializers) {
        collectClassesFromAnnotationValue(initializer, classes);
      }
    } else if (value instanceof PsiClassObjectAccessExpression) {
      final PsiTypeElement operand = ((PsiClassObjectAccessExpression) value).getOperand();
      final PsiType type = operand.getType();
      if (type instanceof PsiClassType) {
        final PsiClass resolve = ((PsiClassType) type).resolve();
        if (resolve != null) {
          classes.add(resolve);
        }
      }
    }
  }
}
