package edu.uwm.cs351;

import java.util.EmptyStackException;

import edu.uwm.cs351.util.Stack;
import edu.uwm.cs351.Operation;


/**
 * @author Eddie Chapman (chapman4@uwm.edu)
 *
 */
public class Calculator {
    private Stack<Operation> operators;      // Stores operators for applying to the operands upon calculation
    private Stack<Long> operands;       // Stores long integers for calculating
    private Long defaultValue;          // The result of the most recent compute operation
    private boolean receiving;
    private int state;                  // Indicates which operations are legal
    
    /**
     * Initialize an empty calculator.
     * 
     * @postcondition   The calculator has an empty state with no stored operators, 
     *                  operands, or recently calculated values.         
     */
    public Calculator() {
        operators = new Stack<Operation>();
        operands = new Stack<Long>();
        defaultValue = 0L;
        state = 0;
    }
    
    /**
     * Clear the calculator and default value.
     * 
     * @postcondition   the calculator has an empty state with no stored operators, 
     *                  operands, or recently calculated values.
     */
    public void clear() {
        operators.clear();
        operands.clear();
        defaultValue = 0L;
        state = 0;
    }
    
    /**
     * Enter a number.
     * 
     * @precondition    the Calculator is in an empty or waiting state.
     * 
     * @postcondition   the number is added to the top of the operands Stack. 
     *                  The Calculator is a ready state. 
     *                  
     * @param number    a Long to be entered in the Calculator.
     * 
     * @throws          IllegalStateException if the Calculator is in a ready state.
     */
    public void value(long number) throws IllegalStateException {
        if (state == 1) throw new IllegalStateException("Cannot add a value to a calculator in state 1 ('ready')"); 
        operands.push(number);
        state = 1;  // ready
    }
    
    /**
     * Enter a binary operator.
     * 
     * @precondition    the Calculator is in an empty or ready state.
     * 
     * @postcondition   an Operation has been added to the operators Stack and the 
     *                  Calculator is in a waiting state.
     *                  
     * @param o         an Operation that will be performed on the long integers in the 
     *                  operators Stack.
     *                  
     * @throws          IllegalStateException if the Calculator is in an waiting state 
     *                  before calling this method.
     */
    public void binop(Operation o) throws IllegalStateException {
        if (state == 2) throw new IllegalStateException("A binary operation cannot be entered to a calculator in a waiting state");
        if (state == 0) value(defaultValue);
        operators.push(o);
        state = 2;
    }
    
    /**
     * Replace the current value with the square root of the unsigned integer. 
     * 
     * This uses the "unsigned integer square root" function in the IntMath class.
     * As with binop, the default value is used if we were in the empty state.
     * 
     * @precondition
     * @postcondition
     */
    public void sqrt() {

    }
    
    /**
     * Start a parenthetical expression.
     * 
     * @precondition    The Calculator is in an empty or waiting state.
     * 
     * @postcondition   A left parenthesis has been added to the operators stack,
     *                  and the Calculator is in a waiting state.
     * 
     * @throws          IllegalStateException if the Calculator is in a ready state
     *                  when this method is called. 
     */
    public void open() throws IllegalStateException {
        if (state == 1) throw new IllegalStateException("Cannot add a parenthesis when the Calculator is in a ready state.");
        operators.push(Operation.find("LPAREN"));
        state = 2;
    }
    
    /**
     * End a parenthetical expression
     * 
     * @precondition    the Calculator is in a ready state and the operators stack includes 
     *                  an unclosed parenthetical expression.
     * 
     * @postcondition   the most recent unclosed parenthetical expression has been closed. 
     *                  The Calculator is in a ready state.
     * 
     * @throws          EmptyStackException if the operators stack is missing an unclosed open.
     * 
     * @throws          IllegalStateException if the Calculator is in an empty or waiting state
     *                  when this this method is called.
     */
    public void close() throws IllegalStateException, EmptyStackException {
        if (state != 1) 
            throw new IllegalStateException("The Calculator must be in a ready state to attempt a close operation.");  
        
        Stack<Operation> temp = operators.clone();
        int unclosed = 0;
        
        while (!temp.isEmpty()) {
            Operation op = temp.pop();
            if (op == Operation.LPAREN) ++unclosed;
            if (op == Operation.RPAREN) --unclosed;
        }   
        
        if (unclosed > 1) 
            throw new EmptyStackException();
        
        operators.push(Operation.RPAREN);
    }
    
    /**
     * Perform all pending computations.
     * 
     * In the process, all incomplete parenthetical expressions are ended. If we were 
     * in an empty state before, the result is just the default value. Afterwards, the 
     * default value is the result which is returned.
     * 
     * @precondition
     * 
     * @postcondition
     * 
     * @return          the default value
     */
    public long compute() {
        
    }
    
    /**
     * Return the current value.
     * 
     * @return          the Long most recently added to the operands Stack, or the 
     *                  Long default value if the Calculator is in an empty state.          
     */
    public Long getCurrent() {
        if (state == 0) return defaultValue;
        return operands.peek();
    }
        
    
    
    
    
}
