package cz.pedry.cucumber.kotlin

import com.intellij.openapi.module.Module
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.plugins.cucumber.BDDFrameworkType
import org.jetbrains.plugins.cucumber.psi.GherkinFile
import org.jetbrains.plugins.cucumber.steps.AbstractCucumberExtension
import org.jetbrains.plugins.cucumber.steps.AbstractStepDefinition
import com.intellij.psi.search.UsageSearchContext
import com.intellij.psi.search.PsiSearchHelper
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.idea.references.KtInvokeFunctionReference
import java.util.ArrayList

class KotlinCucumberExtension: AbstractCucumberExtension() {

    override fun getStepDefinitionCreator() = throw UnsupportedOperationException("Step generation not supported")

    override fun isStepLikeFile(child: PsiElement, parent: PsiElement) = child is KtFile

    override fun isWritableStepLikeFile(child: PsiElement, parent: PsiElement) = isStepLikeFile(child, parent)

    override fun getStepFileType() = BDDFrameworkType(KotlinFileType.INSTANCE)

    override fun getGlues(file: GherkinFile, jGluesFromOtherFiles: MutableSet<String>?) = emptyList<String>()

    override fun getStepDefinitionContainers(featureFile: GherkinFile): MutableCollection<out PsiFile> = mutableListOf()

    override fun loadStepsFor(featureFile: PsiFile?, module: Module): MutableList<AbstractStepDefinition> {
        val result = ArrayList<AbstractStepDefinition>()
        val dependenciesScope = module.getModuleWithDependenciesAndLibrariesScope(true)
        val kotlinFiles = GlobalSearchScope.getScopeRestrictedByFileTypes(dependenciesScope, KotlinFileType.INSTANCE)
        for (method in arrayOf("Given", "And", "Then", "But", "When")) {
            PsiSearchHelper.SERVICE.getInstance(module.project).processElementsWithWord({element, offsetInElement ->
                val parent = element.parent
                if (parent != null) {
                    val references = parent.references
                    for (ref in references) {
                        if (ref is KtInvokeFunctionReference) {
                            result.add(KotlinStepDefinition(parent, ref))
                            break
                        }
                    }
                }
                true
            }, kotlinFiles, method, UsageSearchContext.IN_CODE, true)
        }
        return result
    }

}