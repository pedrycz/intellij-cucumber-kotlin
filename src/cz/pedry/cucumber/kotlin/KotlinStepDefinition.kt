package cz.pedry.cucumber.kotlin

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.references.KtInvokeFunctionReference
import org.jetbrains.plugins.cucumber.steps.AbstractStepDefinition

class KotlinStepDefinition(method: PsiElement, val ref: KtInvokeFunctionReference) : AbstractStepDefinition(method) {

    override fun getVariableNames(): MutableList<String> = mutableListOf()

    override fun getCucumberRegexFromElement(element: PsiElement?): String? {
        if (ref is KtInvokeFunctionReference) {
            return ref.value.substring(ref.value.indexOf('^'), ref.value.indexOf('$') + 1).replace("\\\\", "\\")
        }
        return null
    }

}