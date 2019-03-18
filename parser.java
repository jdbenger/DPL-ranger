/* Friday, February 22nd 
 * Jacoby Benger
 * CS 403 - Programming Languages
 *
 * PARSER.JAVA
 */

public class parser implements types
{
    private lexeme current;     //Lexeme global
    private lexer lex;
    private environment env;    //will be using to build parse tree
    private evaluator err;      //testing this to handle errors;

    public parser(String filename)
    {
        lex = new lexer(filename);
        env = new environment();
        try
        {
            current = lex.lex();
        }
        catch(Exception e)
        {
            throw new NullPointerException("Unable to open file: " + filename);
        }
    }
    
    public lexeme parse() throws Exception
    {
        lexeme tree = program();
        match(END_FILE); 
        return tree;
    }

    /* HELPER FUNCTIONS */
    private lexeme advance() throws Exception
    {
        lexeme prev = current;
        current = lex.lex();
        if(current.type == ERROR){errorHandler(current);}
        return prev;
    }
    
    private boolean check(String type) throws Exception
    {
        return current.type == type;
    }
    
    private lexeme match(String type) throws Exception
    {
        //System.out.println("looking for: " + type + " current - " + current.type);
        if(check(type)) { return advance(); }
        else
        {
            errorHandler(new lexeme(ERROR, "SYNTAX ERROR: expected " + type + ", got " + current.type, current.line));
            return null;
        }
    }
   
    public int getLinenum()
    {
        return lex.getLine();
    }
    
    /* GRAMMER RULES */
    
    /*PROGRAM*/
    public lexeme program() throws Exception
    {
        //System.out.println("PROGRAM");

        lexeme left = null, right = null;
        
        left = definition();
        if(programPending())
        {
            right = program();
        }
        return env.cons(PROGRAM, left, right);
    }
    
    //PROGRAM PENDING
    private boolean programPending() throws Exception
    {
        return definitionPending();
    }

    /*DEFINITION*/
    public lexeme definition() throws Exception
    {
        lexeme tree;

        if(classDefPending())
        {
            tree = classDef();
        }
        else if(functionDefPending())
        {
            tree = functionDef();
        }
        else
        {
            tree = idExpr();
        }
        return env.cons(DEFINITION, tree, null);
    }

    //DEFINITION PENDING
    private boolean definitionPending() throws Exception
    {
        return classDefPending() || functionDefPending() || idExprPending();
    }

    /*CLASSDEF*/
    public lexeme classDef() throws Exception
    {
        lexeme left, right;

        match(CLASS);
        left = match(ID);
        right = block();
        if(check(SEMICOLON) == false)
        {
            errorHandler(new lexeme(ERROR, "CLASS DECLARATION MUST END SEMICOLON", current.line));
        }
        match(SEMICOLON);

        return env.cons(CLASS, left, right);
    }

    //CLASSDEF PENDING
    private boolean classDefPending() throws Exception
    {
        return check(CLASS);
    }
    
    /*VARDEF*/
    public lexeme varDef() throws Exception
    {
        //System.out.println("VARDEF");

        lexeme left = null, right = null;

        match(VAR_TYPE);
        if(check(ID) == false) 
        {
            errorHandler(new lexeme(ERROR, "BAD VARIABLE DECLARATION", current.line-1));
        }
        left = match(ID);
        right = optInit();
        match(SEMICOLON);

        return env.cons(VARDEF, left, right);
    }
    
    //VARDEF PENDING
    private boolean varDefPending() throws Exception
    {
        return check(VAR_TYPE);
    }

    /*IDEXPR*/
    public lexeme idExpr() throws Exception
    {
        //System.out.println("IDEXPR");

        lexeme tree = match(ID);
        if(initPending())
        {
            lexeme temp = init();
            temp.left = tree;
            return temp;
        }
        else if(functionCallPending())
        {
            lexeme temp  = functionCall();
            return env.cons(FUNC_CALL, tree, temp);
        }
        return tree;
    }

    //IDEXPR PENDING
    private boolean idExprPending() throws Exception
    {
        return check(ID);
    }
    
    /*UNARY*/
    public lexeme unary() throws Exception
    {
        //System.out.println("UNARY");
        lexeme tree = null;
        
        if(idExprPending())
        {
            tree = idExpr();
            return tree;
        }
        else if(check(INTEGER)) return tree = match(INTEGER);
        else if(check(REAL))    return tree = match(REAL);
        else if(check(BOOLEAN)) return tree = match(BOOLEAN);
        else if(check(STRING))  return tree = match(STRING);
        else if(check(MINUS))
        {
            match(MINUS);
            if(check(MINUS))
            {
                match(MINUS);
                tree = unary();
                return env.cons(DEC, tree, null);
            }
            tree = unary();
            return env.cons(UMINUS, tree, null);
        }
        else if(check(PLUS))
        {
            match(PLUS);
            match(PLUS);
            tree = unary();
            return env.cons(INC, tree, null);
        }
        else if(parenExprPending())
        {
            tree = parenExpr();
            return tree;
        }
        else if(lambdaPending())
        {
            return lambdaDef();
        }
        else if(functionCallPending())
        {
            tree = lambdaCall();
            return tree;
        }
        else if(check(PRINT))
        {
            match(PRINT);
            match(OPAREN);
            tree = argList();
            match(CPAREN);
            return env.cons(PRINT_STATE, tree, null);
        }
        else if(check(PRINTLN))
        {
            match(PRINTLN);
            match(OPAREN);
            tree = argList();
            match(CPAREN);
            return env.cons(PRINTLN_STATE, tree, null);
        }
        System.out.println("UNARY HIT END WITHOUT RETURN type = " + tree.type);
        return null;
    }
    
    //UNARY PENDING
    private boolean unaryPending() throws Exception
    {
        return idExprPending() || check(INTEGER) || check(REAL) || check(BOOLEAN) || check(STRING) || check(MINUS) || check(PLUS) || check(PRINT) || parenExprPending()
        || functionCallPending() || check(LAMBDA) || check(PRINTLN);
    }
    
    /*OPERATOR*/
    public lexeme operator() throws Exception
    {
        //System.out.println("OPERATOR");
        lexeme tree;

        if(check(PLUS))          return tree = match(PLUS);
        else if(check(MINUS))    return tree = match(MINUS);
        else if(check(TIMES))    return tree = match(TIMES);
        else if(check(DIVIDES))  return tree = match(DIVIDES);
        else if(check(MOD))      return tree = match(MOD);
        else                     return tree = match(DOT);
    
    }
    
    //OPERATOR PENDING
    private boolean operatorPending() throws Exception
    {
        return check(PLUS) || check(MINUS) || check(TIMES) || check(DIVIDES) || check(MOD) || check(DOT);
    }
    
    /*COMPARATOR*/
    public lexeme comparator() throws Exception
    {
        //System.out.println("COMPARATOR");
        lexeme tree;

        if(check(LESSTHAN))          
        {
            tree = match(LESSTHAN);
            if(check(ASSIGN))
            {
                match(ASSIGN);
                return env.cons(LESSEQUAL, null, null);
            }
            else
                return tree; 
        }
        else if(check(GREATTHAN))
        {  
             tree = match(GREATTHAN);
             if(check(ASSIGN))
             {
                match(ASSIGN);
                return env.cons(GREATEQUAL, null, null);
             }
             else
                return tree;

        }
        else if(check(ASSIGN))
        {
            match(ASSIGN);
            match(ASSIGN);
            return env.cons(EQUAL, null, null);
        }        
        else if(check(NOT))
        {
            tree = match(NOT);
            if(check(ASSIGN))
            {
                match(ASSIGN);
                return env.cons(NOTEQUAL, null, null);
            }
            else
                return tree;
        }
        else
            return new lexeme(ERROR, "BAD COMPARERATOR", current.line);  
    }
    
    //COMPARATOR PENDING
    private boolean comparatorPending() throws Exception
    {
        return check(LESSTHAN) || check(GREATTHAN) || check(NOT) || check(ASSIGN);
    }
    
    /*PARENEXPR*/
    public lexeme parenExpr() throws Exception
    {
        //System.out.println("PARENEXPR");
        lexeme tree;
        
        match(OPAREN);
        tree = expr();
        match(CPAREN); 
            
        return env.cons(PAREN_EXPR, tree, null);
    }
    
    //PARENEXPR
    private boolean parenExprPending() throws Exception
    {
        return check(OPAREN);
    }
    
    /*INITIALIZATION*/
    public lexeme init() throws Exception 
    {
        //System.out.println("INITILIZATION");

        lexeme right;
        match(ASSIGN);
        right = expr();

        return env.cons(INIT, null, right);

    }

    //INIT PENDING
    private boolean initPending() throws Exception 
    {
        return check(ASSIGN);
    }

    /*OPT INIT*/
    private lexeme optInit() throws Exception
    {
        if(initPending())
            return init();
        else    
            return env.cons(INIT, null, new lexeme(INTEGER, 0, 0));
    }
    
    /*EXPR*/
    public lexeme expr() throws Exception
    {
        //System.out.println("EXPR");
        lexeme tree = unary();
        
        if(operatorPending())
        {
            lexeme temp = operator();
            lexeme x = expr();
            return env.cons(temp.type, tree, x);
        }
        return tree;
    }
    
    //EXPR PENDING
    private boolean exprPending() throws Exception
    {
        return unaryPending();
    }
    
    /*PARAMLIST*/
    public lexeme paramList() throws Exception 
    {
        //System.out.println("PARAM_LIST");
        lexeme tree = match(ID);
        lexeme temp = null;

        if(check(COMMA))
        {
            match(COMMA);
            if(paramListPending() == false)
            {
                errorHandler(new lexeme(ERROR, "MUST SPECIFY VARIABLE IN LIST AFTER COMMA", current.line-1));
            }
            else
            {
                temp = paramList();
            }
        }
        
        return env.cons(PARAM_LIST, tree, temp);
    }
    
    //PARAMLIST PENDING
    private boolean paramListPending() throws Exception
    {
        return check(ID);
    }
    
    /*OPTPARAMLIST*/
    public lexeme optParamList() throws Exception
    {
        //System.out.println("OPT_PARAM_LIST");
        lexeme tree;
        
        if(paramListPending())
            return tree = paramList();
        else   
            return null;
    }
    
    //OPTPARAMLIST PENDING
    private boolean optParamListPending() throws Exception
    {
        return paramListPending();
    }

    /*ARGLIST*/
    public lexeme argList() throws Exception
    {
        //System.out.println("ARG_LIST");
        lexeme tree = expr();
        lexeme temp = null;

        if(check(COMMA))
        {
            match(COMMA);
            if(argListPending() == false)
            {
                errorHandler(new lexeme(ERROR, "MUST SPECIFY ARGUMENT IN LIST AFTER COMMA", current.line-1));
            }
            else
            {
                temp = argList();
            }
        }
        return env.cons(ARG_LIST, tree, temp);
    }

    //ARG_LIST PENDING
    private boolean argListPending() throws Exception
    {
        return exprPending();
    }

    /*OPTARGLIST*/
    public lexeme optArgList() throws Exception
    {
        //System.out.println("OPT_ARG_LIST");
        lexeme tree;
            
        if(argListPending())
            return tree = argList();
        else   
            return null;
    }
    
    /*STATEMENT*/
    public lexeme statement() throws Exception
    {
        //System.out.println("STATEMENT");
        lexeme tree;
        
        if(exprPending())
        {
            tree = expr();
            match(SEMICOLON);
        }
        else if(ifstatementPending())
            tree = ifstatement();
        else if(varDefPending())
            tree = varDef();
        else if(functionDefPending())
            tree = functionDef();
        else if(functionCallPending())
            tree = functionCall();
        else if(whileLoopPending())
            tree = whileLoop();
        else
        {
            tree = returnCall();
        }
        return env.cons(STATEMENT, tree, null);
    }
    
    //STATEMENT PENDING
    private boolean statementPending() throws Exception
    {
        return exprPending() || ifstatementPending() || varDefPending() || functionDefPending() || whileLoopPending() ||
        returnCallPending() || functionCallPending();
    }
    
    /*STATEMENTS*/
    public lexeme statements() throws Exception
    {
        //System.out.println("STATEMENTS");
        lexeme tree = statement();
        lexeme temp = null;
        
        if(statementsPending())
            temp = statements();

        return env.cons(STATEMENT_LIST, tree, temp);
    }
    
    //STATEMENTS PENDING
    private boolean statementsPending() throws Exception
    {
        return statementPending();
    }
    
    /*BLOCK*/
    public lexeme block() throws Exception
    {
        //System.out.println("BLOCK");
        lexeme tree;
        
        match(OBRACE);
        tree = statements();
        match(CBRACE);

        return env.cons(BLOCK, tree, null);
    }
    
    //BLOCK PENDING
    private boolean blockPending() throws Exception
    {
        return(check(OBRACE));
    }
    
    /*CONDITIONAL*/
    public lexeme conditional() throws Exception
    {
        //System.out.println("CONDITIONAL");
        lexeme tree = null;
        lexeme temp = null;

        if(check(ID))
        {
            tree = match(ID);
        }
        else
        {
            tree = unary();
        }

        if(comparatorPending())
        {
            temp = comparator();
            return env.cons(temp.type, tree, conditional()); 
        }
        else
        {
            return tree;
        }
    }
    
    //CONDITIONAL PENDING
    private boolean conditionalPending() throws Exception
    {
        return exprPending() || check(NOT);
    }
    
    /*CONDITIONAL LIST*/
    public lexeme conditionalList() throws Exception
    {
        //System.out.println("CONDITIONAL_LIST");
        lexeme tree = conditional();
        lexeme temp = null;
        lexeme comp = null;

        if(conditionalListPending())
        {
            if(check(AND))
            {
                match(AND);
                if(check(AND) == false)
                {
                    errorHandler(new lexeme(ERROR, "MISSING SECOND &", current.line-1));
                }
                else
                {
                    comp = match(AND);
                }
            }
            else
            {
                match(OR);
                if(check(OR) == false)
                {
                    errorHandler(new lexeme(ERROR, "MISSING SECOND |", current.line-1));
                }
                else
                {
                    comp = match(OR);
                }
            }
            temp = conditionalList();
            return env.cons(comp.type, tree, env.cons(GLUE, temp, null));
        }
        else
            return tree;
    }
    
    //CONDITIONAL LIST PENDING
    private boolean conditionalListPending() throws Exception
    {
        return check(AND) || check(OR);
    }
    
    /*WHILE LOOP*/
    public lexeme whileLoop() throws Exception
    {
        //System.out.println("WHILE_LOOP");
        lexeme left;
        lexeme right;
        
        match(WHILE);
        match(OPAREN);
        left = conditionalList();
        match(CPAREN);
        right = block();

        return env.cons(WHILE_LOOP, left, right);
    }
    
    //WHILE LOOP PENDING
    private boolean whileLoopPending() throws Exception
    {
        return check(WHILE);
    }
    
    /*IFSTATEMENT*/
    public lexeme ifstatement() throws Exception
    {
        //System.out.println("IF_STATEMENT");
        lexeme left, right, temp;
        
        match(IF);
        match(OPAREN);
        left = conditionalList();
        match(CPAREN);
        right = block();
        temp = optElse();

        return env.cons(IF_STATEMENT, left, env.cons(GLUE, right, env.cons(GLUE, temp, null)));
    }
    
    //IF STATEMENT PENDING
    private boolean ifstatementPending() throws Exception
    {
        return check(IF);
    }
    
    /*OPTELSE*/
    public lexeme optElse() throws Exception
    {
        //System.out.println("OPT_ELSE");
        lexeme tree;
        
        if(optElsePending())
        {
            match(ELSE);
            if(ifstatementPending())
            {
                tree = ifstatement();
                return env.cons(ELSE_STATEMENT, tree, null);
            }
            else    
            {
                tree = block();
                return env.cons(ELSE_STATEMENT, tree, null);
            }
        }
        else    
            return null;
    }
    
    //OPTELSE PENDING
    private boolean optElsePending() throws Exception
    {
        return check(ELSE);
    }
    
    /*FUNCTIONDEF*/
    public lexeme functionDef() throws Exception
    {
        //System.out.println("FUNCTION_DEF");
        lexeme left, params, block;

        match(FUNCTION);
        left = match(ID);
        if(check(OSQUARE) == false)
        {
            errorHandler(new lexeme(ERROR, "FUNCTIONS MUST BE DECLARED WITH SQUARE BRACKETS", current.line-1));
        }
        match(OSQUARE);
        params = optParamList();
        match(CSQUARE);
        block = block();

        return env.cons(FUNC_DEF, left, env.cons(GLUE, params, block));
    }
    
    //FUNCTIONDEF PENDING
    private boolean functionDefPending() throws Exception
    {
        return check(FUNCTION);
    }
    
    /*FUNCTIONCALL*/       
    public lexeme functionCall() throws Exception
    {
        //System.out.println("FUNCTION_CALL");
        lexeme tree;

        match(OSQUARE);
        tree = optArgList();
        match(CSQUARE);

        return tree;
    }
    
    //FUNCTION CALL PENDING
    private boolean functionCallPending() throws Exception
    {
        return check(OSQUARE);
    }

    /*LAMBDA FUNCTION DEF*/
    public lexeme lambdaDef() throws Exception
    {
        match(LAMBDA);
        lexeme tree = functionCall();
        lexeme blck = block();
        return env.cons(LAMBDA_DEF, null, env.cons(GLUE, tree, blck));
    }

    //LAMBDA DEF PENDING
    private boolean lambdaPending() throws Exception
    {
        return check(LAMBDA);
    }

    /*LAMBDA FUNCTION CALL*/
    public lexeme lambdaCall() throws Exception
    {
        match(LAMBDA);
        lexeme args = functionCall();
        return env.cons(LAMBDA_CALL, null, args);
    }
    
    /*RETURNCALL*/
    public lexeme returnCall() throws Exception
    {
        //System.out.println("RETURN_CALL");
        lexeme tree;
        
        match(RETURN);
        if(exprPending())
        {
            tree = expr();
            match(SEMICOLON);
            return env.cons(RETURN_STATE, tree, null);
        }
        else if(functionCallPending())
        {
            tree = functionCall();
            match(SEMICOLON);
            return env.cons(RETURN_STATE, tree, null);
        }
        else
        {
            match(SEMICOLON);
            return env.cons(RETURN_STATE, null, null);
        }
    }
    
    //RETURNCALL PENDING
    private boolean returnCallPending() throws Exception
    {
        return check(RETURN);
    }

    /*ERROR HANDLER*/
    private void errorHandler(lexeme err)
    {
        System.out.println("ERROR - "+err.sval+", on line: "+err.line);
        System.exit(1);        
    }
}//ends class

