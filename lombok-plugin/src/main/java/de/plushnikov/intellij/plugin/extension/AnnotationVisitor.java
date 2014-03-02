package de.plushnikov.intellij.plugin.extension;

import com.intellij.psi.JavaRecursiveElementWalkingVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnonymousClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceExpression;
import de.plushnikov.intellij.plugin.extension.key.AnnotationProcessorKey;
import de.plushnikov.intellij.plugin.extension.key.ProcessorKey;
import de.plushnikov.intellij.plugin.processor.AbstractProcessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

final class AnnotationVisitor<PSI extends PsiElement> extends JavaRecursiveElementWalkingVisitor {
  private final List<PSI> newPsiElements = new ArrayList<PSI>();

  private final Map<ProcessorKey, Collection<AbstractProcessor>> processorMap;

  public AnnotationVisitor(final Map<ProcessorKey, Collection<AbstractProcessor>> processorMap) {
    this.processorMap = processorMap;
  }

  public List<PSI> getNewPsiElements() {
    return newPsiElements;
  }

  @Override
  public void visitAnnotation(final PsiAnnotation annotation) {
    final AnnotationProcessorKey annotationProcessorKey = new AnnotationProcessorKey(annotation);
    final Collection<AbstractProcessor> processors = processorMap.get(annotationProcessorKey);
    if (null != processors) {
      for (AbstractProcessor processor : processors) {
        newPsiElements.addAll((Collection<? extends PSI>) processor.process(annotation));
      }
    }
  }

  @Override
  public void visitAnonymousClass(PsiAnonymousClass psiClass) {

  }

  @Override
  public void visitReferenceExpression(PsiReferenceExpression expression) {
  }

  @Override
  public void visitCodeBlock(PsiCodeBlock block) {

  }
}
