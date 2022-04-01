import java.util.ArrayList;

public class ScopeTree {

    private class Node {
        Node parent;
        SymbolTable table;
        ArrayList<Node> children;

        Node(Node parent, SymbolTable table, ArrayList<Node> children) {
            this.parent = parent;
            this.table = table;
            this.children = children;
        }
    }

    private Node workingScope;
    private Node root;


    public void openScope(String name) {
        SymbolTable table = new SymbolTable(name);
        if (workingScope == null) {
            workingScope = new Node(null, table, new ArrayList<Node>());
            root = workingScope;
        }

        else {
            workingScope.children.add(new Node(workingScope, table, new ArrayList<Node>()));
            workingScope = workingScope.children.get(workingScope.children.size() - 1);
        }
    }

    public void closeScope() {
        if (workingScope.parent != null)
            workingScope = workingScope.parent;
    }

    public void enterSymbol(String id, Type type) {
        Symbol symbol = new Symbol(id, type);
        workingScope.table.getMap().put(id, symbol);
    }

    public void enterSymbol(String id, Type type, String value) {
        Symbol symbol = new Symbol(id, type, value);
        workingScope.table.getMap().put(id, symbol);
    }

    public Symbol retrieveSymbol(String id) {
        if (workingScope == null)
            return null;
        Node tracker = workingScope;
        while (tracker != null) {
            Symbol queryResult = tracker.table.getMap().get(id);
            if (queryResult != null) return queryResult;
            tracker = tracker.parent;
        }
        return null;
    }

    public boolean declaredLocally(String id) {
        return workingScope.table.getMap().containsKey(id);
    }

    public void resetWorkingScope() {
        workingScope = root;
    }

    public SymbolTable getCurrentTable() {
        return workingScope.table;
    }

    private String traverse(Node node, String outputString) {
        outputString += (node.table.toString() + "\n");
        for (Node child : node.children)
            outputString = traverse(child, outputString);
        return outputString;
    }

    @Override
    public String toString() {
        String outputString = traverse(root, "");
        return outputString.stripTrailing();
    }

}
