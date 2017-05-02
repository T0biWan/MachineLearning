public class DataExample {

    private int size;
    private Object[] data;
    
    public DataExample(Object ... data) {
        size = data.length;
        this.data = new Object[size];
        System.arraycopy(data, 0, this.data, 0, size);
    }

    public int getSize() {
        return size;
    }

        
    public Object getData(int attribNr) {
        return data[attribNr];
    }
    
    
    public String toString() {
        String s = "(";
        for (Object o : data) {
            s = s + o + ", ";
        }
        s = s.substring(0, s.length() - 2) + ")";
        return s;
    }
        
    
    
    
}
