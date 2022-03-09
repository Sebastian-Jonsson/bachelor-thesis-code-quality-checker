import java.io.IOException;

import code_quality_analyzer.CodeReader;

public class App {
    public static void main(String[] args) {
        CodeReader CR = new CodeReader();
        try {
            CR.getUserInput();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
