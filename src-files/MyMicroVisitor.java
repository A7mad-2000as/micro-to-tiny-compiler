import java.util.ArrayList;

public class MyMicroVisitor extends  MicroBaseVisitor<Object> {

     private ScopeTree scopes = new ScopeTree();
     private ArrayList<String> ids = new ArrayList<>();
     private int blockCounter = 1;

    @Override
    public Object visitProgram(MicroParser.ProgramContext ctx) {
        scopes.openScope("Global");
        visit(ctx.pgm_body());
        scopes.closeScope();
        return scopes;
    }

    @Override
    public Object visitId(MicroParser.IdContext ctx) {
        return ctx.identifier.getText();
    }

    @Override
    public Object visitString_decl(MicroParser.String_declContext ctx) {
        Type type = Type.STRING;
        String id = (String) visit(ctx.id());
        String value = (String) visit(ctx.str());
        scopes.enterSymbol(id, type, value);
        return null;
    }

    @Override
    public Object visitStr(MicroParser.StrContext ctx) {
        return ctx.stringLiteral.getText();
    }

    @Override
    public Object visitVar_decl(MicroParser.Var_declContext ctx) {
        ids = new ArrayList<String>();
        Type type = (Type) visit(ctx.var_type());
        visit(ctx.id_list());
        for (String id : ids)
            scopes.enterSymbol(id, type);
        return null;
    }

    @Override
    public Object visitFloatType(MicroParser.FloatTypeContext ctx) {
        return Type.FLOAT;
    }

    @Override
    public Object visitIntType(MicroParser.IntTypeContext ctx) {
        return Type.INT;
    }

    @Override
    public Object visitId_list(MicroParser.Id_listContext ctx) {
        String id = (String) visit(ctx.id());
        ids.add(id);
        visit(ctx.id_tail());
        return null;
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
    public Object visitParam_decl(MicroParser.Param_declContext ctx) {
        Type type = (Type) visit(ctx.var_type());
        String id = (String) visit(ctx.id());
        scopes.enterSymbol(id, type);
        return null;
    }

    @Override
    public Object visitFunc_decl(MicroParser.Func_declContext ctx) {
        String id = (String) visit(ctx.id());
        scopes.openScope(id);
        visit(ctx.param_decl_list());
        visit(ctx.func_body());
        scopes.closeScope();
        return null;
    }

    @Override
    public Object visitIf_stmt(MicroParser.If_stmtContext ctx) {
         scopes.openScope("BLOCK #" + blockCounter++);
         visit(ctx.decl());
         visit(ctx.stmt_list());
         visit(ctx.else_part());
         scopes.closeScope();
         return null;
    }

    @Override
    public Object visitElseExists(MicroParser.ElseExistsContext ctx) {
        scopes.openScope("BLOCK #" + blockCounter++);
        visit(ctx.decl());
        visit(ctx.stmt_list());
        scopes.closeScope();
        return null;
    }

    @Override
    public Object visitNoElse(MicroParser.NoElseContext ctx) {
        return null;
    }

    @Override
    public Object visitFor_stmt(MicroParser.For_stmtContext ctx) {
        scopes.openScope("BLOCK #" + blockCounter++);
        visit(ctx.decl());
        visit(ctx.stmt_list());
        scopes.closeScope();
        return null;
    }
}
