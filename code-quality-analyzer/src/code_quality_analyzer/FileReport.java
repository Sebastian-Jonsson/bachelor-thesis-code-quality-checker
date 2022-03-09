package code_quality_analyzer;

import java.util.ArrayList;
import java.util.List;

import code_quality_analyzer.Rules.MethodChecker.MethodDeclarationViolation;
import code_quality_analyzer.Rules.LineLength.LineLengthViolation;

public class FileReport {
    // Metadata
    public String filePath = "";
    public int totalLines = 0;
    public int linesOfCode = 0;
    public int blankLines = 0;
    public int amountOfComments = 0;
    public int linesOfComments = 0;
    public int amountOfViolations = 0;

    // Rules Implementations
    public List<LineLengthViolation> lineLengthViolations = new ArrayList<LineLengthViolation>();
    public List<MethodDeclarationViolation> methodDeclarationViolations;

}
