import java.util.ArrayList;

public class TinyCode {
    private ArrayList<TinyInstruction> code;


    public TinyCode() {
        code = new ArrayList<TinyInstruction>();
    }

    public void emitCode(String opcode, String operand1, String operand2) {
        TinyInstruction instruction = new TinyInstruction(opcode, operand1, operand2);
        code.add(instruction);
    }

    public void emitCode(String opcode, String operand2) {
        TinyInstruction instruction = new TinyInstruction(opcode, operand2);
        code.add(instruction);
    }

    @Override
    public String toString() {
        String outputString = "";
        for (TinyInstruction tinyInstruction : code)
            outputString += tinyInstruction + "\n";
        return outputString + "\n";
    }

}
