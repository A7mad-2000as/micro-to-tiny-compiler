import java.util.*;

public class SymbolTable {
    private String name;
    private LinkedHashMap<String, Symbol> map;

    public SymbolTable(String name) {
        this.name = name;
        map = new LinkedHashMap<>();
    }

    public LinkedHashMap<String, Symbol> getMap() {
        return map;
    }


    @Override
    public String toString() {
        String outputString = "<<Scope " + name + ">>\n";
        Collection<Symbol> symbols = map.values();
        for (Symbol symbol : symbols)
            outputString += (symbol.toString() + "\n");

        return outputString;
    }
}
