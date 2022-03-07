package code_quality_analyzer.Rules;

import code_quality_analyzer.FileReport;

public class MethodChecker {
    String validMethodRegex = "(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+)\\([^\\)]*\\) *(\\{?|[^;]) (\\{)(\\})*";
    // Example: public void methodName()
    String invalidLineMethodRegex = "(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+) *\\([^\\)]*\\)";
    String invalidSpaceParenthesMethodRegex = "(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+) \\([^\\)]*\\) *(\\{?|[^;]) *(\\{)*(\\})*";
    String invalidNullBraceMethodRegex = "(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+) *\\([^\\)]*\\) *(\\{?|[^;]) (\\{) +(\\})";
    private String methodName = "";
    private String previousLine = "";
    private boolean methodStarted;
    private int methodOpenBrace;
    private int methodEndBrace = 0;
    private int methodLength = 0;
    private int methodCount = 0;

    private boolean classStarted;
    private boolean interfaceStarted;
    private int parentLength = 0;

    // Rule 6.4 can help
    // https://www.oracle.com/java/technologies/javase/codeconventions-declarations.html#381
    // A method has 2-5 words with one space between all, at the end of the third word it should have a parenthesis public void method() and then count the amount of { to see how much internal content it has and should end at the time the } appears the same amount of times as {.
    public void methodDeclarationCheck(FileReport report, String line, int lineNumber) {
        classStarted = true;
        interfaceStarted = true;
        if (classStarted || interfaceStarted) {
            parentLength++;
            methodDeclare(report, line, lineNumber);
            // methodStart(report, line, lineNumber);
            // System.out.println(lineNumber + ": " + methodOpenBrace + " " +  methodEndBrace);
            // System.out.println(methodLength);
            
        }
    }

    public void methodDeclare(FileReport report, String line, int lineNumber) {
        String trimmedLine = line.trim();
        MethodDeclarationViolation MDV = new MethodDeclarationViolation();
        
        if (trimmedLine.matches(validMethodRegex)) {
            methodName = trimmedLine;
            methodStarted = true;
        }
        else if (trimmedLine.matches(invalidLineMethodRegex)) {
            MDV.lineNumber = lineNumber;
            MDV.declarationViolation = "First '{' is not on the same line as the declaration statement.";
            methodName = trimmedLine;
            methodStarted = true;
        }
        if (trimmedLine.matches(invalidSpaceParenthesMethodRegex)) {
            MDV.lineNumber = lineNumber;
            MDV.declarationViolation = "No space between a method name and the parenthesis '(' starting its parameter list.";
        }
        if (trimmedLine.matches(invalidNullBraceMethodRegex)) {
            methodLength = 1;
            methodCount++;
            MDV.lineNumber = lineNumber;
            MDV.declarationViolation = "Closing brace '}' starts a line by itself indented to match its corresponding opening statement, except when it is a null statement the '}' should appear immediately after the '{'";
            methodName = trimmedLine;
        }

        if (methodStarted) {
            methodLength++;

            if (trimmedLine.contains("{")) {
                methodOpenBrace++;
            }
            if (trimmedLine.contains("}")) {
                methodEndBrace++;

                if (trimmedLine.length() > 1 && !trimmedLine.matches(validMethodRegex)) {
                    MDV.lineNumber = lineNumber;
                    MDV.declarationViolation = "Closing brace '}' starts a line by itself indented to match its corresponding opening statement, except when it is a null statement the '}' should appear immediately after the '{'";
                }
                if (methodEnd()) {
                    methodStarted = false;
                    methodCount++;
                }
            }
        }
        else {
            methodLength = 0;
        }

    }
    
    // Covers rule 6.4.3
    private boolean methodEnd() {
        return methodOpenBrace == methodEndBrace;
    }

    private boolean isClass(String trimmedLine) {
        if (trimmedLine.contains(" class ")) {
            classStarted = true;
        }
        return trimmedLine.contains(" class ");
    }

    public class MethodDeclarationViolation {
        public int lineNumber = 0;
        public String declarationViolation = "";
    }

}
