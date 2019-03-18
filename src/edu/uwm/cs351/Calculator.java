package edu.uwm.cs351;

import java.util.EmptyStackException;

import edu.uwm.cs351.util.Stack;
import edu.uwm.cs351.util.IntMath;
import edu.uwm.cs351.Operation;


/**
 * @author Eddie Chapman (chapman4@uwm.edu)
 *
 */
public class Calculator {
    private Stack<Operation> operators;     // Stores operators for applying to the operands upon calculation
    private Stack<Long> operands;           // Stores long integers for calculating
    private Long defaultValue;              // The result of the most recent compute operation
    private boolean receiving;
    private int state;                      // Indicates which operations are legal
    
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
        state = 0;  // 'empty'
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
        state = 0;  // 'empty'
    }
    
    /**
     * Enter a number.
     * 
     * @precondition    the Calculator is in an empty or waiting state.
     * 
     * @postcondition   the number is added to the top of the operands Stack. 
     *                  The Calculator is a ready state. 
     *                  
     * @param val       a Long to be entered in the Calculator.
     * 
     * @throws          IllegalStateException if the Calculator is in a ready state.
     */
    public void value(long val) throws IllegalStateException {
        if (state == 1) 
            throw new IllegalStateException("Cannot add a value to a calculator in a ready state"); 
        
        operands.push(val);
        state = 1;  // 'ready'
    }
    
    /**
     * Enter a binary operator.
     * 
     * @precondition    the Calculator is in an empty or ready state.
     * 
     * @postcondition   an Operation has been added to the operators Stack and the 
     *                  Calculator is in a waiting state.
     *                  
     * @param op        an Operation that will be performed on the long integers in the 
     *                  operators Stack.
     *                  
     * @throws          IllegalStateException if the Calculator is in an waiting state 
     *                  before calling this method.
     */
    public void binop(Operation op) throws IllegalStateException{
        Operation op2;
        long operand1;
        long operand2;
        
        if ((op == Operation.RPAREN) || (op == Operation.LPAREN)) 
            throw new IllegalArgumentException("Parenthesis are not binary operators. Please use open() and close() instead.");
        
        if (state == 2) 
            throw new IllegalStateException("A binary operation cannot be entered to a calculator in a waiting state");
        
        if (state == 0) 
            operands.push(defaultValue);
        
        while (!operators.isEmpty() && (op.precedence() <= operators.peek().precedence())) {
            op2 = operators.pop();
            operand1 = operands.pop();
            operand2 = operands.pop();
            operands.push(op2.operate(operand2, operand1));
        }
        
        operators.push(op);
        state = 2;  // 'waiting'
    }
    
    /**
     * Replace the current value with the square root of the unsigned integer. 
     * 
     * @precondition        the Calculator is in an empty or ready state
     * 
     * @postcondition       the current value has been replaced by the square root 
     *                      of the unsigned integer
     *                      
     * @throws              IllegalStateException if the Calculator is in a waiting 
     *                      state when this method is called.
     */
    public void sqrt() throws IllegalStateException {
        if (state == 2) 
            throw new IllegalStateException("A binary operation cannot be entered to a calculator in a waiting state");
        
        Long sqrt = IntMath.isqrt(getCurrent());
        
        if (state == 1)
            operands.pop();
        
        operands.push(sqrt);  
        
        state = 1;
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
        if (state == 1) 
            throw new IllegalStateException("Cannot add a parenthesis when the Calculator is in a ready state.");
        
        operators.push(Operation.LPAREN);
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
     * @throws          EmptyStackException if the operators stack is missing an unclo sed open.
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
        else
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
     */
    public long compute() { 
        Operation op;
        
        if (state == 2)
            throw new IllegalStateException("Cannot compute values in a waiting state");
     
        if (state == 0)
            operands.push(defaultValue);
               
        while (!operators.isEmpty()) {
            op = operators.pop();
            defaultValue = operands.pop();
            defaultValue = op.operate(operands.pop(), defaultValue);
            operands.push(defaultValue);
        }
        
        defaultValue = operands.pop();
        
        state = 0;
        
        return defaultValue;
    }
    
    /**
     * Return the current value.
     * 
     * @return          the long integer most recently added to the operands Stack, 
     *                  or the default value if the Calculator is in an empty state.          
     */
    public long getCurrent() {
        if (state == 0) 
            return defaultValue;
        else
            return operands.peek();
    }
        
    
    
    
    
}
