package code_quality_analyzer.Rules;

public class RulesConfig {
    // Do not support other file types.
    public final String languageSupport = ".java";

    // Length Rules.
    public int MAX_LINE_LENGTH = 80;
    public int MAX_FILE_LENGTH = 2000;

    // Declaration Rules.
    public String validMethodRegex = "(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+)\\([^\\)]*\\) *(\\{?|[^;]) (\\{)(\\})*";
    public String invalidLineMethodRegex = "(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+) *\\([^\\)]*\\)";
    public String invalidSpaceParenthesMethodRegex = "(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+) \\([^\\)]*\\) *(\\{?|[^;]) *(\\{)*(\\})*";
    public String invalidNullBraceMethodRegex = "(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+) *\\([^\\)]*\\) *(\\{?|[^;]) (\\{) +(\\})";
    public String classOrInterfaceRegex = "(public|protected|private|static|\\s) +(class|interface|\\s) [\\w\\<\\>\\[\\]]+\\ *(\\{?|[^;]) (\\{)*";
}
