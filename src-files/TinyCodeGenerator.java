import java.util.Collection;
import java.util.HashMap;

public class TinyCodeGenerator {
    private IR ir;
    private ScopeTree scopeTree;
    private TinyCode tinyCode;
    private int regCounter = 1;
    private HashMap<String, String> registerConversionMap;
    private HashMap<String, Type> registerTypeMap;

    public TinyCodeGenerator(IR ir, ScopeTree scopeTree) {
        this.ir = ir;
        this.scopeTree = scopeTree;
        this.scopeTree.resetWorkingScope();
        tinyCode = new TinyCode();
        registerConversionMap = new HashMap<>();
        registerTypeMap = new HashMap<>();

    }

    public TinyCode getTinyCode() {
        generateTinyCode();
        return tinyCode;
    }

    private void generateTinyCode() {
        emitVariableInformation();
        emitInstructions();
    }



    private void emitVariableInformation() {
        Collection<Symbol> symbols = scopeTree.getCurrentTable().getMap().values();
        for (Symbol symbol : symbols) {
            if (symbol.getType().equals(Type.STRING))
                tinyCode.emitCode("str", symbol.getId(), symbol.getValue());
            else
                tinyCode.emitCode("var", symbol.getId());
        }
    }

    private void emitInstructions() {
        for (IRStatement statement : ir.getCode()) {
            switch (statement.getOpcode()) {
                case "LABEL":
                    tinyCode.emitCode("label", statement.getResult());
                    break;
                case "JUMP":
                    tinyCode.emitCode("jmp", statement.getResult());
                    break;

                case "STOREI":
                case "STOREF":
                    String src;
                    if (statement.getOperator1().charAt(0) == '$')
                        src = registerConversionMap.get(statement.getOperator1());
                    else
                        src = statement.getOperator1();

                    tinyCode.emitCode("move", src, statement.getResult());
                    break;
                case "READI":
                    tinyCode.emitCode("sys readi", statement.getResult());
                    break;
                case "READF":
                    tinyCode.emitCode("sys readr", statement.getResult());
                    break;
                case "WRITEI":
                    tinyCode.emitCode("sys writei", statement.getResult());
                    break;
                case "WRITEF":
                    tinyCode.emitCode("sys writer", statement.getResult());
                    break;
                case "WRITES":
                    tinyCode.emitCode("sys writes", statement.getResult());
                    break;

                case "ADDI":
                case "ADDF":
                    if (statement.getOperator1().equals(statement.getResult()) && statement.getOperator2().equals(statement.getResult())) {
                        String  reg = registerConversionMap.get(statement.getResult());
                        tinyCode.emitCode(statement.getOpcode().equals("ADDI") ? "addi" : "addr", reg, reg);
                    }

                    else if (statement.getResult().equals(statement.getOperator1())) {
                        String equalReg = registerConversionMap.get(statement.getResult());
                        String otherOperand;
                        if (statement.getOperator1().charAt(0) == '$')
                            otherOperand = registerConversionMap.get(statement.getOperator2());
                        else
                            otherOperand = statement.getOperator2();

                        tinyCode.emitCode(statement.getOpcode().equals("ADDI") ? "addi" : "addr", otherOperand, equalReg);
                    }

                    else if (statement.getResult().equals(statement.getOperator2())) {
                        String equalReg = registerConversionMap.get(statement.getResult());
                        String otherOperand;
                        if (statement.getOperator1().charAt(0) == '$')
                            otherOperand = registerConversionMap.get(statement.getOperator1());
                        else
                            otherOperand = statement.getOperator1();
                        tinyCode.emitCode(statement.getOpcode().equals("ADDI") ? "addi" : "addr", otherOperand, equalReg);
                    }

                    else {
                        String reg = registerConversionMap.get(statement.getResult());
                        if (reg == null) {
                            reg = "r" + regCounter++;
                            registerConversionMap.put(statement.getResult(), reg);
                        }

                        String operand1;
                        if (statement.getOperator1().charAt(0) == '$')
                            operand1 = registerConversionMap.get(statement.getOperator1());
                        else
                            operand1 = statement.getOperator1();

                        String operand2;
                        if (statement.getOperator2().charAt(0) == '$')
                            operand2 = registerConversionMap.get(statement.getOperator2());
                        else
                            operand2 = statement.getOperator2();

                        tinyCode.emitCode("move", operand1, reg);
                        tinyCode.emitCode(statement.getOpcode().equals("ADDI") ? "addi" : "addr", operand2, reg);
                    }

                    registerTypeMap.put(statement.getResult(), statement.getOpcode().equals("ADDI") ? Type.INT : Type.FLOAT);
                    break;

                case "MULTI":
                case "MULTF":
                    if (statement.getOperator1().equals(statement.getResult()) && statement.getOperator2().equals(statement.getResult())) {
                        String  reg = registerConversionMap.get(statement.getResult());
                        tinyCode.emitCode(statement.getOpcode().equals("MULTI") ? "muli" : "mulr", reg, reg);
                    }

                    else if (statement.getResult().equals(statement.getOperator1())) {
                        String equalReg = registerConversionMap.get(statement.getResult());
                        String otherOperand;
                        if (statement.getOperator1().charAt(0) == '$')
                            otherOperand = registerConversionMap.get(statement.getOperator2());
                        else
                            otherOperand = statement.getOperator2();
                        tinyCode.emitCode(statement.getOpcode().equals("MULTI") ? "muli" : "mulr", otherOperand, equalReg);
                    }

                    else if (statement.getResult().equals(statement.getOperator2())) {
                        String equalReg = registerConversionMap.get(statement.getResult());
                        String otherOperand;
                        if (statement.getOperator1().charAt(0) == '$')
                            otherOperand = registerConversionMap.get(statement.getOperator1());
                        else
                            otherOperand = statement.getOperator1();
                        tinyCode.emitCode(statement.getOpcode().equals("MULTI") ? "muli" : "mulr", otherOperand, equalReg);
                    }

                    else {
                        String reg = registerConversionMap.get(statement.getResult());
                        if (reg == null) {
                            reg = "r" + regCounter++;
                            registerConversionMap.put(statement.getResult(), reg);
                        }

                        String operand1;
                        if (statement.getOperator1().charAt(0) == '$')
                            operand1 = registerConversionMap.get(statement.getOperator1());
                        else
                            operand1 = statement.getOperator1();

                        String operand2;
                        if (statement.getOperator2().charAt(0) == '$')
                            operand2 = registerConversionMap.get(statement.getOperator2());
                        else
                            operand2 = statement.getOperator2();

                        tinyCode.emitCode("move", operand1, reg);
                        tinyCode.emitCode(statement.getOpcode().equals("MULTI") ? "muli" : "mulr", operand2, reg);
                    }

                    registerTypeMap.put(statement.getResult(), statement.getOpcode().equals("MULTI") ? Type.INT : Type.FLOAT);
                    break;

                case "SUBI":
                case "SUBF":
                    if (statement.getOperator1().equals(statement.getResult()) && statement.getOperator2().equals(statement.getResult())) {
                        String  reg = registerConversionMap.get(statement.getResult());
                        tinyCode.emitCode(statement.getOpcode().equals("SUBI") ? "subi" : "subr", reg, reg);
                    }

                    else if (statement.getResult().equals(statement.getOperator1())) {
                        String equalReg = registerConversionMap.get(statement.getResult());
                        String otherOperand;
                        if (statement.getOperator1().charAt(0) == '$')
                            otherOperand = registerConversionMap.get(statement.getOperator2());
                        else
                            otherOperand = statement.getOperator2();
                        tinyCode.emitCode(statement.getOpcode().equals("SUBI") ? "subi" : "subr", otherOperand, equalReg);
                    }

                    else {
                        String reg = registerConversionMap.get(statement.getResult());
                        if (reg == null) {
                            reg = "r" + regCounter++;
                            registerConversionMap.put(statement.getResult(), reg);
                        }

                        String operand1;
                        if (statement.getOperator1().charAt(0) == '$')
                            operand1 = registerConversionMap.get(statement.getOperator1());
                        else
                            operand1 = statement.getOperator1();

                        String operand2;
                        if (statement.getOperator2().charAt(0) == '$')
                            operand2 = registerConversionMap.get(statement.getOperator2());
                        else
                            operand2 = statement.getOperator2();

                        tinyCode.emitCode("move", operand1, reg);
                        tinyCode.emitCode(statement.getOpcode().equals("SUBI") ? "subi" : "subr", operand2, reg);
                    }

                    registerTypeMap.put(statement.getResult(), statement.getOpcode().equals("SUBI") ? Type.INT : Type.FLOAT);
                    break;

                case "DIVI":
                case "DIVF":
                    if (statement.getOperator1().equals(statement.getResult()) && statement.getOperator2().equals(statement.getResult())) {
                        String  reg = registerConversionMap.get(statement.getResult());
                        tinyCode.emitCode(statement.getOpcode().equals("DIVI") ? "divi" : "divr", reg, reg);
                    }

                    else if (statement.getResult().equals(statement.getOperator1())) {
                        String equalReg = registerConversionMap.get(statement.getResult());
                        String otherOperand;
                        if (statement.getOperator1().charAt(0) == '$')
                            otherOperand = registerConversionMap.get(statement.getOperator2());
                        else
                            otherOperand = statement.getOperator2();
                        tinyCode.emitCode(statement.getOpcode().equals("DIVI") ? "divi" : "divr", otherOperand, equalReg);
                    }

                    else {
                        String reg = registerConversionMap.get(statement.getResult());
                        if (reg == null) {
                            reg = "r" + regCounter++;
                            registerConversionMap.put(statement.getResult(), reg);
                        }

                        String operand1;
                        if (statement.getOperator1().charAt(0) == '$')
                            operand1 = registerConversionMap.get(statement.getOperator1());
                        else
                            operand1 = statement.getOperator1();

                        String operand2;
                        if (statement.getOperator2().charAt(0) == '$')
                            operand2 = registerConversionMap.get(statement.getOperator2());
                        else
                            operand2 = statement.getOperator2();

                        tinyCode.emitCode("move", operand1, reg);
                        tinyCode.emitCode(statement.getOpcode().equals("DIVI") ? "divi" : "divr", operand2, reg);
                    }

                    registerTypeMap.put(statement.getResult(), statement.getOpcode().equals("DIVI") ? Type.INT : Type.FLOAT);
                    break;

                case "GT":
                case "GE":
                case "LT":
                case "LE":
                case "EQ":
                case "NE":
                    Type workingType = Type.INT;
                    if (statement.getOperator1().charAt(0) == '$' && registerTypeMap.get(statement.getOperator1()).equals(Type.FLOAT) ||
                        statement.getOperator2().charAt(0) == '$' && registerTypeMap.get(statement.getOperator2()).equals(Type.FLOAT))
                            workingType = Type.FLOAT;
                    else if (statement.getOperator1().contains(".") || statement.getOperator2().contains("."))
                        workingType = Type.FLOAT;
                    else if (Character.isLetter(statement.getOperator1().charAt(0)) && scopeTree.retrieveSymbol(statement.getOperator1()).getType().equals(Type.FLOAT) ||
                             Character.isLetter(statement.getOperator2().charAt(0)) && scopeTree.retrieveSymbol(statement.getOperator2()).getType().equals(Type.FLOAT))
                        workingType = Type.FLOAT;

                    if (statement.getOperator2().charAt(0) == '$') {
                        String operand1;
                        if (statement.getOperator1().charAt(0) == '$')
                            operand1 = registerConversionMap.get(statement.getOperator1());
                        else
                            operand1 = statement.getOperator1();

                        String operand2 = registerConversionMap.get(statement.getOperator2());

                        tinyCode.emitCode(workingType.equals(Type.INT) ? "cmpi" : "cmpr", operand1, operand2);
                    }

                    else {
                        String newReg = "r" + regCounter++;
                        String operand1;
                        if (statement.getOperator1().charAt(0) == '$')
                            operand1 = registerConversionMap.get(statement.getOperator1());
                        else
                            operand1 = statement.getOperator1();

                        String operand2 = statement.getOperator2();

                        tinyCode.emitCode("move", operand2, newReg);
                        tinyCode.emitCode(workingType.equals(Type.INT) ? "cmpi" : "cmpr", operand1, newReg);
                    }

                    switch (statement.getOpcode()) {
                        case "GT":
                            tinyCode.emitCode("jgt", statement.getResult());
                            break;
                        case "GE":
                            tinyCode.emitCode("jge", statement.getResult());
                            break;
                        case "LT":
                            tinyCode.emitCode("jlt", statement.getResult());
                            break;
                        case "LE":
                            tinyCode.emitCode("jle", statement.getResult());
                            break;
                        case "EQ":
                            tinyCode.emitCode("jeq", statement.getResult());
                            break;
                        case "NE":
                            tinyCode.emitCode("jne", statement.getResult());
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown opcode");
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Unknown opcode");
            }
        }
    }
}
