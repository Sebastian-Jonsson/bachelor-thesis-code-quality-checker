package code_quality_analyzer.Rules;

import java.util.ArrayList;
import java.util.List;

import code_quality_analyzer.FileReport;

public class MethodChecker {
    public List<MethodDeclarationViolation> methodDeclarationViolations = new ArrayList<MethodDeclarationViolation>();

    private String validMethodRegex = "(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+)\\([^\\)]*\\) *(\\{?|[^;]) (\\{)(\\})*";
    private String invalidLineMethodRegex = "(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+) *\\([^\\)]*\\)";
    private String invalidSpaceParenthesMethodRegex = "(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+) \\([^\\)]*\\) *(\\{?|[^;]) *(\\{)*(\\})*";
    private String invalidNullBraceMethodRegex = "(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+) *\\([^\\)]*\\) *(\\{?|[^;]) (\\{) +(\\})";

    // Uppercase of classname/interface first 
    private String classOrInterfaceRegex = "(public|protected|private|static|\\s) +(class|interface|\\s) [\\w\\<\\>\\[\\]]+\\ *(\\{?|[^;]) (\\{)*";

    private String methodName = "";
    private String previousLine = "";
    private boolean methodStarted;
    private int methodOpenBrace;
    private int methodEndBrace = 0;
    private int methodLength = 0;
    private int methodCount = 0;

    private boolean classStarted;
    private boolean classEnded;
    private int parentLength = 0;
    private int parentOpenBrace = 0;
    private int parentEndBrace = 0;

    // Rule 6.4
    // https://www.oracle.com/java/technologies/javase/codeconventions-declarations.html#381
    public void methodDeclarationCheck(FileReport report, String line, int lineNumber) {

        /** Assumes the class end brace is on it's own line. */
        if (line.matches(classOrInterfaceRegex) || classStarted) {
            if (line.contains("{")) {
                parentOpenBrace++;
            }
            if (line.contains("}")) {
                parentEndBrace++;
            }
            if (parentOpenBrace == parentEndBrace) {
                classStarted = false;
                appendClassData();
                classEnded = true;
            }
            
            classStarted = true;
            parentLength++;
            methodDeclare(line, lineNumber);
            report.methodDeclarationViolations = methodDeclarationViolations;
        }
        if (classEnded) {
            parentLength++;
        }
    }

    public void methodDeclare(String line, int lineNumber) {
        String trimmedLine = line.trim();

        if (trimmedLine.matches(validMethodRegex)) {
            methodName = trimmedLine;
            lineEndComment(lineNumber);
            methodStarted = true;
        }
        else if (trimmedLine.matches(invalidLineMethodRegex)) {
            MethodDeclarationViolation MDV = new MethodDeclarationViolation();
            methodName = trimmedLine;
            lineEndComment(lineNumber);
            MDV.methodName = methodName;
            MDV.lineNumber = lineNumber;
            MDV.declarationViolation = "First '{' is not on the same line as the declaration statement.";
            methodDeclarationViolations.add(MDV);
            methodStarted = true;
        }
        if (trimmedLine.matches(invalidSpaceParenthesMethodRegex)) {
            MethodDeclarationViolation MDV = new MethodDeclarationViolation();
            MDV.methodName = methodName;
            MDV.lineNumber = lineNumber;
            MDV.declarationViolation = "No space between a method name and the parenthesis '(' starting its parameter list.";
            methodDeclarationViolations.add(MDV);
        }
        if (trimmedLine.matches(invalidNullBraceMethodRegex)) {
            MethodDeclarationViolation MDV = new MethodDeclarationViolation();
            methodName = trimmedLine;
            methodLength = 1;
            methodCount++;
            MDV.methodName = methodName;
            MDV.lineNumber = lineNumber;
            MDV.declarationViolation = "Closing brace '}' starts a line by itself indented to match its corresponding opening statement, except when it is a null statement the '}' should appear immediately after the '{'";
            methodDeclarationViolations.add(MDV);
            appendMethodData(methodName, lineNumber, methodLength);
        }

        if (methodStarted) {
            methodStart(trimmedLine, lineNumber);
        }
        else {
            methodLength = 0;
        }

        previousLine = trimmedLine;
    }

    private void methodStart(String trimmedLine, int lineNumber) {
        methodLength++;

        if (trimmedLine.contains("{")) {
            methodOpenBrace++;
        }
        if (trimmedLine.contains("}")) {
            methodEndBrace++;

            if (trimmedLine.length() > 1 && !trimmedLine.matches(validMethodRegex)) {
                MethodDeclarationViolation MDV = new MethodDeclarationViolation();
                MDV.methodName = methodName;
                MDV.lineNumber = lineNumber;
                MDV.declarationViolation = "Closing brace '}' starts a line by itself indented to match its corresponding opening statement, except when it is a null statement the '}' should appear immediately after the '{'";
                methodDeclarationViolations.add(MDV);;
            }
            if (methodEnd()) {
                appendMethodData(methodName, lineNumber, methodLength);
                methodStarted = false;
                methodCount++;
            }
        }
    }

    private void appendMethodData(String methodName, int lineNumber, int methodLength) {

        for (MethodDeclarationViolation declarationViolation : methodDeclarationViolations) {
            if (declarationViolation.methodName == methodName) {
                declarationViolation.methodLength = methodLength;
            }
        }
    }

    private void appendClassData() {

        for (MethodDeclarationViolation declarationViolation : methodDeclarationViolations) {
            declarationViolation.parentTotalMethods = methodCount;
            declarationViolation.parentLength = parentLength;
        }
    }
    
    private boolean methodEnd() {
        return methodOpenBrace == methodEndBrace;
    }

    private void lineEndComment(int lineNumber) {
        MethodDeclarationViolation MDV = new MethodDeclarationViolation();

        if (previousLine.length() > 0) {
            if (previousLine.contains("//") || previousLine.contains("*/")) {
                // Java refuses the does not contain statements, adding else works.
            }
            else {
                MDV.methodName = methodName;
                MDV.lineNumber = lineNumber - 1;
                MDV.declarationViolation = "Methods are separated by a blank line."; 
                methodDeclarationViolations.add(MDV);
            }
        }
    }

    /** Existing in Classes or Interfaces */
    public class MethodDeclarationViolation {
        public int parentLength = 0;
        public int parentTotalMethods = 0;
        public String methodName = "";
        public int methodLength = 0;
        public int lineNumber = 0;
        public String declarationViolation = "";
    }

}
