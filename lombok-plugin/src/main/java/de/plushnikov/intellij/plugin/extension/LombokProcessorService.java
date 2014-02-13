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
import de.plushnikov.intellij.plugin.processor.clazz.AbstractClassProcessor;
import de.plushnikov.intellij.plugin.processor.field.AbstractFieldProcessor;
import de.plushnikov.intellij.plugin.processor.method.AbstractMethodProcessor;
import de.plushnikov.intellij.plugin.util.PsiClassUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LombokProcessorService {

  @SuppressWarnings("unchecked")
  private final List<? extends Class<? extends PsiNameIdentifierOwner>> PROCESSABLE_PSI_ELEMENTS = Arrays.asList(PsiClass.class, PsiMethod.class, PsiField.class);

  private final Map<Class<? extends PsiElement>, Map<ProcessorKey, AbstractClassProcessor>> producedClassProcessorMap;
  private final Map<Class<? extends PsiElement>, Map<ProcessorKey, AbstractFieldProcessor>> producedFieldProcessorMap;
  private final Map<Class<? extends PsiElement>, Map<ProcessorKey, AbstractMethodProcessor>> producedMethodProcessorMap;

  public LombokProcessorService() {
    producedClassProcessorMap = new HashMap<Class<? extends PsiElement>, Map<ProcessorKey, AbstractClassProcessor>>();
    producedFieldProcessorMap = new HashMap<Class<? extends PsiElement>, Map<ProcessorKey, AbstractFieldProcessor>>();
    producedMethodProcessorMap = new HashMap<Class<? extends PsiElement>, Map<ProcessorKey, AbstractMethodProcessor>>();
    for (Class<? extends PsiElement> aClass : PROCESSABLE_PSI_ELEMENTS) {
      producedClassProcessorMap.put(aClass, new HashMap<ProcessorKey, AbstractClassProcessor>());
      producedFieldProcessorMap.put(aClass, new HashMap<ProcessorKey, AbstractFieldProcessor>());
      producedMethodProcessorMap.put(aClass, new HashMap<ProcessorKey, AbstractMethodProcessor>());
    }
  }

  public void init() {
    for (Processor processor : LombokProcessorExtensionPoint.EP_NAME.getExtensions()) {
//      processor.isEnabled()
      for (Class<? extends PsiElement> processableElement : PROCESSABLE_PSI_ELEMENTS) {
        if (processor.canProduce(processableElement)) {
          if (processor instanceof AbstractClassProcessor) {
            producedClassProcessorMap.get(processableElement).put(new SimpleProcessorKey(processor.getSupportedAnnotation()), (AbstractClassProcessor) processor);
          } else if (processor instanceof AbstractFieldProcessor) {
            producedFieldProcessorMap.get(processableElement).put(new SimpleProcessorKey(processor.getSupportedAnnotation()), (AbstractFieldProcessor) processor);
          } else if (processor instanceof AbstractMethodProcessor) {
            producedMethodProcessorMap.get(processableElement).put(new SimpleProcessorKey(processor.getSupportedAnnotation()), (AbstractMethodProcessor) processor);
          }
        }
      }
    }
  }

  public <Psi extends PsiElement> List<Psi> processFor(@NotNull PsiClass psiClass, @NotNull Class<Psi> type) {
    final List<Psi> result = new ArrayList<Psi>();

    processClassAnnotations(psiClass, producedClassProcessorMap.get(type), result);
    processFieldAnnotations(psiClass, producedFieldProcessorMap.get(type), result);
    processMethodAnnotations(psiClass, producedMethodProcessorMap.get(type), result);

    return result;
  }

  private <Psi extends PsiElement> void processClassAnnotations(@NotNull PsiClass psiClass, @NotNull Map<ProcessorKey, AbstractClassProcessor> processorMap, @NotNull List<Psi> result) {
    if (processorMap.isEmpty()) {
      return;
    }

    PsiModifierList psiModifierList = psiClass.getModifierList();
    if (null != psiModifierList) {
      PsiAnnotation[] annotations = psiModifierList.getAnnotations();
      for (PsiAnnotation annotation : annotations) {
        AbstractClassProcessor processor = processorMap.get(new AnnotationProcessorKey(annotation));
        if (null != processor) {
          processor.process(psiClass, annotation, (List) result);
        }
      }
    }
  }

  private <Psi extends PsiElement> void processFieldAnnotations(@NotNull PsiClass psiClass, @NotNull Map<ProcessorKey, AbstractFieldProcessor> processorMap, @NotNull List<Psi> result) {
    if (processorMap.isEmpty()) {
      return;
    }

    final Collection<PsiField> psiFields = PsiClassUtil.collectClassFieldsIntern(psiClass);
    for (PsiField psiField : psiFields) {
      PsiModifierList psiModifierList = psiField.getModifierList();
      if (null != psiModifierList) {
        PsiAnnotation[] annotations = psiModifierList.getAnnotations();
        for (PsiAnnotation annotation : annotations) {
          AbstractFieldProcessor processor = processorMap.get(new AnnotationProcessorKey(annotation));
          if (null != processor) {
            processor.process(psiField, annotation, (List) result);
          }
        }
      }
    }
  }

  private <Psi extends PsiElement> void processMethodAnnotations(@NotNull PsiClass psiClass, @NotNull Map<ProcessorKey, AbstractMethodProcessor> processorMap, @NotNull List<Psi> result) {
    if (processorMap.isEmpty()) {
      return;
    }

    final Collection<PsiMethod> psiMethods = PsiClassUtil.collectClassMethodsIntern(psiClass);
    for (PsiMethod psiMethod : psiMethods) {
      PsiModifierList psiModifierList = psiMethod.getModifierList();
      if (null != psiModifierList) {
        PsiAnnotation[] annotations = psiModifierList.getAnnotations();
        for (PsiAnnotation annotation : annotations) {
          AbstractMethodProcessor processor = processorMap.get(new AnnotationProcessorKey(annotation));
          if (null != processor) {
            processor.process(psiMethod, annotation, (List) result);
          }
        }
      }
    }
  }


}
