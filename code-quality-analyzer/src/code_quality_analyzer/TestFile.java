package code_quality_analyzer;

public class TestFile {
    // Convention check file
    public void testMethod() {
        if (1 == 1) {
            
        }
    }

    // This method is not counted
    public void nullMethod() { }

    public void randomMethod() {}

    public void akafus()
    
    {}

    public void akadfsfus()
    {}

    private int testIntMethod ()
    {
        return 0;}
}
