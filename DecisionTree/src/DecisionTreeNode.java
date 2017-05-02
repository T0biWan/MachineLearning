import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class DecisionTreeNode {
    
    private DecisionTreeNode parent;
    private LinkedList<DecisionTreeNode> children;
    private HashMap<Object, Integer> attribMapForChildren;
    private HashMap<DecisionTreeNode, Object> childMapForAttribs;
    private int attributeNr;
    private String classification;
    
    
    /**
     * Build a new inner node that splits the data at the given attribute number.
     * @param parent The parent of the inner node or null if it is to be a root node.
     * @param attribute The attribute at which to split the data.
     */
    public DecisionTreeNode(DecisionTreeNode parent, int attribute) {
        classification = null;
        this.attributeNr = attribute;
        children = new LinkedList<>();
        attribMapForChildren = new HashMap<>();
        childMapForAttribs = new HashMap<>();
        this.parent = parent;
    }
    

    /**
     * Build a new leaf node.
     * @param parent The parent of the inner node or null if it is to be a root node.
     * @param classification The classification that is given for data "arriving" at this node.
     */
    public DecisionTreeNode(DecisionTreeNode parent, String classification) {
        this.classification = classification;
        children = null;
        attribMapForChildren = null;
        this.parent = parent;
    }
    
    
    /**
     * Adds a child to this node (which needs to be an inner node).
     * @param node The node that is to be added to this node. 
     * @param forAttribValue The attribute-value that leads to the new child.
     */
    public void addChild(DecisionTreeNode node, Object forAttribValue) {
        if (children == null) 
            throw new RuntimeException("Cannot add child to a leaf node.");
        children.add(node);
        attribMapForChildren.put(forAttribValue, children.size()-1);
        childMapForAttribs.put(node, forAttribValue);
    }
    
    
    /**
     * @return The children of this node as list.
     */
    public List<DecisionTreeNode> getChildren() {
        return children;
    }
    
    
    /**
     * @return The parent of this node.
     */
    public DecisionTreeNode getParent() {
        return parent;
    }
    
    
    /**
     * Classifies the given data by walking to a root node
     * while checking the attributes of the data at each inner node
     * to determine which path to take.
     * 
     * @param de The data that is to be classified.
     * @return The classification of the data according to the decision tree
     *         that sits below this node.
     */
    public String findClassification(DataExample de) {
        if (classification != null) return classification;
        else return children.get(attribMapForChildren.get(de.getData(attributeNr))).findClassification(de);
    }

    
    /**
     * @return The root of the tree that this node is part of.
     */
    public DecisionTreeNode getRoot() {
        if (parent == null) return this;
        else return parent.getRoot();
    }
    
    
    public String toString() {
        String s = "(";
        if (classification == null) {
            s = s + "Attrib. " + attributeNr + ": "; 
            for (DecisionTreeNode c : children) {
                s = s + "Value " + childMapForAttribs.get(c) + ": " +  c.toString() + ", ";
            }
            if (children.size() > 0) s = s.substring(0, s.length() - 2);
        } else {
            s = s + "Class: " + classification;
        }
        return s + ")";
    }

    
    /**
     * @return A list of rules encoding the decision tree below this node.
     */
    public String buildRules() {
        if (classification == null) {
            LinkedList<String> li = buildRestRules();
            String s = "";
            for (String t : li) {
                s = s + "if " + t + "\n";            
            }
            return s;
        } else return "Classify everything as " + classification;
    }
    
    private LinkedList<String> buildRestRules() {
        LinkedList<String> li = new LinkedList<>();
        if (classification == null) {
            for (DecisionTreeNode c : children) {
                Object v = childMapForAttribs.get(c);
                String t = "Attr(" + attributeNr + ") = " + v;
                LinkedList<String> sub = c.buildRestRules();
                for (String s : sub) {
                    if (s.startsWith("then")) li.add(t + " " + s);                     
                    else li.add(t + " and " + s);
                }
            }
        } else {
            li.add("then classify as " + classification);
        }
        return li;
    }

}
