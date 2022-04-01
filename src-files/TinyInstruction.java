public class TinyInstruction {
    String opcode;
    String operand1;
    String operand2;

    public TinyInstruction(String opcode, String operand1, String operand2) {
        this.opcode = opcode;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    public TinyInstruction(String opcode, String operand2) {
        this(opcode, null, operand2);
    }

    @Override
    public String toString() {
        String outputString = opcode;
        if (operand1 != null) outputString += " " + operand1;
        if (operand2 != null) outputString += " " + operand2;
        return outputString;
    }
}
