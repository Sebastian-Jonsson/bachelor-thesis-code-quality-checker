package code_quality_analyzer.Rules;

import code_quality_analyzer.*;

public class RulesOrganizer {
    private TypeOfLine ToL = new TypeOfLine();
    private LineLength LL = new LineLength();
    private MethodChecker MC = new MethodChecker();

    public void rulesChecker(FileReport report, String line, int lineNumber) {
        ToL.lineTypeCount(report, line);
        LL.maxLineLength(report, line, lineNumber);
        MC.methodDeclarationCheck(report, line, lineNumber);
    }

}
