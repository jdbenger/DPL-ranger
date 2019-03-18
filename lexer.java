/* Friday, February 22nd 
 * Jacoby Benger
 * CS 403 - Programming Languages 
 * 
 * LEXER.JAVA
 */

import java.io.FileReader;
import java.io.PushbackReader;
import java.io.BufferedReader;
import java.lang.Character;

public class lexer implements types
{
    /*Globals*/
    String filename;
    PushbackReader in;
    int linenum = -1;
    char ch;
    int temp;


    /*Main Constructor*/
    public lexer(String file)
    {
        filename = file;
        
        //initializing scanner object 
        try{
            in = new PushbackReader(new BufferedReader(new FileReader(filename)));
        }
        catch(Exception e){
            throw new NullPointerException("Unable to open file: " + filename);
        }
    }

    /*Main Lexing Function*/
    public lexeme lex() throws Exception
    {
           skipWhiteSpace();
           if(temp == -1) { return new lexeme(END_FILE, linenum); }

           ch = (char) temp;

           if(ch == '#')
           {
               pushback(temp);
               skipWhiteSpace();
           }

           switch(ch)
           {
               case('(')    : return new lexeme(OPAREN, linenum);
               case(')')    : return new lexeme(CPAREN, linenum);
               case('{')    : return new lexeme(OBRACE, linenum);
               case('}')    : return new lexeme(CBRACE, linenum);
               case('[')    : return new lexeme(OSQUARE, linenum);
               case(']')    : return new lexeme(CSQUARE, linenum);
               case(',')    : return new lexeme(COMMA, linenum);
               case('+')    : return new lexeme(PLUS, linenum);
               case('-')    : return new lexeme(MINUS, linenum);
               case('*')    : return new lexeme(TIMES, linenum);
               case('/')    : return new lexeme(DIVIDES, linenum);
               case('%')    : return new lexeme(MOD, linenum);
               case('<')    : return new lexeme(LESSTHAN, linenum);
               case('>')    : return new lexeme(GREATTHAN, linenum);
               case('=')    : return new lexeme(ASSIGN, linenum);
               case(';')    : return new lexeme(SEMICOLON, linenum);
               case('&')    : return new lexeme(AND, linenum);
               case('|')    : return new lexeme(OR, linenum);
               case('!')    : return new lexeme(NOT, linenum);
               case('.')    : return new lexeme(DOT, linenum);   

               default      :
                              if(Character.isDigit(ch))
                              {
                                  pushback(temp);
                                  return lexNumber();
                              }
                              else if(Character.isLetter(ch))
                              {
                                  pushback(temp);
                                  return lexVariableOrKey();
                              }
                              else if(ch == '\"')
                              {
                                  return lexString();
                              }
                              else
                              {   System.out.println("MAKING UNKOWN: " + ch);
                                  return new lexeme(UNKOWN, ch, linenum);
                              }
           }
    }
   
    /*LEX NUMBER HELPER*/
    private lexeme lexNumber() throws Exception
    {
        boolean real = false;
        String buffer = "";

        temp = readChar();
        ch = (char) temp;

        while(temp != -1 && (Character.isDigit(ch) || ch == '.'))
        {
            buffer += ch;
            if(ch == '.' && real) { return new lexeme(ERROR, "ILLEGAL NUMBER", linenum); }
            else if(ch == '.') { real = true; }

            temp = readChar();
            ch = (char) temp;
        }
        pushback(temp);

        if(real)
            return new lexeme(REAL, Double.parseDouble(buffer), linenum);
        else
            return new lexeme(INTEGER, Integer.parseInt(buffer), linenum);
    }

    /*LEX VARIABLE OR KEY HELPER*/
    private lexeme lexVariableOrKey() throws Exception
    {
        String buffer = "";

        temp = readChar();
        ch = (char) temp;

        while(temp != -1 && (Character.isDigit(ch) || (Character.isLetter(ch)) || ch == '_'))
        {
            buffer += ch;
            temp = readChar();
            ch = (char) temp;
        }
        pushback(temp);

        switch(buffer)
        {
            case("while")   : return new lexeme(WHILE, linenum);
            case("if")      : return new lexeme(IF, linenum);
            case("else")    : return new lexeme(ELSE, linenum);
            case("true")    : return new lexeme(BOOLEAN, true, linenum);
            case("false")   : return new lexeme(BOOLEAN, false, linenum);
            case("var")     : return new lexeme(VAR_TYPE, linenum);

            case("class")   : return new lexeme(CLASS, linenum);
            case("return")  : return new lexeme(RETURN, linenum);
            case("function"): return new lexeme(FUNCTION, linenum);
            case("lambda")  : return new lexeme(LAMBDA, linenum);
            case("print")   : return new lexeme(PRINT, linenum);
            case("println") : return new lexeme(PRINTLN, linenum);

            default         : return new lexeme(ID, buffer, linenum);
        }
    }

    /*LEX STRING HELPER*/
    private lexeme lexString() throws Exception
    {
        String buffer = "";

        temp = readChar();
        ch = (char) temp;

        do
        {
            buffer += ch;

            temp = readChar();
            ch = (char) temp;
        }
        while(ch != '\"');

        return new lexeme(STRING, buffer, linenum);
    }

    private void skipWhiteSpace() throws Exception
    {
        temp = readChar();
        ch = (char) temp;

        //Dealing with comments
        if(ch == '#')
        {
            while(ch != '\n')
            {
                temp = readChar();
                ch = (char) temp;
            }
            pushback(temp);
        }

        while(temp != -1 && Character.isWhitespace(ch))
        {
            //read ASCII value into temp, casting to ensure type agreement
            temp = readChar();
            ch = (char) temp;
        }
    }

    /*Helper for Reading*/
    private int readChar() throws Exception 
    {
        int cha = in.read();
        if(cha == '\n') { linenum++; }
        return cha;
    }

    /*Helper for Pushback*/
    private void pushback(int val) throws Exception
    {
        in.unread(val);
    }

    /*Helper for linenum*/
    public int getLine()
    {
        return linenum-1;
    }

}
