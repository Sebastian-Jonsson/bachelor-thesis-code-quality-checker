package code_quality_analyzer.Rules;

import code_quality_analyzer.FileReport;

public class MethodChecker {
    private String validMethodRegex = "(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+)\\([^\\)]*\\) *(\\{?|[^;]) (\\{)(\\})*";
    private String invalidLineMethodRegex = "(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+) *\\([^\\)]*\\)";
    private String invalidSpaceParenthesMethodRegex = "(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+) \\([^\\)]*\\) *(\\{?|[^;]) *(\\{)*(\\})*";
    private String invalidNullBraceMethodRegex = "(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+) *\\([^\\)]*\\) *(\\{?|[^;]) (\\{) +(\\})";

    // Uppercase of classname/interface first 
    private String classOrInterfaceRegex = "(public|protected|private|static|\\s) +(class|interface|\\s) [\\w\\<\\>\\[\\]]+\\ *(\\{?|[^;]) (\\{)*";

    private String methodName = ""; /** For use in identifying the method where errors occured. */
    private String previousLine = "";
    private boolean methodStarted;
    private int methodOpenBrace;
    private int methodEndBrace = 0;
    private int methodLength = 0;
    private int methodCount = 0;

    private boolean classStarted;
    private int parentLength = 0;
    private int parentOpenBrace = 0;
    private int parentEndBrace = 0;

    // Rule 6.4
    // https://www.oracle.com/java/technologies/javase/codeconventions-declarations.html#381
    public void methodDeclarationCheck(FileReport report, String line, int lineNumber) {

        if (line.matches(classOrInterfaceRegex) || classStarted) {
            // System.out.println("sup");
            if (line.contains("{")) {
                parentOpenBrace++;
            }
            if (line.contains("}")) {
                parentEndBrace++;
            }
            if (parentOpenBrace == parentEndBrace) {
                classStarted = false;
                System.out.println(lineNumber);
            }
            
            classStarted = true;
            parentLength++;
            methodDeclare(report, line, lineNumber);
        }
    }

    public void methodDeclare(FileReport report, String line, int lineNumber) {
        String trimmedLine = line.trim();
        // TODO: MDV into FileReport proper.
        MethodDeclarationViolation MDV = new MethodDeclarationViolation();
        
        if (trimmedLine.matches(validMethodRegex)) {
            methodName = trimmedLine;
            lineEndComment(report, MDV, lineNumber);
            methodStarted = true;
        }
        else if (trimmedLine.matches(invalidLineMethodRegex)) {
            methodName = trimmedLine;
            lineEndComment(report, MDV, lineNumber);
            MDV.methodName = methodName;
            MDV.lineNumber = lineNumber;
            MDV.declarationViolation = "First '{' is not on the same line as the declaration statement.";
            report.methodDeclarationViolations.add(MDV);
            testLogger(MDV);
            methodStarted = true;
        }
        if (trimmedLine.matches(invalidSpaceParenthesMethodRegex)) {
            MDV.methodName = methodName;
            MDV.lineNumber = lineNumber;
            MDV.declarationViolation = "No space between a method name and the parenthesis '(' starting its parameter list.";
            report.methodDeclarationViolations.add(MDV);
            testLogger(MDV);
        }
        if (trimmedLine.matches(invalidNullBraceMethodRegex)) {
            methodName = trimmedLine;
            methodLength = 1;
            methodCount++;
            MDV.methodName = methodName;
            MDV.lineNumber = lineNumber;
            MDV.declarationViolation = "Closing brace '}' starts a line by itself indented to match its corresponding opening statement, except when it is a null statement the '}' should appear immediately after the '{'";
            report.methodDeclarationViolations.add(MDV);
            testLogger(MDV);
        }

        if (methodStarted) {
            methodStart(report, MDV, trimmedLine, lineNumber);
        }
        else {
            methodLength = 0;
        }
        previousLine = trimmedLine;
    }

    private void methodStart(FileReport report, MethodDeclarationViolation MDV, String trimmedLine, int lineNumber) {
        methodLength++;

        if (trimmedLine.contains("{")) {
            methodOpenBrace++;
        }
        if (trimmedLine.contains("}")) {
            methodEndBrace++;

            if (trimmedLine.length() > 1 && !trimmedLine.matches(validMethodRegex)) {
                MDV.methodName = methodName;
                MDV.lineNumber = lineNumber;
                MDV.declarationViolation = "Closing brace '}' starts a line by itself indented to match its corresponding opening statement, except when it is a null statement the '}' should appear immediately after the '{'";
                report.methodDeclarationViolations.add(MDV);
                testLogger(MDV);
            }
            if (methodEnd()) {
                methodStarted = false;
                methodCount++;
            }
        }
    }

    private void testLogger(MethodDeclarationViolation MDV) {
        System.out.println("\nMethod Name: " + MDV.methodName + " | Line: " + MDV.lineNumber + " | Violation: " + MDV.declarationViolation);
    }
    
    private boolean methodEnd() {
        return methodOpenBrace == methodEndBrace;
    }

    private void lineEndComment(FileReport report, MethodDeclarationViolation MDV, int lineNumber) {
        if (previousLine.length() > 0) {
            if (previousLine.contains("//") || previousLine.contains("*/")) {
                // Java refuses the does not contain statements, adding else works.
            }
            else {
                MDV.methodName = methodName;
                MDV.lineNumber = lineNumber - 1;
                MDV.declarationViolation = "Methods are separated by a blank line."; 
                report.methodDeclarationViolations.add(MDV);
                testLogger(MDV);
            }
        }
    }

    /** Existing in Classes or Interfaces */
    public class MethodDeclarationViolation {
        public int parentLength = 0;
        public String methodName = "";
        public int lineNumber = 0;
        public String declarationViolation = "";
    }

}
