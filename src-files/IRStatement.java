public class IRStatement {
    private String opcode;
    private String operator1;
    private String operator2;
    private String result;

    public IRStatement(String opcode, String operator1, String operator2, String result) {
        this.opcode = opcode;
        this.operator1 = operator1;
        this.operator2 = operator2;
        this.result = result;
    }

    public IRStatement(String opcode, String operator1, String result) {
        this.opcode = opcode;
        this.operator1 = operator1;
        this.result = result;
    }

    public IRStatement (String opcode, String result) {
        this.opcode = opcode;
        this.result = result;
    }

    public void setOperator2(String operator2) {
        this.operator2 = operator2;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setOpcode(String opcode) {
        this.opcode = opcode;
    }

    public String getOpcode() {
        return opcode;
    }

    public String getOperator1() {
        return operator1;
    }

    public String getOperator2() {
        return operator2;
    }

    public String getResult() {
        return result;
    }

    @Override
    public String toString() {
        String outputString = opcode;
        outputString = operator1 != null ? outputString + "  " + operator1  : outputString;
        outputString = operator2 != null ? outputString + "  " + operator2  : outputString;
        outputString = result != null ? outputString + "  " + result  : outputString;
        return outputString;
    }
}
