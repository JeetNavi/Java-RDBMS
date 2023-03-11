package ed.inf.adbs.minibase.base;

import java.util.Arrays;
import java.util.List;

/**
 * Tuple class used to represent a tuple (record).
 * Each tuple is made up of constants. We represent this with the values attribute.
 * Each tuple should store the variables (specified in the CQ) that correspond to each value.
 * Storing variables at tuple level makes it easy to extract constants from tuples given a variable.
 */
public class Tuple {

    // The constants of the tuple.
    private final Constant[] values;
    // The variables of the tuple.
    private final List<Variable> variables;

    /**
     * Constructor for Tuple.
     * Assigns values (constants) and variables.
     * @param values The constants that make the tuple.
     * @param variables The variables that correspond to the values.
     */
    public Tuple (Constant[] values, List<Variable> variables) {
        this.values = values;
        this.variables = variables;
    }

    /**
     * Getter for the values of the tuple.
     * @return The values of the tuple as an array of Constants.
     */
    public Constant[] getValues() {
        return values;
    }

    /**
     * Method to get the position of a variable in a tuple.
     * We exploit the fact that we store the variables in order of its values, so we can use indexOf here.
     * @param variable The variable of which we want the position of.
     * @return The position of the variable.
     */
    public Integer getPosOfVar(Variable variable) {
        return variables.indexOf(variable);
    }

    /**
     * Getter for the variables.
     * @return The variable.
     */
    public List<Variable> getVariables() {
        return variables;
    }

    /**
     * Method to get the constant value from the tuple given a variable.
     * @param variable The variable which corresponds to the desired constant.
     * @return The desired constant.
     */
    public Constant getConstantFromVariable(Variable variable) {
        return values[variables.indexOf(variable)];
    }

    /**
     * Overriding equals. Tuple objects should be equal if they have the same values (in the same order).
     * Note we don't consider variables here, because tuples are ultimately known only for their values.
     * @param o The object to be compared with.
     * @return true if the object is an equivalent Tuple object (from the explanation above), else false.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple)) return false;
        Tuple tuple = (Tuple) o;
        return Arrays.equals(values, tuple.values);
    }

    /**
     * Overriding hashcode for Tuple since we override equals.
     * @return int hashcode.
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }

    /**
     * Overriding toString for Tuple.
     * @return Tuple as string.
     */
    @Override
    public String toString() {
        return "Tuple{" +
                "values=" + Arrays.toString(values) +
                '}';
    }
}