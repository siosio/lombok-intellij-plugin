/*
 * Copyright 2013 Consulo.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.plushnikov.intellij.plugin.agent.handler;

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

/**
 * @author VISTALL
 * @since 23:49/30.03.13
 */
public class SneakyTrowsExceptionHandler implements ExtraExceptionHandler {
  @Override
  public boolean isHandled(@NotNull PsiClassType type, @NotNull PsiElement element, PsiElement topElement) {
//    if (!ProjectSettings.loadAndGetEnabledInProject(element.getProject())) {
//      return emptyResult;
//    }

    System.out.println("Called for " + element);

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
