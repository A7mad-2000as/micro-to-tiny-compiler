import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.Stack;

public class IRGenerationVisitor extends MicroBaseVisitor<Object> {
    private IR code;
    private ArrayList<String> ids;
    private int labelCounter = 1;
    private int tempRegCounter = 1;
    private ScopeTree scopeTree;
    private Stack<String> labels = new Stack<>();

    public IRGenerationVisitor(ScopeTree scopeTree) {
        this.scopeTree = scopeTree;
        this.scopeTree.resetWorkingScope();
    }

    @Override
    public Object visitProgram(MicroParser.ProgramContext ctx) {
        code = new IR();

        visitChildren(ctx);
        return code;
    }

    @Override
    public Object visitId(MicroParser.IdContext ctx) {
        return ctx.identifier.getText();
    }

    @Override
    public Object visitId_list(MicroParser.Id_listContext ctx) {
        ids = new ArrayList<>();
        String id = (String) visit(ctx.id());
        ids.add(id);
        visit(ctx.id_tail());
        return ids;
    }

    @Override
    public Object visitMoreId(MicroParser.MoreIdContext ctx) {
        String id = (String) visit(ctx.id());
        ids.add(id);
        visit(ctx.id_tail());
        return null;
    }

    @Override
    public Object visitEpsilonRule(MicroParser.EpsilonRuleContext ctx) {
        return null;
    }

    @Override
    public Object visitFunc_decl(MicroParser.Func_declContext ctx) {
        String id = (String) visit(ctx.id());
        code.emitCode("LABEL", id);
        visitChildren(ctx);
        return null;
    }

    @Override
    public Object visitAssign_expr(MicroParser.Assign_exprContext ctx) {
        String exprResult =  (String) (((ArrayList<Object>) visit(ctx.expr())).get(0)); //see visitExpr()
        String id = (String) visit(ctx.id());
        String opcode = scopeTree.retrieveSymbol(id).getType().equals(Type.INT) ? "STOREI" : "STOREF";
        code.emitCode(opcode, exprResult, id);
        return null;
    }

    @Override
    public Object visitRead_stmt(MicroParser.Read_stmtContext ctx) {
        ArrayList<String> ids = (ArrayList<String>) visit(ctx.id_list());
        for (String id : ids) {
            String opcode = scopeTree.retrieveSymbol(id).getType().equals(Type.INT) ? "READI" : "READF";
            code.emitCode(opcode, id);
        }
        return null;
    }

    @Override
    public Object visitWrite_stmt(MicroParser.Write_stmtContext ctx) {
        ArrayList<String> ids = (ArrayList<String>) visit(ctx.id_list());
        for (String id : ids) {
            String opcode;
            Type type = scopeTree.retrieveSymbol(id).getType();
            if (type.equals(Type.STRING))
                opcode = "WRITES";
            else
                opcode = type.equals(Type.INT) ? "WRITEI" : "WRITEF";
            code.emitCode(opcode, id);
        }
        return null;
    }

    @Override
    public Object visitReturn_stmt(MicroParser.Return_stmtContext ctx) {
        String exprResult =  (String) (((ArrayList<Object>) visit(ctx.expr())).get(0)); //see visitExpr()
        code.emitCode("RETURN", exprResult);
        return null;
    }

    @Override
    public Object visitIf_stmt(MicroParser.If_stmtContext ctx) {
        String elseLabel = (String) visit(ctx.cond());
        labels.push(elseLabel);
        visit(ctx.stmt_list());
        visit(ctx.else_part());
        return null;
    }

    @Override
    public Object visitElseExists(MicroParser.ElseExistsContext ctx) {
        String endLabel = "L" + labelCounter++;
        code.emitCode("JUMP", endLabel);
        code.emitCode("LABEL", labels.pop());
        visit(ctx.stmt_list());
        code.emitCode("LABEL", endLabel);
        return null;
    }

    @Override
    public Object visitNoElse(MicroParser.NoElseContext ctx) {
        code.emitCode("LABEL", labels.pop());
        return null;
    }

    @Override
    public Object visitCond(MicroParser.CondContext ctx) {
        String firstExprResult =  (String) (((ArrayList<Object>) visit(ctx.expr(0))).get(0)); //see visitExpr()
        String opcode = (String) visit(ctx.compare());
        String secondExprResult =  (String) (((ArrayList<Object>) visit(ctx.expr(1))).get(0)); //see visitExpr()
        String label = "L" + labelCounter++;
        code.emitCode(opcode, firstExprResult, secondExprResult, label);
        return label;
    }

    /* Return opposite of each comparison operation */
    @Override
    public Object visitEquals(MicroParser.EqualsContext ctx) {
        return "NE";
    }

    @Override
    public Object visitNotEquals(MicroParser.NotEqualsContext ctx) {
        return "EQ";
    }

    @Override
    public Object visitLessOrEqual(MicroParser.LessOrEqualContext ctx) {
        return "GT";
    }

    @Override
    public Object visitGreaterOrEqual(MicroParser.GreaterOrEqualContext ctx) {
        return "LT";
    }

    @Override
    public Object visitLess(MicroParser.LessContext ctx) {
        return "GE";
    }

    @Override
    public Object visitGreater(MicroParser.GreaterContext ctx) {
        return "LE";
    }

    @Override
    public Object visitFor_stmt(MicroParser.For_stmtContext ctx) {
        visit(ctx.init_expr());
        String startLabel = "L" + labelCounter++;
        code.emitCode("LABEL", startLabel);
        String exitLabel = (String) visit(ctx.cond());
        visit(ctx.stmt_list());
        visit(ctx.incr_expr());
        code.emitCode("JUMP", startLabel);
        code.emitCode("LABEL", exitLabel);
        return null;
    }

    /* Returns ArrayList of two elements, the first element is the result register or variable, and the second element is the type of the result */
    @Override
    public Object visitExpr(MicroParser.ExprContext ctx) {
        ArrayList<Object> response = (ArrayList<Object>) visit(ctx.expr_prefix());
        ArrayList<Object> termResult = (ArrayList<Object>) visit(ctx.term());
        if (response == null)
            return termResult;
        IRStatement statement = (IRStatement) response.get(0);
        statement.setOperator2((String) termResult.get(0));
        String result = "$T" + tempRegCounter++;
        statement.setResult(result);
        if (statement.getOpcode().equals("ADDI") && termResult.get(1).equals(Type.FLOAT))
            statement.setOpcode("ADDF");
        else if (statement.getOpcode().equals("SUBI") && termResult.get(1).equals(Type.FLOAT))
            statement.setOpcode("SUBF");
        code.emitCode(statement);
        ArrayList<Object> returnList = new ArrayList<>();
        returnList.add(result);
        returnList.add(statement.getOpcode().equals("ADDI") || statement.getOpcode().equals("SUBI") ? Type.INT : Type.FLOAT) ;
        return returnList;
    }

    /* Returns ArrayList of two elements, the first element is an unfinished IRStatement, and the second element is the type of the filled first operand */
    @Override
    public Object visitMoreExpr(MicroParser.MoreExprContext ctx) {
        ArrayList<Object> response = (ArrayList<Object>) visit(ctx.expr_prefix());
        ArrayList<Object> termResult = (ArrayList<Object>) visit(ctx.term());
        String result = (String) termResult.get(0);

        if (response != null) {
            IRStatement statement = (IRStatement) response.get(0);
            statement.setOperator2((String) termResult.get(0));
            result = "$T" + tempRegCounter++;
            statement.setResult(result);
            if (statement.getOpcode().equals("ADDI") && termResult.get(1).equals(Type.FLOAT))
                statement.setOpcode("ADDF");
            else if (statement.getOpcode().equals("SUBI") && termResult.get(1).equals(Type.FLOAT))
                statement.setOpcode("SUBF");
            code.emitCode(statement);
        }

        Type resultType = (response != null && (response.get(1).equals(Type.FLOAT) || termResult.get(1).equals(Type.FLOAT))) ? Type.FLOAT : (Type) termResult.get(1);
        String operation = (String) visit(ctx.addop());
        String opcode;
        if (operation.equals("+"))
            opcode = resultType.equals(Type.INT) ? "ADDI" : "ADDF";

        else
            opcode = resultType.equals(Type.INT) ? "SUBI" : "SUBF";

        IRStatement returnStatement = new IRStatement(opcode, result, null, null);
        ArrayList<Object> returnList = new ArrayList<>();
        returnList.add(returnStatement);
        returnList.add(resultType);
        return returnList;
    }

    @Override
    public Object visitEndExpr(MicroParser.EndExprContext ctx) {
        return null;
    }

    /* Returns ArrayList of two elements, the first element is the result register or variable, and the second element is the type of the result */
    @Override
    public Object visitTerm(MicroParser.TermContext ctx) {
        ArrayList<Object> response = (ArrayList<Object>) visit(ctx.factor_prefix());
        ArrayList<Object> factorResult = (ArrayList<Object>) visit(ctx.factor());
        if (response == null)
            return factorResult;
        IRStatement statement = (IRStatement) response.get(0);
        statement.setOperator2((String) factorResult.get(0));
        String result = "$T" + tempRegCounter++;
        statement.setResult(result);
        if (statement.getOpcode().equals("MULTI") && factorResult.get(1).equals(Type.FLOAT))
            statement.setOpcode("MULTF");
        else if (statement.getOpcode().equals("DIVI") && factorResult.get(1).equals(Type.FLOAT))
            statement.setOpcode("DIVF");
        code.emitCode(statement);
        ArrayList<Object> returnList = new ArrayList<>();
        returnList.add(result);
        returnList.add(statement.getOpcode().equals("MULTI") || statement.getOpcode().equals("DIVI") ? Type.INT : Type.FLOAT) ;
        return returnList;
    }

    /* Returns ArrayList of two elements, the first element is an unfinished IRStatement, and the second element is the type of the filled first operand */
    @Override
    public Object visitMoreFactor(MicroParser.MoreFactorContext ctx) {
        ArrayList<Object> response = (ArrayList<Object>) visit(ctx.factor_prefix());
        ArrayList<Object> factorResult = (ArrayList<Object>) visit(ctx.factor());
        String result = (String) factorResult.get(0);

        if (response != null) {
            IRStatement statement = (IRStatement) response.get(0);
            statement.setOperator2((String) factorResult.get(0));
            result = "$T" + tempRegCounter++;
            statement.setResult(result);
            if (statement.getOpcode().equals("MULTI") && factorResult.get(1).equals(Type.FLOAT))
                statement.setOpcode("MULTF");
            else if (statement.getOpcode().equals("DIVI") && factorResult.get(1).equals(Type.FLOAT))
                statement.setOpcode("DIVF");
            code.emitCode(statement);
        }

        Type resultType = (response != null && (response.get(1).equals(Type.FLOAT) || factorResult.get(1).equals(Type.FLOAT))) ? Type.FLOAT : (Type) factorResult.get(1);
        String operation = (String) visit(ctx.mulop());
        String opcode;
        if (operation.equals("*"))
            opcode = resultType.equals(Type.INT) ? "MULTI" : "MULTF";
        else
            opcode = resultType.equals(Type.INT) ? "DIVI" : "DIVF";

        IRStatement returnStatement = new IRStatement(opcode, result, null, null);
        ArrayList<Object> returnList = new ArrayList<>();
        returnList.add(returnStatement);
        returnList.add(resultType);
        return returnList;
    }

    @Override
    public Object visitEndFactor(MicroParser.EndFactorContext ctx) {
        return null;
    }

    @Override
    public Object visitPrimaryFactor(MicroParser.PrimaryFactorContext ctx) {
        return visit(ctx.primary());
    }

    @Override
    public Object visitExprPrimaryFactor(MicroParser.ExprPrimaryFactorContext ctx) {
        return visit(ctx.expr());
    }

    /* Returns ArrayList of two elements, the first element is the id, and the second element is the type of the id */
    @Override
    public Object visitIdPrimaryFactor(MicroParser.IdPrimaryFactorContext ctx) {
        String value = (String) visit(ctx.id());
        Type type = scopeTree.retrieveSymbol(value).getType();
        ArrayList<Object> returnList = new ArrayList<>();
        returnList.add(value);
        returnList.add(type);
        return returnList;
    }

    /* Returns ArrayList of two elements, the first element is the intLiteral, and the second element is the type (INT) */
    @Override
    public Object visitIntPrimaryFactor(MicroParser.IntPrimaryFactorContext ctx) {
        String value = ctx.intLiteral.getText();
        Type type = Type.INT;
        ArrayList<Object> returnList = new ArrayList<>();
        returnList.add(value);
        returnList.add(type);
        return returnList;
    }

    /* Returns ArrayList of two elements, the first element is the floatLiteral, and the second element is the type (FLOAT) */
    @Override
    public Object visitFloatPrimaryFactor(MicroParser.FloatPrimaryFactorContext ctx) {
        String value = ctx.floatLiteral.getText();
        Type type = Type.FLOAT;
        ArrayList<Object> returnList = new ArrayList<>();
        returnList.add(value);
        returnList.add(type);
        return returnList;
    }

    @Override
    public Object visitPlus(MicroParser.PlusContext ctx) {
        return "+";
    }

    @Override
    public Object visitMinus(MicroParser.MinusContext ctx) {
        return "-";
    }

    @Override
    public Object visitMul(MicroParser.MulContext ctx) {
        return "*";
    }

    @Override
    public Object visitDiv(MicroParser.DivContext ctx) {
        return "/";
    }

}
