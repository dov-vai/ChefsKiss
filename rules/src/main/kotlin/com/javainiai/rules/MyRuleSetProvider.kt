package com.javainiai.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

class MyRuleSetProvider : RuleSetProvider {
    override val ruleSetId: String = "ChefsKissRuleSet"

    override fun instance(config: Config): RuleSet {
        return RuleSet(
            ruleSetId,
            listOf(
                NoRawStringInText(config)
            ),
        )
    }
}