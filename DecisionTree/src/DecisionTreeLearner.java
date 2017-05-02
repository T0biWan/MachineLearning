import java.util.HashSet;
import java.util.List;

public class DecisionTreeLearner {
    
    /**
     * Used to store all values of all attributes that the algorithm knows of. 
     */
    private static List<Object>[] attributeValues;
    
    
    /**
     * @param data Data that is used to build a decision tree.
     * @return A tree that is able to categorize the given data.
     */
    public static DecisionTreeNode ID3(DataExampleSet data) {
        attributeValues = data.getAttributeValues();
        HashSet<Integer>  attribs = new HashSet<>();
        for (int i = 0; i < data.getAttributeCount(); i++) attribs.add(i);
        return ID3(null, data, attribs);
    }
    
    
    private static DecisionTreeNode ID3(DecisionTreeNode parent, DataExampleSet data, HashSet<Integer> attribs) {
        //TODO: Implementieren Sie diese Methode!
    } 
    
    

}
