public class Type {
    private String type;
    public static final Type INT = new Type("INT");
    public static final Type FLOAT = new Type("FLOAT");
    public static final Type STRING = new Type("STRING");

    public Type(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Type))
            return false;
        return type.equals(((Type) o).type);
    }
}
