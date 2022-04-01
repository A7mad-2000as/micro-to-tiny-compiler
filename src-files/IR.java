import java.util.ArrayList;

public class IR {
    private ArrayList<IRStatement> code;

    public IR() {
        code = new ArrayList<>();
    }

    public void emitCode(String opcode, String operator1, String operator2, String result) {
        IRStatement statement = new IRStatement(opcode, operator1, operator2, result);
        code.add(statement);
    }

    public void emitCode(String opcode, String operator1, String result) {
        IRStatement statement = new IRStatement(opcode, operator1, result);
        code.add(statement);
    }

    public void emitCode(String opcode, String result) {
        IRStatement statement = new IRStatement(opcode, result);
        code.add(statement);
    }

    public void emitCode(IRStatement statement) {
        code.add(statement);
    }

    public ArrayList<IRStatement> getCode() {
        return code;
    }

    @Override
    public String toString() {
        String outputString = "";
        for (IRStatement statement : code)
            outputString += statement.toString() + "\n";
        return outputString.stripTrailing();
    }
}
