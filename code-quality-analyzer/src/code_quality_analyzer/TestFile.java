package code_quality_analyzer;

public class TestFile {
    // Convention check file
    public void testMethod() {
        if (1 == 1) {
            
        }
    }

    public void nullMethod() { }

    int wrongPlaceInteger = 0;
    public void randomNullMethodWhichIsIntendedToBeFarTooLongInOrderToTriggerTheLineLengthViolation() {}

    public void wrongLine()
    {}
    
    /** */
    private int testIntMethod ()
    {
        return 0;}
}
