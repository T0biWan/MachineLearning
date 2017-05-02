public class ClassifiedExample {
    
    public DataExample example;
    public String classification;
    
    
    public ClassifiedExample(DataExample example, String classification) {
        super();
        this.example = example;
        this.classification = classification;
    }
    
    
    public String toString() {
        return example + " --> " + classification;
    }
    

}
