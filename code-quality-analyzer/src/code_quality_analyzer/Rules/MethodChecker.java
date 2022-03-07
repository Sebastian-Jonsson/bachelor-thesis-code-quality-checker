package code_quality_analyzer.Rules;

import code_quality_analyzer.FileReport;

public class MethodChecker {
    String validMethodRegex = "(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+)\\([^\\)]*\\) *(\\{?|[^;]) (\\{)(\\})*";
    // Example: public void methodName()
    String invalidLineMethodRegex = "(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+) *\\([^\\)]*\\)";
    String invalidSpaceParenthesMethodRegex = "(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+) \\([^\\)]*\\) *(\\{?|[^;]) *(\\{)*(\\})*";
    String invalidNullBraceMethodRegex = "(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+) *\\([^\\)]*\\) *(\\{?|[^;]) (\\{) +(\\})";
    private boolean methodStarted;
    private String methodString = "";
    private String methodStartString = "";
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
        if ((trimmedLine.contains("{") || trimmedLine.length() < 1) && methodString.matches(invalidLineMethodRegex)) {
            methodStarted = true;
            if (trimmedLine.contains("}")) {
                methodCount++;
                methodLength++;
                methodStarted = false;
                System.out.println("Hej " +lineNumber + ": Method Length: " + methodLength + "| Method Count: " + methodCount);
            }
        }
        // Add rule 6.4.4 6.4.1
        if (trimmedLine.matches(validMethodRegex)) {
            methodStarted = true;
            methodString = trimmedLine;
        }
        // Covers rule 6.4.2
        else if (trimmedLine.matches(invalidLineMethodRegex)) {
            MDV.lineNumber = lineNumber;
            MDV.declarationViolation = "First '{' is not on the same line as the declaration statement.";
            methodString = trimmedLine;
            System.out.println("Line: " + lineNumber + ": { not on the same line as declaration.");
        }
        // Covers rule 6.4.1
        else if (trimmedLine.matches(invalidSpaceParenthesMethodRegex)) {
            MDV.lineNumber = lineNumber;
            MDV.declarationViolation = "No space between a method name and the parenthesis '(' starting its parameter list.";
            methodString = trimmedLine;
            System.out.println("Line: " + lineNumber + ": space issue parenthesis.");
        }
        // Makes method length count not work at all.
        if (trimmedLine.matches(invalidNullBraceMethodRegex)) {
            methodStarted = true;
            if (trimmedLine.contains("}")) {
                methodCount++;
                methodStarted = false;  
                System.out.println(lineNumber + ": Method Length: " + methodLength + "| Method Count: " + methodCount);
            }
        }
        // När alla startas lägg till ängd bara då! Om speciell lägg till två eller fler
        if (methodStarted) {
            if (trimmedLine.contains("{") && methodString.matches(invalidLineMethodRegex)) {
                methodLength += 2; // Does not cover if they make more spaces between declaration and  first "{". Consider for line 23,24,25 in TestFile
                methodString = "";
            }
            methodLength++;
            if (trimmedLine.contains("{")) {
                methodOpenBrace++;
            }
            if (trimmedLine.contains("}")) {
                methodEndBrace++;

                if (trimmedLine.length() > 1) {
                    MDV.lineNumber = lineNumber;
                    System.out.println("Line: " + lineNumber + ": } should be alone on line.");
                    MDV.declarationViolation = "Closing brace '}' should start a line by itself, except for when it is a null statement.";
                }
                if (methodEnd()) {
                    methodStarted = false;
                    methodCount++;
                    // methodLength = 0;
                    // Add into report here and then reset values
                    System.out.println(lineNumber + ": Method Length: " + methodLength + "| Method Count: " + methodCount);
                }
            }
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
