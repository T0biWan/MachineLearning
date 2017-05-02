public class TestDecisionTree {
    
    
    
    public static void testLearning() {
        DataExampleSet data = new DataExampleSet();
        for (int i = 0; i < (ObservedData.observations.length * 2) / 3; i++) {
            data.addExample(ObservedData.observations[i]);
        }
        DecisionTreeNode t = DecisionTreeLearner.ID3(data);
        System.out.println(t.buildRules());
        int inCorrectClassifiedCount = 0;
        int count = 0;
        for (int i = (ObservedData.observations.length * 2) / 3; i < ObservedData.observations.length; i++) {
            ClassifiedExample ce = ObservedData.observations[i];
            String correct = ce.classification;
            String predicted = t.findClassification(ce.example);
            if (!correct.equals(predicted)) inCorrectClassifiedCount++;
            count++;
        }
        System.out.println("Predicted " + inCorrectClassifiedCount + " of " + count + " incorrectly.");
    }
    
    
    public static void testBasicMethods() {
        DataExample[] examples = {
                new DataExample(3, "N", "J", "kulturell", "mittel"),
                new DataExample(1, "J", "N", "sozial",    "wenig"),
                new DataExample(4, "J", "N", "technisch", "mittel"),
                new DataExample(2, "N", "N", "kulturell", "Spinner"),
                new DataExample(2, "N", "N", "technisch", "mittel"),
                new DataExample(5, "N", "N", "kulturell", "wenig"),
                new DataExample(3, "J", "N", "sozial",    "viel"),
                new DataExample(1, "N", "N", "technisch", "mittel"),
                new DataExample(2, "J", "N", "kulturell", "wenig"),
                new DataExample(3, "N", "N", "sozial",    "mittel"),
                new DataExample(2, "N", "J", "sozial",    "Spinner"),
                new DataExample(4, "J", "J", "technisch", "mittel"),
        };
        String[] classifications = {"N", "J", "J", "J", "J", "N", "J", "J", "J", "N", "J", "N"};
        ClassifiedExample[] ce = new ClassifiedExample[examples.length];
        for (int i = 0; i < examples.length; i++) ce[i] = new ClassifiedExample(examples[i], classifications[i]);
        DataExampleSet des = new DataExampleSet();
        
        System.out.println(des);
        
        for (ClassifiedExample ex : ce) des.addExample(ex);
        System.out.println("Entropie: " + des.getEntropy());
        
        System.out.println("Informationsgewinn Abinote: " + des.getinformationGain(0));
        System.out.println("Informationsgewinn Vor St. pr.: " + des.getinformationGain(1));
        System.out.println("Informationsgewinn wirt. unabh.: " + des.getinformationGain(2));
        System.out.println("Informationsgewinn Interesse: " + des.getinformationGain(3));
        System.out.println("Informationsgewinn R.-Zeit: " + des.getinformationGain(4));        
    }
    

    public static void main(String[] args) {
        testBasicMethods();
        testLearning();
    }
    
}
