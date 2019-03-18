Friday, February 22
CS 403 - Programming Languages
Jacoby Benger

DESIGNER PROGRAMMING LANGUAGE - ranger

Ranger was implemented using Java. All files were complied using javac.
All ranger source files are of file extension '.rng'

Keywords:
    - while
    - if
    - else
    - var
    - return
    - true
    - false
    - class
    - function
    - lambda
    - print
    - println

Operators:
    - '+' -> Addition  
    - '-' -> Subtraction
    - '*' -> Multiplication
    - '/' -> Division
    - '%' -> Modulus

    - Additionally, pre-incrementation and pre-decrementation is supported on variables of type INTEGER 
        - ++x; 
        - --x;

Comparators
    - '>' -> Greater Than
    - '<' -> Less Than
    - '>=' -> Greater Than or Equal to
    - '<=' -> Less Than or Equal to
    - '==' -> Equivalent
    - '&&' -> AND
    - '||' -> OR
 
Primitives:
    - All variables are declared using the 'var' keyword, similar to python variables are not type exclusive and can change from storing integers to strings dynamically
    - Types:
        - INTEGER
        - REAL
        - STRING
        - BOOLEAN
    - Initializing a new variable to a set value is not neccessary. 
        - As a note, when just declaring a variable, it will automatically default to an INTEGER of value 0.
        - ex:
            - var x; 
            - print(x); <-- this would print the value 0;

Comments:
    - Comments must start with #
    - Block comments are not supported, all comments terminate with a newline character

Printing:
    - The keywords 'print' and 'println' can be used to print to stdout 
    - The only difference between the two is 'println' prints a newline after the expressions
    - Lists of expressions can be printed if seperated by commas
        - ex: println("value x = ", x);

Functions:
    - Functions are declared with the keyword 'function' followed by func_name[paramaters]
    - Functions Paramaters and Arguments must be encased with square brakets - []
    - Anonymous functions must be declared with keyword 'lambda' and must be assigned to a variable before being called

Arrays:
    - Arrays are supported via builtin functions:
        - newArray[INTEGER]:
            - returns a new array of size INTEGER
        - getArray[array_var, index]: 
            - array_var being the variable the array was assigned to
            - returns the value store at 'index'
        - setArray[array_var, index, value]:
            - this sets the slot at 'index' with 'value'
    - Arrays support storage of all primitive data types

File Input:
    - Reading from a file is supported via builtin functions:
        - openFileForReading["filename"]:
            - attempts to open the file - filename and returns a file pointer
        - ReadInteger[filepointer]:
            - filepointer being the variable the file pointer was assinged to
            - reads and returns one INTEGER at a time from the file
        - atFileEnd[filepointer]:
            - determines if there is anything left to read in the file and returns a BOOLEAN
        - closeFile[filepointer]:
            - this closes the file

Command Line Arguments:
    - Ranger supports reading in command line arguments via builtin functions:
        - getArgCount[]:
            - returns the number of arguments on the command line   
                - As a note - this will return 0 if nothing listed after the '.rng' file 
                - ex: java evaluator problem.rng            -> getArgCount[] = 0
                - ex: jave evaluator problem.rng ints.txt   -> getArgCount[] = 1
        - getArg[index]:
            - returns a STRING containing the arguement at 'index'

OVERALL PROGRAM STRUCTURE
    - Ranger requires programs to start with 'class' followed by ID of class
    - a semicolon must be added to the closing brace of each class

    - ex: class example{
                        ...
                        }; 
