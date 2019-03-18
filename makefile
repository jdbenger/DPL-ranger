#MAKEFILE FOR FINAL PROJECT#
evaluator: evaluator.class parser.class lexer.class environment.class types.class lexeme.class 

evaluator.class: evaluator.java
	javac -Xlint:all -d . -classpath . evaluator.java

parser.class: parser.java
	javac -Xlint:all -d . -classpath . parser.java

lexer.class: lexer.java
	javac -Xlint:all -d . -classpath . lexer.java

environment.class: environment.java
	javac -Xlint:all -d . -classpath . environment.java

types.class: types.java
	javac -Xlint:all -d . -classpath . types.java

lexeme.class: lexeme.java
	javac -Xlint:all -d . -classpath . lexeme.java

clean:
	rm -f *.class

error1:
	cat error1.rng

error1x:
	-java evaluator error1.rng

error2:
	cat error2.rng

error2x:
	-java evaluator error2.rng

error3:
	cat error3.rng

error3x:
	-java evaluator error3.rng

error4:
	cat error4.rng

error4x:
	-java evaluator error4.rng

error5:
	cat error5.rng

error5x:
	-java evaluator error5.rng

arrays: 
	cat arrays.rng

arraysx:
	java evaluator arrays.rng

conditionals:
	cat conditionals.rng

conditionalsx:
	java evaluator conditionals.rng

recursion:
	cat recursion.rng

recursionx:
	java evaluator recursion.rng

iteration:
	cat iteration.rng

iterationx:
	java evaluator iteration.rng 

functions:
	cat functions.rng

functionsx:
	java evaluator functions.rng

lambda:
	cat lambda.rng

lambdax:
	java evaluator lambda.rng

objects:
	cat objects.rng

objectsx:
	java evaluator objects.rng

problem:
	cat problem.rng

problemx:
	java evaluator problem.rng ints.txt

test:
	cat error1.rng
	-java evaluator error1.rng
	cat error2.rng
	-java evaluator error2.rng
	cat error3.rng
	-java evaluator error3.rng
	cat error4.rng
	-java evaluator error4.rng
	cat error5.rng
	-java evaluator error5.rng
	cat arrays.rng
	-java evaluator arrays.rng
	cat conditionals.rng
	-java evaluator conditionals.rng
	cat recursion.rng
	-java evaluator recursion.rng
	cat iteration.rng
	-java evaluator iteration.rng 
	cat functions.rng
	-java evaluator functions.rng
	cat lambda.rng
	-java evaluator lambda.rng
	cat objects.rng
	-java evaluator objects.rng
	cat problem.rng
	-java evaluator problem.rng ints.txt
