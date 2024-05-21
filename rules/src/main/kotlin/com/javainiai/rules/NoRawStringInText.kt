package com.javainiai.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression

class NoRawStringInText(config: Config) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        severity = Severity.CodeSmell,
        "Text composables shouldn't use a raw string. Move them to resources and use stringResource() to load them instead. This allows easier code maintenance and translation to other languages.",
        Debt.FIVE_MINS
    )

    val pattern = Regex("(text( )?=( )?)?\".+\"\$")

    override fun visitCallExpression(expression: KtCallExpression) {
        if (expression.calleeExpression?.text == "Text") {
            val argument = expression.valueArguments.firstOrNull()
            if (argument != null && pattern.matches(argument.text)) {
                report(
                    CodeSmell(
                        issue,
                        Entity.from(expression),
                        message = "Don't use raw strings in Text() composables. Found: ${argument.text}"
                    )
                )
            }
        }

        super.visitCallExpression(expression)
    }

}