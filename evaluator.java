/* Friday, February 2nd 
 * Jacoby Benger
 * CS 403 - Programming Languages 
 * 
 * EVALUATOR.JAVA
 */

import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;;

public class evaluator implements types
{
    private environment env; //global env

    //Globals for Command Line
    private static int countCL;
    private static String argsCL[];

    public evaluator() throws Exception
    {
        env = new environment();
    }

    /*MAIN EVALUATING FUNCTION*/
    public lexeme eval(lexeme tree, lexeme e) throws Exception
    {
        if(tree == null)
        {
            //System.out.println("TREE IS NULL");
            return null;
        }
        else if(e == null)
        {
            //System.out.println("ENVIRONMENT IS NULL");
            return null;
        }

        switch(tree.type)
        {
            //Self Evaluating 
            case(INTEGER)       :   return tree;
            case(REAL)          :   return tree;
            case(STRING)        :   return tree;
            case(BOOLEAN)       :   return tree;
            case(UMINUS)        :   return evalUminus(tree, e);

            //IDs
            case(ID)            :   return env.getVal(e, tree);
            case(INIT)          :   return evalInit(tree, e);

            //Operators + Comparators
            case(PLUS)          :   return evalSimpleOp(tree, e);
            case(MINUS)         :   return evalSimpleOp(tree, e);
            case(TIMES)         :   return evalSimpleOp(tree, e);
            case(DIVIDES)       :   return evalSimpleOp(tree, e);
            case(MOD)           :   return evalSimpleOp(tree, e);
            case(EQUAL)         :   return evalSimpleOp(tree, e);
            case(NOTEQUAL)      :   return evalSimpleOp(tree, e);
            case(LESSTHAN)      :   return evalSimpleOp(tree, e);
            case(GREATTHAN)     :   return evalSimpleOp(tree, e);
            case(LESSEQUAL)     :   return evalSimpleOp(tree, e);
            case(GREATEQUAL)    :   return evalSimpleOp(tree, e);
            case(INC)           :   return evalInc(tree, e);
            case(DEC)           :   return evalDec(tree, e);
            case(DOT)           :   return evalDot(tree, e);

            case(AND)           :   return evalShortCircuitOp(tree, e);
            case(OR)            :   return evalShortCircuitOp(tree, e);

            //Lists
            case(ARG_LIST)      :   return evalArgList(tree, e);

            //Variables and Statements
            case(BLOCK)         :   return evalBlock(tree, e);
            case(VARDEF)        :   return evalVarDef(tree, e);
            case(PAREN_EXPR)    :   return evalParenExpr(tree, e);
            case(WHILE_LOOP)    :   return evalWhile(tree, e);
            case(STATEMENT)     :   return evalStatement(tree, e);
            case(STATEMENT_LIST):   return evalStatementList(tree, e);
            case(IF_STATEMENT)  :   return evalIf(tree, e);
            case(ELSE_STATEMENT):   return evalElse(tree, e);
            case(RETURN_STATE)  :   return evalReturn(tree, e);
            case(PRINTLN_STATE) :   evalPrintln(tree, e); break;
            case(PRINT_STATE)   :   evalPrint(tree, e); break;    //print does not return a lexeme

            //Functions
            case(FUNC_DEF)      :   return evalFuncDef(tree, e); 
            case(FUNC_CALL)     :   return evalFuncCall(tree, e);
            case(LAMBDA_DEF)    :   return evalLambdaDef(tree, e);

            //BuiltIns          
            case(ARRAY)         :   return tree;
            case(FILE_POINT)    :   return tree;
            
            //Top Level
            case(PROGRAM)       :   return evalProgram(tree, e);
            case(DEFINITION)    :   return evalDefinition(tree, e);
            case(CLASS)         :   return evalClass(tree, e);

            case(ERROR)         :   evalError(tree); break; //exits out of program
            

            default             :
                System.out.println("ERROR in EVAL - unknown expression: " + tree.type);
                return new lexeme(ERROR, "UNKNOWN EXPRESSION: "+tree.type, tree.line);
        }//ends switch 
        return null;
    }

    /*EVAL PROGRAM*/
    private lexeme evalProgram(lexeme tree, lexeme e) throws Exception
    {
        //System.out.println("EVAL PROGRAM");

        while(tree != null)
        {
            eval(tree.left, e);
            tree = tree.right;
        }
        return null; // <---- I dont think I need to return anything right here?
    }

    /*EVAL DEFINITION*/
    private lexeme evalDefinition(lexeme tree, lexeme e) throws Exception
    {
        //System.out.println("EVAL DEFINITION");
        return eval(tree.left, e);
    }

    /*EVAL CLASS*/
    private lexeme evalClass(lexeme tree, lexeme e) throws Exception 
    {
        //System.out.println("EVAL CLASS");
        env.insert(e, tree.left, env.cons(OCLOSURE, e, tree));
        return eval(tree.right, e);
    }

    /*EVAL FUNCTION DEFINITION*/
    private lexeme evalFuncDef(lexeme tree, lexeme e) throws Exception 
    {
        //System.out.println("EVAL FUNC DEF");
        lexeme close = env.cons(CLOSURE, e, tree);
        return env.insert(e, tree.left, close);
    }

    /*EVAL FUNCTION CALL*/
    private lexeme evalFuncCall(lexeme tree, lexeme e) throws Exception
    {
        //System.out.println("EVAL FUNC CALL");

        lexeme name = tree.left;
        lexeme args = getCallArgs(tree);
        lexeme eargs = eval(args, e);

        if(name.sval.equals("newArray"))
            return evalNewArray(eargs, e);
        else if(name.sval.equals("setArray"))
            return evalSetArray(eargs, e);
        else if(name.sval.equals("getArray"))
            return evalGetArray(eargs, e);
        else if(name.sval.equals("openFileForReading"))
            return evalOpenFile(eargs, e);
        else if(name.sval.equals("readInteger"))
            return evalReadInteger(eargs, e);
        else if(name.sval.equals("atFileEnd"))
            return evalAtFileEnd(eargs, e);
        else if(name.sval.equals("closeFile"))
            return evalCloseFile(eargs, e);
        else if(name.sval.equals("getArgCount"))
            return evalGetArgCount(eargs, e);
        else if(name.sval.equals("getArg"))
            return evalGetArg(eargs, e);
        else
        {
        lexeme close = eval(tree.left, e);
        lexeme senv = getClosureEnv(close);
        lexeme params = getParams(close);
        lexeme xenv = env.extend(params, eargs, senv);
        lexeme body = getClosureBody(close);
        
        //points to extended environment
        env.insert(xenv, new lexeme(ID, "this", 0), xenv);

        return eval(body, xenv);
        }
    }
    //FUNCTION CALL HELPERS
    private lexeme getParams(lexeme close) throws Exception
    {
        //System.out.println("GETTING PARAMS");
        return close.right.right.left;
    }
    private lexeme getCallArgs(lexeme tree) throws Exception
    {
        //System.out.println("GETTING ARGS");
        return tree.right;
    }
    private lexeme getClosureBody(lexeme close) throws Exception
    {
        return close.right.right.right; //should work
    }
    private lexeme getClosureEnv(lexeme close) throws Exception
    {
        return close.left;
    }

    /*EVAL ARG LIST*/
    private lexeme evalArgList(lexeme tree, lexeme e) throws Exception
    {
        //System.out.println("EVAL ARG_LIST");
        tree.left = eval(tree.left, e);
        return tree;

    }

    /*EVAL LAMBDA DEF*/
    private lexeme evalLambdaDef(lexeme tree, lexeme e) throws Exception
    {
        return env.cons(CLOSURE, e, tree);
    }

    /*EVAL VAR_DEF*/
    private lexeme evalVarDef(lexeme tree, lexeme e) throws Exception
    {
        //System.out.println("EVAL VARDEF");

        lexeme id = tree.left; 

        lexeme value = eval(tree.right, e);
        return env.insert(e, id, value);
    }

    /*EVAL BLOCK*/
    private lexeme evalBlock(lexeme tree, lexeme e) throws Exception
    {
        //System.out.println("EVAL BLOCK");

        lexeme evalBlock = null;

        while(tree != null)
        {
            evalBlock = eval(tree.left, e); //THIS SHOULD GRAB THE STATEMENT_LIST LEXEME
            tree = tree.right;
        }
        return evalBlock;
    }

    /*EVAL STATEMENT*/
    private lexeme evalStatement(lexeme tree, lexeme e) throws Exception
    {
        //System.out.println("EVAL STATEMENT");
        return eval(tree.left, e);
    }

    /*EVAL STATEMENT_LIST*/
    private lexeme evalStatementList(lexeme tree, lexeme e) throws Exception
    {
        //System.out.println("EVAL STATEMENT LIST");
        lexeme evalList = null;
        while(tree != null)
        {
            evalList = eval(tree.left, e);
            tree = tree.right;
        }
        return evalList;
    }

    /*EVAL DOT*/
    private lexeme evalDot(lexeme tree, lexeme e) throws Exception
    {
        lexeme obj = eval(tree.left, e);
        return eval(tree.right, obj);
    }

    /*EVAL ASSIGN*/
    private lexeme evalInit(lexeme tree, lexeme e) throws Exception
    {
        //System.out.println("EVAL INIT");

        lexeme right = eval(tree.right, e);
        if(tree.left == null)
        {
            return right;
        }        
        else if(tree.left.type == DOT)
        {
            lexeme obj = eval(tree.left.left, e);
            return env.update(obj, tree.left.right, right);
        }
        else
        {
            return env.update(e, tree.left, right);
        }
    }

    /*EVAL PAREN_EXPR*/
    private lexeme evalParenExpr(lexeme tree, lexeme e) throws Exception
    {
        //System.out.println("EVAL PAREN EXPR");
        return eval(tree.left, e);
    } 

    /*EVAL SHORT CIRCUIT OP*/
    private lexeme evalShortCircuitOp(lexeme tree, lexeme e) throws Exception
    {
        //System.out.println("EVAL SHORT CIRUIT");
        if(tree.type == AND)
            return evalAnd(tree, e);
        else
            return evalOr(tree, e);
    }

    private lexeme evalAnd(lexeme tree, lexeme e) throws Exception
    {
        lexeme left = eval(tree.left, e);
        lexeme right = null;
        if(tree.right.type == GLUE)
            right = eval(tree.right.left, e);
        else
            right = eval(tree.right, e);

        if(left.bval == true && right.bval == true)
            return new lexeme(BOOLEAN, true, left.line);
        else
            return new lexeme(BOOLEAN, false, left.line);
    }

    private lexeme evalOr(lexeme tree, lexeme e) throws Exception
    {
        lexeme left = eval(tree.left, e);
        lexeme right = null;
        if(tree.right.type == GLUE)
            right = eval(tree.right.left, e);
        else
            right = eval(tree.right, e);

        if(left.bval == true || right.bval == true)
            return new lexeme(BOOLEAN, true, left.line);
        else
            return new lexeme(BOOLEAN, false, left.line);
    }

    /*EVAL WHILE LOOP*/
    private lexeme evalWhile(lexeme tree, lexeme e) throws Exception
    {
        //System.out.println("EVAL WHILE");
        lexeme result = null;
        while(eval(tree.left, e).bval == true)
        {
            result = eval(tree.right, e);
        }
        return result;
    }

    /*EVAL IF STATEMENT*/
    private lexeme evalIf(lexeme tree, lexeme e) throws Exception
    {
        //System.out.println("EVAL IF");
        lexeme conds = eval(tree.left, e);

        if(conds.bval == true)
            return eval(tree.right.left, e);
        else    
            return eval(tree.right.right.left, e); //this should eval the optElse
    }

    /*EVAL ELSE STATEMENT*/
    private lexeme evalElse(lexeme tree, lexeme e) throws Exception
    {
        //System.out.println("EVAL ELSE");
        return eval(tree.left, e);
    }

    /*EVAL SIMPLE OP*/
    private lexeme evalSimpleOp(lexeme tree, lexeme e) throws Exception
    {
        //System.out.println("EVAL SIMPLE OP");
        switch(tree.type)
        {
            case(PLUS)      : return evalPlus(tree, e);
            case(MINUS)     : return evalMinus(tree, e);
            case(TIMES)     : return evalTimes(tree, e);
            case(DIVIDES)   : return evalDivide(tree, e);
            case(MOD)       : return evalMod(tree, e);

            //Comparators
            case(EQUAL)     : return evalEqual(tree, e);
            case(NOTEQUAL)  : return evalNotEqual(tree, e);
            case(LESSTHAN)  : return evalLessThan(tree, e);
            case(GREATTHAN) : return evalGreatThan(tree, e);
            case(LESSEQUAL) : return evalLessEqual(tree, e);
            case(GREATEQUAL): return evalGreatEqual(tree, e);

            default     :
                return new lexeme(ERROR, "ILLEGAL OPERATOR: "+tree.type, tree.line);
        }
    }

    /*SIMPLE OPERATION AND COMPARATOR EVAL FUNCTIONS*/
    private lexeme evalPlus(lexeme tree, lexeme e) throws Exception
    {
        //System.out.println("EVAL PLUS");

        lexeme left = eval(tree.left, e);
        lexeme right = eval(tree.right, e);

        if(left.type == INTEGER && right.type == INTEGER)
            return new lexeme(INTEGER, left.ival + right.ival, left.line);
        else if(left.type == INTEGER && right.type == REAL)
            return new lexeme(REAL, left.ival + right.rval, left.line);
        else if(left.type == REAL && right.type == INTEGER)
            return new lexeme(REAL, left.rval + right.ival, left.line);
        else if(left.type == REAL && right.type == REAL)
            return new lexeme(REAL, left.rval + right.rval, left.line);
        else
        {
            evalError(new lexeme(ERROR, "ILLEGAL ADDITION, cannot add " + left.type + " + " + right.type, tree.line));
            return new lexeme(ERROR, "ILLEGAL ADDITION, cannot add " + left.type + " + " + right.type, tree.line);
        }
    }

    //I think this needs an IF_STATEMENT to handle UNMINUS or does it because we are not appending to tree
    private lexeme evalMinus(lexeme tree, lexeme e) throws Exception
    {
        //System.out.println("EVAL MINUS");

        lexeme left = eval(tree.left, e);
        lexeme right = eval(tree.right, e);

        if(left.type == INTEGER && right.type == INTEGER)
            return new lexeme(INTEGER, left.ival - right.ival, left.line);
        else if(left.type == INTEGER && right.type == REAL)
            return new lexeme(REAL, left.ival - right.rval, left.line);
        else if(left.type == REAL && right.type == INTEGER)
            return new lexeme(REAL, left.rval - right.ival, left.line);
        else if(left.type == REAL && right.type == REAL)
            return new lexeme(REAL, left.rval - right.rval, left.line);
        else
        {
            evalError(new lexeme(ERROR, "ILLEGAL SUBTRACTION, cannot sub " + left.type + " - " + right.type, tree.line));
            return new lexeme(ERROR, "ILLEGAL SUBTRACTION, cannot sub " + left.type + " - " + right.type, tree.line);
        }
    }

    private lexeme evalTimes(lexeme tree, lexeme e) throws Exception
    {
        //System.out.println("EVAL MINUS");

        lexeme left = eval(tree.left, e);
        lexeme right = eval(tree.right, e);

        if(left.type == INTEGER && right.type == INTEGER)
            return new lexeme(INTEGER, left.ival * right.ival, left.line);
        else if(left.type == INTEGER && right.type == REAL)
            return new lexeme(REAL, left.ival * right.rval, left.line);
        else if(left.type == REAL && right.type == INTEGER)
            return new lexeme(REAL, left.rval * right.ival, left.line);
        else if(left.type == REAL && right.type == REAL)
            return new lexeme(REAL, left.rval * right.rval, left.line);
        else
        {
            evalError(new lexeme(ERROR, "ILLEGAL MULTIPLICATION, cannot multiply " + left.type + " * " + right.type, tree.line));
            return new lexeme(ERROR, "ILLEGAL MULTIPLICATION, cannot multiply " + left.type + " * " + right.type, tree.line);
        }
    }
    private lexeme evalDivide(lexeme tree, lexeme e) throws Exception
    {
        //System.out.println("EVAL DIVIDE");

        lexeme left = eval(tree.left, e);
        lexeme right = eval(tree.right, e);

        if(left.type == INTEGER && right.type == INTEGER)
            return new lexeme(INTEGER, left.ival / right.ival, left.line);
        else if(left.type == INTEGER && right.type == REAL)
            return new lexeme(REAL, left.ival / right.rval, left.line);
        else if(left.type == REAL && right.type == INTEGER)
            return new lexeme(REAL, left.rval / right.ival, left.line);
        else if(left.type == REAL && right.type == REAL)
            return new lexeme(REAL, left.rval / right.rval, left.line);
        else
        {
            evalError(new lexeme(ERROR, "ILLEGAL DIVISION, cannot divide " + left.type + " / " + right.type, tree.line));
            return new lexeme(ERROR, "ILLEGAL DIVISION, cannot divide " + left.type + " / " + right.type, tree.line);
        }
    }

    private lexeme evalMod(lexeme tree, lexeme e) throws Exception
    {
        //System.out.println("EVAL MOD");

        lexeme left = eval(tree.left, e);
        lexeme right = eval(tree.right, e);

        if(left.type == INTEGER && right.type == INTEGER)
            return new lexeme(INTEGER, left.ival % right.ival, left.line);
        else
        {
            evalError(new lexeme(ERROR, "ILLEGAL DIVISION, cannot mod " + left.type + " % " + right.type, tree.line));
            return new lexeme(ERROR, "ILLEGAL DIVISION, cannot mod " + left.type + " % " + right.type, tree.line);
        }
    }

    /*INCREMENTAION AND DECREMENTATION*/
    private lexeme evalInc(lexeme tree, lexeme e) throws Exception
    {
        lexeme var = tree.left;
        lexeme val = eval(tree.left, e);
        if(val.type != INTEGER)
        {
            evalError(new lexeme(ERROR, "CAN ONLY INCREMENT VARIABLES OF TYPE: INTEGER", tree.line));
            return new lexeme(ERROR, "CAN ONLY INCREMENT VARIABLES OF TYPE: INTEGER", tree.line);
        }
        val.ival++;
        return env.update(e, var, val);
    }

    private lexeme evalDec(lexeme tree, lexeme e) throws Exception
    {
        lexeme var = tree.left;
        lexeme val = eval(tree.left, e);
        if(val.type != INTEGER)
        {
            evalError(new lexeme(ERROR, "CAN ONLY DECREMETN VARIABLES OF TYPE: INTEGER", tree.line));
            return new lexeme(ERROR, "CAN ONLY DECREMETN VARIABLES OF TYPE: INTEGER", tree.line);
        }
        val.ival--;
        return env.update(e, var, val);
    }

    //COMPARATORS
    private lexeme evalEqual(lexeme tree, lexeme e) throws Exception
    {
        //System.out.println("EVAL EQUAL");

        lexeme left = eval(tree.left, e);
        lexeme right = eval(tree.right, e);

        if(left.type == INTEGER && right.type == INTEGER)
            return new lexeme(BOOLEAN, left.ival == right.ival, left.line);
        else if(left.type == REAL && right.type == REAL)
            return new lexeme(BOOLEAN, left.rval == right.rval, left.line);
        else if(left.type == STRING && right.type == STRING)
            return new lexeme(BOOLEAN, left.sval.equals(right.sval), left.line);
        else if(left.type == BOOLEAN && right.type == BOOLEAN)
            return new lexeme(BOOLEAN, left.bval == right.bval, left.line);
        else
        {
            evalError(new lexeme(ERROR, "ILLEGAL COMPARISON, cannot compare types " + left.type + ", " + right.type, tree.line));
            return new lexeme(ERROR, "ILLEGAL COMPARISON, cannot compare types" + left.type + ", " + right.type, tree.line);
        }
    }
    private lexeme evalNotEqual(lexeme tree, lexeme e) throws Exception
    {
        lexeme left = eval(tree.left, e);
        lexeme right = eval(tree.right, e);

        if(left.type == INTEGER && right.type == INTEGER)
            return new lexeme(BOOLEAN, left.ival != right.ival, left.line);
        else if(left.type == REAL && right.type == REAL)
            return new lexeme(BOOLEAN, left.rval != right.rval, left.line);
        else if(left.type == STRING && right.type != STRING)
            return new lexeme(BOOLEAN, !(left.sval.equals(right.sval)), left.line);
        else if(left.type == BOOLEAN && right.type == BOOLEAN)
            return new lexeme(BOOLEAN, left.bval != right.bval, left.line);
        else
        {
            evalError(new lexeme(ERROR, "ILLEGAL COMPARISON, cannot compare types " + left.type + ", " + right.type, tree.line));
            return new lexeme(ERROR, "ILLEGAL COMPARISON, cannot compare types" + left.type + ", " + right.type, tree.line);

        }
    }
    private lexeme evalLessThan(lexeme tree, lexeme e) throws Exception
    {
        //System.out.println("EVAL LESS THAN");

        lexeme left = eval(tree.left, e);
        lexeme right = eval(tree.right, e);

        if(left.type == INTEGER && right.type == INTEGER)
            return new lexeme(BOOLEAN, left.ival < right.ival, left.line);
        else if(left.type == REAL && right.type == REAL)
            return new lexeme(BOOLEAN, left.rval < right.rval, left.line);
        else
        {
            evalError(new lexeme(ERROR, "ILLEGAL COMPARISON, cannot compare types " + left.type + ", " + right.type, tree.line));
            return new lexeme(ERROR, "ILLEGAL COMPARISON, cannot compare types" + left.type + ", " + right.type, tree.line);
        }
    }
    private lexeme evalGreatThan(lexeme tree, lexeme e) throws Exception
    {
        //System.out.println("EVAL GREAT THAN");

        lexeme left = eval(tree.left, e);
        lexeme right = eval(tree.right, e);

        if(left.type == INTEGER && right.type == INTEGER)
            return new lexeme(BOOLEAN, left.ival > right.ival, left.line);
        else if(left.type == REAL && right.type == REAL)
            return new lexeme(BOOLEAN, left.rval > right.rval, left.line);
        else
        {
            evalError(new lexeme(ERROR, "ILLEGAL COMPARISON, cannot compare types " + left.type + ", " + right.type, left.line));
            return new lexeme(ERROR, "ILLEGAL COMPARISON, cannot compare types" + left.type + ", " + right.type, tree.line);
        }
    }
    private lexeme evalLessEqual(lexeme tree, lexeme e) throws Exception
    {
        //System.out.println("EVAL LESS EQUAL");

        lexeme left = eval(tree.left, e);
        lexeme right = eval(tree.right, e);

        if(left.type == INTEGER && right.type == INTEGER)
            return new lexeme(BOOLEAN, left.ival <= right.ival, left.line);
        else if(left.type == REAL && right.type == REAL)
            return new lexeme(BOOLEAN, left.rval <= right.rval, left.line);
        else
        {
            evalError(new lexeme(ERROR, "ILLEGAL COMPARISON, cannot compare types " + left.type + ", " + right.type, tree.line));
            return new lexeme(ERROR, "ILLEGAL COMPARISON, cannot compare types" + left.type + ", " + right.type, tree.line);
        }
    }
    private lexeme evalGreatEqual(lexeme tree, lexeme e) throws Exception
    {
        //System.out.println("EVAL GREAT EQUAL");

        lexeme left = eval(tree.left, e);
        lexeme right = eval(tree.right, e);

        if(left.type == INTEGER && right.type == INTEGER)
            return new lexeme(BOOLEAN, left.ival >= right.ival, left.line);
        else if(left.type == REAL && right.type == REAL)
            return new lexeme(BOOLEAN, left.rval >= right.rval, left.line);
        else
        {
            evalError(new lexeme(ERROR, "ILLEGAL COMPARISON, cannot compare types " + left.type + ", " + right.type, tree.line));
            return new lexeme(ERROR, "ILLEGAL COMPARISON, cannot compare types" + left.type + ", " + right.type, tree.line);
        }
    }

    /*EVAL UMINUS*/
    private lexeme evalUminus(lexeme tree, lexeme e) throws Exception
    {
        lexeme val = eval(tree.left, e);

        if(val.type == INTEGER)
        {
            int neg = (-val.ival);
            return new lexeme(INTEGER, neg, val.line);
        }
        else if(val.type == REAL)
        {
            double neg = (-val.rval);
            return new lexeme(REAL, neg, val.line);
        }
        else
        {
            evalError(new lexeme(ERROR, "Cannot make type: "+val.type+" a negative", val.line));
            return new lexeme(ERROR, "Cannot make type: "+val.type+" a negative", val.line);
        }
    }

    /*EVAL RETURN STATEMENT*/   
    private lexeme evalReturn(lexeme tree, lexeme e) throws Exception
    {
        //System.out.println("EVAL RETURN");
        return eval(tree.left, e);
    }

    /*EVAL PRINT*/
    private void evalPrint(lexeme tree, lexeme e) throws Exception
    {
        //System.out.println("EVAL PRINT");

        lexeme list = eval(tree.left, e);

        while(list != null)
        {
            lexeme out = eval(list.left, e); 
            if(out == null)
                System.out.print("printing null");
            else
            {
                if(out.type == INTEGER)
                    System.out.print(out.ival);
                else if(out.type == REAL)
                    System.out.print(out.rval);
                else if(out.type == BOOLEAN)
                    System.out.print(out.bval);
                else 
                    System.out.print(out.sval);
            }
            list = list.right;
        }
    }

    /*EVAL PRINT*/
    private void evalPrintln(lexeme tree, lexeme e) throws Exception
    {
        //System.out.println("EVAL PRINT LINE");
    
        lexeme list = eval(tree.left, e);

        while(list != null)
        {
            lexeme out = eval(list.left, e); 
            if(out == null)
                System.out.print("printing null");
            else
            {
                if(out.type == INTEGER)
                    System.out.print(out.ival);
                else if(out.type == REAL)
                    System.out.print(out.rval);
                else if(out.type == BOOLEAN)
                    System.out.print(out.bval);
                else 
                    System.out.print(out.sval);
            }
            list = list.right;
        }
        System.out.println(); //adds the newline
    }

    /*EVAL ERRORS*/
    public void evalError(lexeme tree)
    {
        System.out.println("ERROR - "+tree.sval+", on line: "+tree.line);
        System.exit(1);
    }

    //Strictly here for debugging 
    public void inorder(lexeme tree) throws Exception
    {
        if(tree == null)
        {
            System.out.println("lexeme is null");
            return;
        }

        System.out.println(tree.type);
        inorder(tree.left);
        inorder(tree.right);
    }

    /***********BUILT IN FUNCTION SECTION***********/
    private lexeme evalNewArray(lexeme args, lexeme e) throws Exception
    {
        if(length(args) != 1)
            return new lexeme(ERROR, "newArray() only accepts one argument", args.line);
        else if(args.left.type != INTEGER)
            return new lexeme(ERROR, "newArray() only accepts one INTEGER argument", args.line);

        int size = args.left.ival;
        lexeme a = new lexeme(ARRAY, null, null);
        ArrayList<lexeme> list = new ArrayList<>();

        

        for(int x = 0; x < size; x++)
            list.add(null);

        a.aval = list;
        //System.out.println("CREATING NEW ARRAY with size: "+ list.size());
        return a;
    }

    private lexeme evalGetArray(lexeme args, lexeme e) throws Exception
    {
        if(length(args) != 2)
            return new lexeme(ERROR, "getArray() only accepts arguments of type ID and INTEGER", args.line);
        else if(args.left.aval == null)
            return new lexeme(ERROR, "ARRAY HAS NOT BEEN INITIALIZED", args.line);
        
        lexeme a = args.left;

        int i = eval(args.right.left, e).ival;

        lexeme result = a.aval.get(i);
        return result;
    }

    private lexeme evalSetArray(lexeme args, lexeme e) throws Exception
    {
        //System.out.println("SETTING ARRAY");
        if(length(args) != 3)
            return new lexeme(ERROR, "setArray() only accepts three arguments", args.line);
        
        
        if(args.left.aval == null)
            System.out.println("the array pointer is null");
        lexeme a = args.left;
        int i = eval(args.right.left, e).ival;
        lexeme val = eval(args.right.right.left, e);
        //System.out.println("SETTING INDEX: "+i+" with value: "+val.ival);

        a.aval.set(i, val);
        return val;
    }

    private int length(lexeme list) throws Exception
    {
        int x = 0;
        while(list != null)
        {
            x++;
            list = list.right;
        }
        return x;
    }

    //FILE I/O
    private lexeme evalOpenFile(lexeme args, lexeme e) throws Exception
    {
        if(length(args) != 1)
            return new lexeme(ERROR, "openFileForReading() only accepts one argument", args.line);

        String fname = eval(args.left, e).sval;
        lexeme fp = new lexeme(FILE_POINT, null, null);

        try
        {
            fp.fval = new Scanner(new File(fname));
        }
        catch(Exception ex)
        {
            throw new NullPointerException("Unable to open file: " + fname);
        }
        return fp;
    }

    private lexeme evalReadInteger(lexeme args, lexeme e) throws Exception
    {
        if(length(args) != 1)
            return new lexeme(ERROR, "readInteger() only accepts one argument", args.line);  
            
        lexeme in = args.left;
        int val = in.fval.nextInt();
        //System.out.println("READ VALUE - "+val);
        return new lexeme(INTEGER, val, 0);
    }

    private lexeme evalAtFileEnd(lexeme args, lexeme e) throws Exception
    {
        if(length(args) != 1)
            return new lexeme(ERROR, "atFileEnd() only accepts one argument", args.line);    
            
        lexeme fp = args.left;
        if(fp.fval.hasNext())
            return new lexeme(BOOLEAN, false, 0);
        else    
            return new lexeme(BOOLEAN, true, 0);
    }

    private lexeme evalCloseFile(lexeme args, lexeme e) throws Exception
    {
        if(length(args) != 1)
            return new lexeme(ERROR, "closeFile() only accepts one argument", args.line);  
            
        lexeme fp = args.left;
        fp.fval.close();

        return new lexeme(BOOLEAN, true, 0);
    }

    //Command Line
    private lexeme evalGetArgCount(lexeme args, lexeme e) throws Exception
    {
        return new lexeme(INTEGER, countCL, 0);
    }

    private lexeme evalGetArg(lexeme args, lexeme e) throws Exception
    {
        int index = eval(args, e).ival;
        return new lexeme(STRING, argsCL[index+1], 0);
    }

    public static void main(String args[])
    {
        countCL = args.length - 1;
        argsCL = args;
        parser par = new parser(args[0]);
        environment ev = new environment();
        lexeme x = ev.newEnvironment();
        try
        {
            evaluator temp = new evaluator();
            lexeme prog = par.parse();

            //temp.inorder(prog);
            temp.eval(prog, x);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}//ends class
