import java.util.*;

public class DataExampleSet {
    private LinkedList<ClassifiedExample> examples;
    
    public DataExampleSet() {
        examples = new LinkedList<>();
    }
    
    
    public void addExample(ClassifiedExample e) {
        if (examples.size() > 0) {
            if (examples.getFirst().example.getSize() != e.example.getSize())
                throw new IllegalArgumentException("Set allows only examples whith equal number of attributes.");
        }
        examples.add(e);
    }

    
    /**
     * @return The number of Examples in this set.
     */
    public int getSize() {
        return examples.size();
    }
    
    
    /**
     * @return The number of attributes each example of this set has.
     */
    public int getAttributeCount() {
        if (examples.size() == 0) return -1;
        else return examples.getFirst().example.getSize();
    }
    
    
    /**
     * @return An array that contains all values of all attributes, i.e.
     *         entry i of the returned array contains all values that attribute
     *         i realizes in at least one example of this set. 
     */
    public List<Object>[] getAttributeValues() {
        @SuppressWarnings("unchecked")
        List<Object>[] attributeValues = new List[getAttributeCount()];
        for (int i = 0; i < getAttributeCount(); i++) {
            attributeValues[i] = getAttribValuesOf(i);
        }
        return attributeValues;
    }

    
    
    public List<ClassifiedExample> getExamples() {
        return examples;
    }
    
    
    
    /**
     * @param attribNr The number of the attribute.
     * @return A list of values that the attribute with the given number realizes in 
     *         the examples of this set.
     */
    public List<Object> getAttribValuesOf(int attribNr) {
        HashSet<Object> h = new HashSet<>();
        for (ClassifiedExample ce : examples) {
            h.add(ce.example.getData(attribNr));
        }
        LinkedList<Object> li = new LinkedList<>();
        li.addAll(h);
        return li;
    }
    
    
    /**
     * Sorts the examples of this data set into subsets 
     * such that all members of one subset have the same value
     * in the attribute that is given by nr.
     * 
     * @return A partition (given as list of sets) of this set
     *         such that there is exactly one subset for every value of the attribute nr 
     *         and for each of these subsets S  
     *         all x in S have the same value on attribute nr. 
     */
    public HashMap<Object, DataExampleSet> splitAtAttribute(int nr) {
        //TODO: Implementieren Sie diese Methode!
        HashMap<Object, DataExampleSet> subsets = new HashMap<>();
        for (ClassifiedExample example: examples) {
            Object value = example.example.getData(nr);
            if (subsets.containsKey(value)) {
                subsets.get(value).addExample(example);
            } else {
                DataExampleSet s = new DataExampleSet();
                s.addExample(example);
                subsets.put(value, s);
            }
        }

        return subsets;
    }
    
    
    /**
     * @return A map, where each possible classification occurring in this set
     * is mapped to the number of examples classified in that way.
     * Example: If this set consists of
     * <ul>
     *   <li>(a, b, c) --> "blue"</li>
     *   <li>(d, e, f) --> "red"</li>
     *   <li>(g, h, i) --> "blue"</li>
     *   <li>(j, k, l) --> "red"</li>
     *   <li>(m, n, o) --> "blue"</li>
     * </ul>
     * then this method returns a map, where "blue" is mapped to 3 and "red" is mapped to 2.
     */
    public HashMap<String, Integer> getClassificationOccurences() {
        //TODO: Implementieren Sie diese Methode!
        HashMap<String, Integer> occurences = new HashMap<>();
        for (ClassifiedExample example: examples) {
            String classification = example.classification;
            if (occurences.containsKey(classification)) {
                Integer o = occurences.get(classification);
                o++;
            } else {
                occurences.put(classification, 1);
            }
        }

        return occurences;
    }
    
    
    
    /**
     * @return The classification that most of the examples in this set are
     *         mapped to. If there is a tie between classifications (i.e. 
     *         there are an equal number of examples for two or more
     *         classifications, then one of these is returned in an undefined way).
     */
    public String getClassificationOfMajority() {
        //TODO: Implementieren Sie diese Methode!
        String classificationOfMajority = "";
        int majority = 0;
        HashMap<String, Integer> occurences = getClassificationOccurences();
        Set<String> classifications = occurences.keySet();

        for (String classification: classifications) {
            int occurence = occurences.get(classification);
            if (occurence > majority) {
                classificationOfMajority = classification;
                majority = occurence;
            }
        }

        return classificationOfMajority;
    }
    
    
    
    /**
     * @return The entropy of this set.
     */
    public double getEntropy() {
        //TODO: Implementieren Sie diese Methode!
       double entropy = 0;
       HashMap<String, Integer> classificationOccurences = getClassificationOccurences();
       Set<String> classifications = classificationOccurences.keySet();

       for(String classification: classifications) {
          double probability = classificationOccurences.get(classification)/getSize(); // = P(K) = Amount / All rows
          double logBase2 = Math.log(probability) / Math.log(2);                       // ln(P(K)) / ln(2) = log2(P(K))
          entropy += probability * logBase2;                                           // = P(K)*log2(P(K))
       }

       return entropy * (-1);
    }
    
    /**
     * @return The information gain of this set when splitting it at the attribute given by nr.
     */
    public double getinformationGain(int nr) {
        //TODO: Implementieren Sie diese Methode!
        double informationGain = 0;
        double completeAmount = getSize();
        double entropyBeforeSplit = getEntropy();
        double entropyOfAllSubsets = 0;
        HashMap<Object, DataExampleSet> subsets = splitAtAttribute(nr);
        Set<Object> keys = subsets.keySet();
        for (Object key: keys) {
            // M = completeAmount
            // M A b = amountOfKeyInSubset
            // M A b / M = dividedAmounts
            // Key = values for a column
            // Attribute = column

            double amountOfKeyInSubset = subsets.get(key).getSize();
            double dividedAmounts = amountOfKeyInSubset/completeAmount;

            entropyOfAllSubsets += dividedAmounts * subsets.get(key).getEntropy();
        }

        informationGain = entropyBeforeSplit - entropyOfAllSubsets;

        return informationGain;
    }
    
    
    
    /**
     * @return The attribute-number of the attribute that will deliver 
     *         the largest information gain when splitting this set at that attribute.
     */
    public int getAttributeWithLargestInformationGain() {
        //TODO: Implementieren Sie diese Methode!
        int attributeWithLargestInformationGain = 0;
        double largestInformationGain = 0;

        for(int i = 0; i < getSize(); i++) {
            double informationGain = getinformationGain(i);
            if (informationGain > largestInformationGain) {
                largestInformationGain = informationGain;
                attributeWithLargestInformationGain = i;
            }
        }

        return attributeWithLargestInformationGain;
    }
    
    
    public String toString() {
        String s = "";
        for (ClassifiedExample e : examples) s = s + e + "\n";
        return s;
    }
    
}


