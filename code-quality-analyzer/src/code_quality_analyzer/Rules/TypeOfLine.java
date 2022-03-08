package code_quality_analyzer.Rules;

import code_quality_analyzer.FileReport;

public class TypeOfLine {
    private boolean commentStarted;
    
    public void lineTypeCount(FileReport report, String line) {
        String trimmedLine = line.trim();

        if (singleLineComment(trimmedLine)) {
            report.amountOfComments++;
        } 
        else if (multiLineStart(trimmedLine)) {
            commentStarted = true;
            report.linesOfComments++;
            if (oneLineJavaComment(trimmedLine)) {
                report.linesOfComments++;
                report.amountOfComments++;
                commentStarted = false;
            }    
        } 
        else if (commentStarted) {

            if (multiLineEnd(trimmedLine)) {
                commentStarted = false;
                report.amountOfComments++;
                report.linesOfComments++;
            } 
            else {
                report.linesOfComments++;
            }
        } 
        else if (blankLineChecker(trimmedLine)) {
            report.blankLines++;
        } 
        else {
            report.linesOfCode++;
        }
    }

    private boolean blankLineChecker(String line) {
        return line.length() == 0;
    }

    private boolean singleLineComment(String line) {
        return line.startsWith("//");
    }

    private boolean multiLineStart(String line) {
        return line.startsWith("/*");
    }

    private boolean multiLineEnd(String line) {
        return line.endsWith("*/") && commentStarted;
    }

    private boolean oneLineJavaComment(String line) {
        return line.startsWith("/*") && line.endsWith("*/");
    }
}
