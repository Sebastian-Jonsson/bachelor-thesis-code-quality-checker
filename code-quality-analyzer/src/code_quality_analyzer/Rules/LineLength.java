package code_quality_analyzer.Rules;

import code_quality_analyzer.*;

/**
 * Rule 4.1
 * https://www.oracle.com/java/technologies/javase/codeconventions-indentation.html#313
 */
public class LineLength {
    RulesConfig rule = new RulesConfig();
    
    public void maxLineLength(FileReport report, String line, int lineNumber) {
        if (line.length() > rule.MAX_LINE_LENGTH) {
            LineLengthViolation LLV = new LineLengthViolation();
            LLV.lineNumber = lineNumber;
            LLV.actualLength = line.length();
            report.lineLengthViolations.add(LLV);
        }
    }

    public class LineLengthViolation {
        public int lineNumber = 0;
        public int actualLength = 0;
    }
}


