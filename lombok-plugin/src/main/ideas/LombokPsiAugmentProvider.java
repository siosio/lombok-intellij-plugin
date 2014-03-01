package lombok.idea;

import com.intellij.psi.JavaRecursiveElementWalkingVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.augment.PsiAugmentProvider;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An augment provider for Project Lombok annotations
 */
public final class LombokPsiAugmentProvider extends PsiAugmentProvider {
	private static final HandlerLibrary HANDLERS = HandlerLibrary.load();

	@NotNull
	@Override
	public <PSI extends PsiElement> List<PSI> getAugments(final @NotNull PsiElement element, final @NotNull Class<PSI> type) {
		List<PSI> newPsiElements = Collections.emptyList();
		if ((element instanceof PsiClass) && isSubclassOfAnyOfThese(type, PsiMethod.class, PsiField.class, PsiLocalVariable.class, PsiClass.class)) {
			final AnnotationAugmentingVisitor<PSI> visitor = new AnnotationAugmentingVisitor<PSI>(type);
			element.accept(visitor);
			newPsiElements = visitor.getNewPsiElements();
		}
		return newPsiElements;
	}

	private boolean isSubclassOfAnyOfThese(final Class<?> type, final Class<?>... potentialSuperClasses) {
		for (Class<?> potentialSuperClass : potentialSuperClasses) {
			if (potentialSuperClass.isAssignableFrom(type)) {
				return true;
			}
		}
		return false;
	}

	private static final class AnnotationAugmentingVisitor<PSI extends PsiElement> extends JavaRecursiveElementWalkingVisitor {
		private final List<PSI> newPsiElements = new ArrayList<PSI>();
		private final Class<PSI> type;

		public AnnotationAugmentingVisitor(final Class<PSI> type) {
			this.type = type;
		}

		@Override
		public void visitAnnotation(final PsiAnnotation annotation) {
			newPsiElements.addAll(HANDLERS.handle(annotation, type));
		}

		public List<PSI> getNewPsiElements() {
			return newPsiElements;
		}
	}
}
