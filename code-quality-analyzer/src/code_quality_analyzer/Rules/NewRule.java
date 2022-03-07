package code_quality_analyzer.Rules;

import code_quality_analyzer.*;

public class NewRule {
    RulesConfig rule = new RulesConfig();

    public void newRuleTemplate(FileReport report, String line) {
        // Fill in logic to check line for violation and add result to report.
    }
}

// If is not method or class blank spaces should be around parentheses (word) comas between params in argumentlist (var1, var2)
// https://www.oracle.com/java/technologies/javase/codeconventions-whitespace.html#682
