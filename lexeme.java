/* Friday, February 22nd
 * Jacoby Benger
 * CS 403 - Programming Languages 
 * 
 * LEXEME.JAVA
 */
import java.util.ArrayList;
import java.util.Scanner;

public class lexeme implements types
{
    String varName;
    String type;
    int ival;        //INTEGER
    double rval;     //REAL
    String sval;     //STRING
    boolean bval;    //BOOLEAN
    int line;        //LINENUMBER
    char cval;
    Scanner fval;


    //ADDED FOR ENVIRONMENT MODULE
    lexeme left;
    lexeme right;
    ArrayList<lexeme> aval;

    /***** CONSTRUCTORS *****/
    public lexeme(String type, lexeme l, lexeme r)
    {
        this.type = type;
        this.left = l;
        this.right = r;
        this.aval = null;
        this.fval = null;
    }

    /*INTEGER LEXEMES*/
    public lexeme(String type, int intdata, int line)
    {
        this.type = type;
        this.ival = intdata;
        this.line = line;
        this.left = null;
        this.right = null;
        this.aval = null;
        this.fval = null;
    }

    /*REAL LEXEMES*/
    public lexeme(String type, double realdata, int line)
    {
        this.type = type;
        this.rval = realdata;
        this.line = line;
        this.left = null;
        this.right = null;
        this.aval = null;
        this.fval = null;
    }

    /*STRING LEXEMES*/
    public lexeme(String type, String sdata, int line)
    {
        this.type = type;
        this.sval = sdata;
        this.line = line;
        this.left = null;
        this.right = null;
        this.aval = null;
        this.fval = null;
    }

    /*BOOLEAN LEXEMES*/
    public lexeme(String type, boolean booldata, int line)
    {
        this.type = type;
        this.bval = booldata;
        this.line = line;
        this.left = null;
        this.right = null;
        this.aval = null;
        this.fval = null;
    }
    
    /*OPERATOR / COMPARISON LEXEMES*/
    public lexeme(String type, int line)
    {
        switch(type)
        {
            case(OPAREN)     : this.type = "OPAREN"; break;
            case(CPAREN)     : this.type = "CPAREN"; break;
            case(OBRACE)     : this.type = "OBRACE"; break;
            case(CBRACE)     : this.type = "CBRACE"; break;
            case(OSQUARE)    : this.type = "OSQUARE"; break;
            case(CSQUARE)    : this.type = "CSQUARE"; break;
            case(COMMA)      : this.type = "COMMA"; break;
            case(PLUS)       : this.type = "PLUS"; break;
            case(MINUS)      : this.type = "MINUS"; break;
            case(TIMES)      : this.type = "TIMES"; break;
            case(DIVIDES)    : this.type = "DIVIDES"; break;
            case(MOD)        : this.type = "MOD"; break;
            case(EQUAL)      : this.type = "EQUAL"; break;
            case(NOTEQUAL)   : this.type = "NOTEQUAL"; break;
            case(LESSTHAN)   : this.type = "LESSTHAN"; break;
            case(GREATTHAN)  : this.type = "GREATTHAN"; break; 
            case(LESSEQUAL)  : this.type = "LESSEQUAL"; break;
            case(GREATEQUAL) : this.type = "GREATEQUAL"; break;
            case(ASSIGN)     : this.type = "ASSIGN"; break;
            case(SEMICOLON)  : this.type = "SEMICOLON"; break;
            case(NOT)        : this.type = "NOT"; break;
            case(WHILE)      : this.type = "WHILE"; break;
            case(IF)         : this.type = "IF"; break;
            case(ELSE)       : this.type = "ELSE"; break;
            case(ID)         : this.type = "ID"; break;
            case(VAR_TYPE)   : this.type = "VAR_TYPE"; break;
            case(END_FILE)   : this.type = "END_FILE"; break;
            
            case(PRINT)      : this.type = "PRINT"; break;
            case(PRINTLN)    : this.type = "PRINTLN"; break;
            case(AND)        : this.type = "AND"; break;
            case(OR)         : this.type = "OR"; break;
            case(FUNCTION)   : this.type = "FUNCTION"; break;
            case(LAMBDA)     : this.type = "LAMBDA"; break;
            case(RETURN)     : this.type = "RETURN"; break;

            case(CLASS)      : this.type = "CLASS"; break;
            case(DOT)        : this.type = "DOT"; break;

            default          : System.out.println("YOU HAVE NOT YET IMPLEMENTED - " + type); break;
        }
        this.line = line;
        this.left = null;
        this.right = null;
        this.aval = null;
        this.fval = null;
    }
    
    /*UNKOWN LEXEME*/
    public lexeme(String type, char val, int line)
    {
        this.type = type;
        this.cval = val; 
        this.line = line;
    }
}
