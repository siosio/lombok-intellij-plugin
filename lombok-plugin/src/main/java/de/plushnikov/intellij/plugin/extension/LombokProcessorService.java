package de.plushnikov.intellij.plugin.extension;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiNameIdentifierOwner;
import de.plushnikov.intellij.plugin.extension.key.AnnotationProcessorKey;
import de.plushnikov.intellij.plugin.extension.key.ProcessorKey;
import de.plushnikov.intellij.plugin.extension.key.SimpleProcessorKey;
import de.plushnikov.intellij.plugin.processor.Processor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LombokProcessorService {

  @SuppressWarnings("unchecked")
  private final List<? extends Class<? extends PsiNameIdentifierOwner>> PROCESSABLE_PSI_ELEMENTS = Arrays.asList(PsiClass.class, PsiMethod.class, PsiField.class);

  private final Map<Class<? extends PsiElement>, Map<ProcessorKey, Processor>> producedClassProcessorMap;

  public LombokProcessorService() {
    producedClassProcessorMap = new HashMap<Class<? extends PsiElement>, Map<ProcessorKey, Processor>>();
    for (Class<? extends PsiElement> aClass : PROCESSABLE_PSI_ELEMENTS) {
      producedClassProcessorMap.put(aClass, new HashMap<ProcessorKey, Processor>());
    }
  }

  public void init() {
    for (Processor processor : LombokProcessorExtensionPoint.EP_NAME.getExtensions()) {
//      processor.isEnabled()
      for (Class<? extends PsiElement> processableElement : PROCESSABLE_PSI_ELEMENTS) {
        if (processor.canProduce(processableElement)) {
          producedClassProcessorMap.get(processableElement).put(new SimpleProcessorKey(processor.getSupportedAnnotation()), processor);
        }
      }
    }
  }

  public <Psi extends PsiElement> List<Psi> processFor(@NotNull PsiClass psiClass, @NotNull Class<Psi> type) {
    final List<Psi> emptyResult = Collections.emptyList();

    PsiModifierList psiClassModifierList = psiClass.getModifierList();
    PsiAnnotation[] annotations = psiClassModifierList.getAnnotations();
    for (PsiAnnotation annotation : annotations) {
      Processor processor = producedClassProcessorMap.get(type).get(new AnnotationProcessorKey(annotation));
      if (null != processor) {
        processor.process(psiClass);
      }
    }

    return emptyResult;
  }


}
