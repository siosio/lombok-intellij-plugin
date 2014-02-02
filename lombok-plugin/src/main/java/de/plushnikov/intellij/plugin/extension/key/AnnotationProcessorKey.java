package de.plushnikov.intellij.plugin.extension.key;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import org.jetbrains.annotations.NotNull;

public class AnnotationProcessorKey extends ProcessorKey {
  private final PsiAnnotation psiAnnotation;
  private String name;
  private String fqName;

  public AnnotationProcessorKey(@NotNull PsiAnnotation psiAnnotation) {
    this.psiAnnotation = psiAnnotation;

    final PsiJavaCodeReferenceElement referenceElement = psiAnnotation.getNameReferenceElement();
    if (null != referenceElement) {
      this.name = referenceElement.getReferenceName();
    } else {
      this.name = "";
    }
  }

  @Override
  protected String getName() {
    return name;
  }

  @Override
  protected String getQualifiedName() {
    if (null == fqName) {
      fqName = psiAnnotation.getQualifiedName();
    }
    return fqName;
  }
}
