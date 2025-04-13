package simplf;
 
import java.util.List;

class SimplfFunction implements SimplfCallable {
    private final Stmt.Function declaration;
    private Environment closure;

    SimplfFunction(Stmt.Function declaration, Environment closure) {
        this.declaration = declaration;
        this.closure = closure;
    }

    public void setClosure(Environment environment) {
        this.closure = environment;
    }

    //unexpected behaviour
    @Override
    public Object call(Interpreter interpreter, List<Object> args){
        //need to create a new environment everytime a function is called -> environment diagrams
        Environment tempEnv = new Environment(closure);
        for(int i=0;i<declaration.params.size();i++){
            tempEnv = tempEnv.define(declaration.params.get(i), declaration.params.get(i).lexeme, args.get(i));
        }

        //store the return value
        Object returnValue=null;
        for(int i=0;i<declaration.body.size();i++){
            Stmt functionStmt = declaration.body.get(i);
    
            if (i==declaration.body.size()-1 && functionStmt instanceof Stmt.Expression){
                //last line of the function has the return value
                returnValue = interpreter.evaluateRecEnv(((Stmt.Expression) functionStmt).expr,tempEnv);
            }else if (functionStmt instanceof Stmt.Var){
                //assignment statements
                Object value = interpreter.evaluate(((Stmt.Var) functionStmt).initializer);
                tempEnv = tempEnv.define(((Stmt.Var) functionStmt).name, ((Stmt.Var) functionStmt).name.lexeme, value);
            }else if (functionStmt instanceof Stmt.Function){
                //inner functions
                Stmt.Function func = (Stmt.Function) functionStmt;
                tempEnv = tempEnv.define(func.name, func.name.lexeme, null);
                SimplfFunction fn = new SimplfFunction(func,tempEnv);
                tempEnv.assign(func.name, fn);
            }else{
                //regular statements
                interpreter.executeRecEnv(functionStmt,tempEnv);
            }
        }
        return returnValue;
    }

    @Override
    public String toString() {
        return "<fn " + declaration.name.lexeme + ">";
    }
    
    public int args() {
        return declaration.params.size();
    }
}