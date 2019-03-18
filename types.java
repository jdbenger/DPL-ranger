/* Friday, February 22nd 
 * Jacoby Benger
 * CS 403 - Programming Languages 
 * 
 * TYPES.JAVA
 */

public interface types
{
    /* VARIABLE TYPES */
    public static final String ID = "ID";
    public static final String INTEGER = "INTEGER";
    public static final String REAL = "REAL";
    public static final String STRING = "STRING";
    public static final String BOOLEAN = "BOOLEAN";
    public static final String VAR_TYPE = "VAR_TYPE";
    public static final String UMINUS = "UMINUS";

    /* OPERATOR & COMPARATOR */
    public static final String PLUS = "PLUS";
    public static final String MINUS = "MINUS";
    public static final String TIMES = "TIMES";
    public static final String DIVIDES = "DIVIDES";
    public static final String MOD = "MOD";
    public static final String EQUAL = "EQUAL";
    public static final String NOTEQUAL = "NOTEQUAL";
    public static final String LESSTHAN = "LESSTHAN";
    public static final String GREATTHAN = "GREATTHAN";
    public static final String LESSEQUAL = "LESSEQUAL";
    public static final String GREATEQUAL = "GREATEQUAL";
    public static final String AND = "AND";
    public static final String OR = "OR";
    public static final String DOT = "DOT";
    public static final String DEC = "DEC";
    public static final String INC = "INC";

    /* PUNCTUATION */
    public static final String OPAREN = "OPAREN";
    public static final String CPAREN = "CPAREN";
    public static final String OBRACE = "OBRACE";
    public static final String CBRACE = "CBRACE";
    public static final String OSQUARE = "OSQUARE";
    public static final String CSQUARE = "CSQUARE";
    public static final String COMMA = "COMMA";
    public static final String SEMICOLON = "SEMICOLON";
    public static final String NOT = "NOT";

    /* PROGRAM PARTS */
    public static final String ASSIGN = "ASSIGN";
    public static final String WHILE = "WHILE";
    public static final String IF = "IF";
    public static final String ELSE = "ELSE";
    public static final String RETURN = "RETURN";  
    public static final String FUNCTION = "FUNCTION";
    public static final String END_FILE = "END_FILE";
    public static final String UNKOWN = "UNKOWN";
    public static final String BAD_NUMBER = "BAD_NUMBER";
    public static final String ERROR = "ERROR";
    public static final String EMPTY = "EMPTY";
    public static final String PRINT = "PRINT";
    public static final String PRINTLN = "PRINTLN";

    public static final String START = "START";
    public static final String END = "END";

    //Added during environment module
    public static final String ENV = "ENV";
    public static final String TABLE = "TABLE";

    //Added during final module
    public static final String GLUE = "GLUE";
    public static final String DEFINITION  = "DEFINITION";   
    public static final String CLASS = "CLASS";        
    public static final String VARDEF = "VARDEF";       
    public static final String INIT = "INIT";           
    public static final String PROGRAM = "PROGRAM";     
    public static final String LAMBDA = "LAMBDA";
    public static final String LAMBDA_CALL = "LAMBDA_CALL";
    public static final String LAMBDA_DEF = "LAMBDA_DEF";
    public static final String PAREN_EXPR = "PAREN_EXPR";   
    public static final String PARAM_LIST = "PARAM_LIST"; 
    public static final String ARG_LIST = "ARG_LIST";       
    public static final String STATEMENT = "STATEMENT";     
    public static final String STATEMENT_LIST = "STATEMENT_LIST";  
    public static final String BLOCK = "BLOCK";             
    public static final String AND_LIST = "AND_LIST";       
    public static final String OR_LIST = "OR_LIST";         
    public static final String WHILE_LOOP = "WHILE_LOOP";   
    public static final String IF_STATEMENT = "IF_STATEMENT";   
    public static final String ELSE_STATEMENT = "ELSE_STATEMENT";   
    public static final String FUNC_DEF = "FUNC_DEF";       
    public static final String FUNC_CALL = "FUNC_CALL";     
    public static final String ID_FUNC_CALL = "ID_FUNC_CALL";
    public static final String PRINT_STATE = "PRINT_STATE";     
    public static final String PRINTLN_STATE = "PRINTLN_STATE";
    public static final String RETURN_STATE = "RETURN_STATE";

    //Object oriented and Function
    public static final String CLOSURE = "CLOSURE";
    public static final String OCLOSURE = "OCLOSURE";
    public static final String EVAL_ARGS = "EVAL_ARGS";
    public static final String BUILTIN = "BUILTIN";
    public static final String ARRAY = "ARRAY";
    public static final String FILE_POINT = "FILE_POINT";

    //Error Lexemes
    public static final String NOT_FOUND = "NOT_FOUND";
}
