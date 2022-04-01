public class Symbol {
    private Type type;
    private String id;
    private String value;

    public Symbol(String id, Type type, String value) {
        this.type = type;
        this.id = id;
        this.value = value;
    }

    public Symbol(String id, Type type) {
        this(id, type, null);
    }

    public Type getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        String initialString = "name " + id +  " type " + type.toString();
        return type.equals(Type.STRING) ? (initialString + " value " + value) : initialString;
    }
}
