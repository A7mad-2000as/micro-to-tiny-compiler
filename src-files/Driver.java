import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import java.io.*;

public class Driver {

    public static void main(String[] args) {

        MicroLexer lexer = null;
        try{
            String srcFile = args[0];
            CharStream charStream = CharStreams.fromFileName(srcFile);
            lexer = new MicroLexer(charStream);
        }
        catch(IOException e){
            System.out.println("Error: incorrect input file");
            System.exit(0);
        }

        MicroParser parser = new MicroParser(new CommonTokenStream(lexer));
        ParseTree parseTree = parser.program();

        MyMicroVisitor myVisitor = new MyMicroVisitor();
        ScopeTree scopeTree = (ScopeTree) myVisitor.visit(parseTree);

        IRGenerationVisitor irVisitor = new IRGenerationVisitor(scopeTree);
        IR code = (IR) irVisitor.visit(parseTree);

        TinyCodeGenerator generator = new TinyCodeGenerator(code, scopeTree);
        TinyCode tinyCode = generator.getTinyCode();
        System.out.print(tinyCode);

        /* **************** Write to an output file at the same path as the input file **************** */
//        try {
//            FileWriter writer = new FileWriter((args[0].replace("micro", "out")));
//            writer.write(tinyCode.toString());
//            writer.close();
//        }
//
//        catch (IOException ex) {
//            ex.printStackTrace();
//        }

    }
}
