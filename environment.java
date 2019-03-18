/* Friday, February 22nd 
 * Jacoby Benger
 * CS 403 - Programming Languages 
 * 
 * ENVIORNMENT.JAVA
 */

public class environment implements types
{
    /*HELPER FUNCITONS*/
    private lexeme newLexeme(String type)
    {
        lexeme temp = new lexeme(type, null, null);
        return temp;
    }

    public lexeme car(lexeme temp)
    {
        return temp.left;
    }

    public lexeme cdr(lexeme temp)
    {
        return temp.right;
    }

    public lexeme set_car(lexeme temp, lexeme val)
    {
        temp.left = val;
        return temp.left;
    }

    public lexeme set_cdr(lexeme temp, lexeme val)
    {
        temp.right = val;
        return temp.right;
    }
    public lexeme cadr(lexeme temp)
    {
        return temp.left.right;
    }

    public lexeme cons(String glue, lexeme vars, lexeme vals)
    {
        //Debugging print for tree structure 
        // if(vars == null && vals == null)
        //     System.out.println("CONS - type: "+glue+" left: null, right: null");
        // else if(vars == null && vals != null)
        //     System.out.println("CONS - type: "+glue+" left: null, right: "+vals.type);
        // else if(vars != null && vals == null)
        //     System.out.println("CONS - type: "+glue+" left: "+vars.type+", right: null");
        // else
        //     System.out.println("CONS - type: "+glue+" left: "+vars.type+" right: "+vals.type);
        return new lexeme(glue, vars, vals);
    }

    private boolean sameLex(lexeme a, lexeme b)
    {
        return ((a.type == b.type) && a.sval.equals(b.sval));
    }

    /*ENVIRONMENT API*/
    public lexeme newEnvironment()
    {
        //System.out.println("Creating a new environment");
        return cons(ENV, cons(TABLE, null, null), null);
    }

    //extends current scope
    public lexeme extend(lexeme vars, lexeme vals, lexeme env)
    {
        //System.out.println("Extending scope");
        //System.out.println("vars.type = "+vars.type);
        //if(vals == null) System.out.println("VALS is NULL");
        //System.err.println("vals.type = "+vals.type);
        return cons(ENV, cons(TABLE, vars, vals), env);
    }

    public lexeme insert(lexeme env, lexeme var, lexeme val)
    {
        // System.out.print("Inserting variable: " + var.sval);
        // if(val == null)
        //     System.out.println(" that has not been initilized");
        // else if(val.type == INTEGER)
        //     System.out.println(" with value: " + val.ival);
        // else if(val.type == STRING)
        //     System.out.println(" with value: " + val.sval);
        // else if(val.type == BOOLEAN)
        //     System.out.println(" with value: " + val.bval);
        // else if(val.type == ARRAY)
        //     System.out.println(", that is an ARRAY");
        // else
        //     System.out.println(", that is a closure");
        set_car(car(env), cons(TABLE, var, car(car(env))));  //adding ID
        set_cdr(car(env), cons(TABLE, val, cdr(car(env))));  //adding VALUE
       
        return val;
    }

    public lexeme getVal(lexeme env, lexeme var) throws Exception
    {
        //System.out.println("GETTING VALUE");
        while(env != null)
        {
            lexeme vars = car(car(env));
            lexeme vals = cdr(car(env));

            while(vars != null)
            {
                if(sameLex(var, car(vars)))
                {
                    // System.out.print("FOUND: " + var.sval);
                    // if(car(vals) == null)
                    //     System.out.println(", but it has not been initalized");
                    // else if(car(vals).type == INTEGER)
                    //     System.out.println(" with value: " + car(vals).ival);
                    // else if(car(vals).type == STRING)
                    //     System.out.println(" with value: " + car(vals).sval);
                    // else if(car(vals).type == BOOLEAN)
                    //     System.out.println(" with value: " + car(vals).bval);
                    // else if(car(vals).type == CLOSURE)
                    //     System.out.println(", that is a closure");
                    // else if(car(vals).type == ARRAY)
                    //     System.out.println(", that is an ARRAY");
                    return car(vals);
                }
            
                vars = cdr(vars);
                vals = cdr(vals);
            }
            env = cdr(env);
        }
        errorHandler(new lexeme(ERROR, "Variable: <" + var.sval + "> has not been declared", var.line-1));
        return new lexeme(ERROR, "Variable: <" + var.sval + "> was not found", var.line);
    }

    //Function to update value of already defined variable
    public lexeme update(lexeme env, lexeme var, lexeme val) throws Exception
    {
        while(env != null)
        {
            lexeme vars = car(car(env));
            lexeme vals = cdr(car(env));

            while(vars != null)
            {
                if(sameLex(var, car(vars)))
                {
                    // System.out.print("Updated variable: " + var.sval);
                    // if(val.type == INTEGER)
                    //     System.out.println(" to the value: " + val.ival);
                    // else if(val.type == STRING)
                    //     System.out.println(" to the value: " + val.sval);
                    // else if(val.type == BOOLEAN)
                    //     System.out.println(" to the value: " + val.bval);
                    return set_car(vals, val);
                }
                vars = cdr(vars);
                vals = cdr(vals);
            }
        }
        errorHandler(new lexeme(ERROR, "Variable: <" + var.sval + "> has not been declared", var.line-1));
        return new lexeme(ERROR, "Variable: <" + var.sval + "> was not found", var.line);
    }

    //Function for displaying Environment
    //The level parameter determines the scope of printing
    //true = entire env, false = local env
    public void printEnv(lexeme env, boolean level)
    {
        //print entire environment
        if(level == true)
        {
            System.out.println("The environment is: ");
            while(env != null)
            {
                lexeme var = car(car(env));
                lexeme val = cdr(car(env));
                
                if(var == null)
                {
                    System.out.println("This Scope is empty");
                }
                else
                {
                    while(var != null)
                    {
                        System.out.print("Variable: " + car(var).sval);
                        if(car(val).type == INTEGER)
                            System.out.println(" with value: " + car(val).ival);
                        else if(car(val).type == STRING)
                            System.out.println(" with value: " + car(val).sval);
                        else if(car(val).type == BOOLEAN)
                            System.out.println(" with value: " + car(val).bval);
                        
                        var = cdr(var);
                        val = cdr(val);
                    }
                    
                }
                env = cdr(env);
                if(env != null)
                    System.out.println("Outer Scope is: ");
                
            }
        }
        //print local environment
        else
        {
            System.out.println("The local enviornment is: ");
            lexeme var = car(car(env));
            lexeme val = cdr(car(env));

            if(var == null)
            {
                System.out.println("This Scope is empty");
            }
            else
            {
                while(var != null)
                {   
                    System.out.print("Variable: " + car(var).sval);
                    if(car(val).type == INTEGER)
                        System.out.println(" with value: " + car(val).ival);
                    else if(car(val).type == STRING)
                        System.out.println(" with value: " + car(val).sval);
                    else if(car(val).type == BOOLEAN)
                        System.out.println(" with value: " + car(val).bval);

                    var = cdr(var);
                    val = cdr(val);
                 }
            }
        }
    }

        /*ERROR HANDLER*/
    private void errorHandler(lexeme err)
    {
        System.out.println("ERROR - "+err.sval+", on line: "+err.line);
        System.exit(1);        
    }
}//ends class
