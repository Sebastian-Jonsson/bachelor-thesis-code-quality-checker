package code_quality_analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
// import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
// import java.util.Scanner;
import java.util.function.Consumer;

import code_quality_analyzer.Rules.RulesConfig;
import code_quality_analyzer.Rules.RulesOrganizer;
// import java.util.stream.Stream;
import code_quality_analyzer.Rules.LineLength.LineLengthViolation;
import code_quality_analyzer.Rules.MethodChecker.MethodDeclarationViolation;


public class CodeReader {
    // ReportFormat[] reportCollection;
    RulesConfig rule = new RulesConfig();
    RulesOrganizer Rules = new RulesOrganizer();
    List<FileReport> reportList = new ArrayList<FileReport>();

    /**
     * Collects user input.
     * @throws IOException
     */
    public void getUserInput() throws IOException {
        // Scanner scan = new Scanner(System.in);
        // System.out.print("Type the path to the input folder: ");
        // String inputFolder = scan.nextLine();
        // System.out.println(inputFolder);s
        
        File inputFolder = new File("D:/Kurser/2DV50E/static-java-analyzer/bachelor-thesis-code-quality-checker/code-quality-analyzer/src/code_quality_analyzer/TestFile.java");
        processFiles(inputFolder, f -> System.out.println(f.getAbsolutePath()));
        tempFileReport();
        // scan.close();
    }

    /**
     * TODO: move this method.
     */
    private void tempFileReport() {
        StringBuilder fileReport = new StringBuilder();
        for (FileReport report : reportList) {
            fileReport.append(
                "\n\nMetaData:\nFile: " + report.filePath
                + "\nTotal Lines: " + report.totalLines + " | Max File Length Violation: " + (report.totalLines > rule.MAX_FILE_LENGTH)
                + "\nLines of Code: " + report.linesOfCode
                + " Blank Lines: " + report.blankLines
                + " Lines of Comments: " + report.linesOfComments
                + " Amount of Comments: " + report.amountOfComments 
                + "\n\nRules: \nTotal Line Length Violations: " + report.lineLengthViolations.size()
            );
            for (LineLengthViolation lineLength : report.lineLengthViolations) {
                fileReport.append(
                    "\nLine Length Violation at line: " + lineLength.lineNumber 
                    + " - Actual Length: " + lineLength.actualLength
                );
            }
            fileReport.append("\n\n\nMethod Declaration Violations below: " + report.methodDeclarationViolations.size());
            for (MethodDeclarationViolation declarationViolation : report.methodDeclarationViolations) {
                fileReport.append(
                    "\n\nParent Class or Interface Length: " + declarationViolation.parentLength
                    + "\nLine: " + declarationViolation.lineNumber + " | Method: " + declarationViolation.methodName
                    + " | Method Length: " + declarationViolation.methodLength
                    + "\nDescription: " + declarationViolation.declarationViolation
                );
            }
        };
        System.out.println(fileReport.toString());
    }

    private void processFiles(File dir, Consumer<File> fileConsumer) throws IOException {
        if (dir.isDirectory()) {
            for (File file1 : dir.listFiles()) {
                processFiles(file1, fileConsumer);
            }
        } else {
            if (dir.toString().endsWith(".java")) {
                readFile(dir);
            }
        }
    }

    private void readFile(File inputFile) throws IOException {
            FileInputStream fileInputStream = new FileInputStream(inputFile);
            InputStreamReader inStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader buffReader = new BufferedReader(inStreamReader);

            FileReport report = new FileReport();
            String line = "";
            int lineNumber = 0;

            report.filePath = inputFile.getAbsolutePath();
            line = buffReader.readLine();

            while (line != null) {
                lineNumber++;
                Rules.rulesChecker(report, line, lineNumber);
                report.totalLines++;
                line = buffReader.readLine();
            }
            buffReader.close();
            reportList.add(report);
    }

}
