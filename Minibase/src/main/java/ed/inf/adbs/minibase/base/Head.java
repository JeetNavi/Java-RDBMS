package ed.inf.adbs.minibase.base;

import ed.inf.adbs.minibase.Utils;

import java.util.List;
import java.util.Objects;

public class Head {
    private String name;

    private List<Variable> variables;

    private SumAggregate agg;

    public Head(String name, List<Variable> variables, SumAggregate agg) {
        this.name = name;
        this.variables = variables;
        this.agg = agg;
    }

    public String getName() {
        return name;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public SumAggregate getSumAggregate() {
        return agg;
    }

    @Override
    public String toString() {
        if (agg == null) {
            return name + "(" + Utils.join(variables, ", ") + ")";
        }
        if (variables.isEmpty()) {
            return name + "(" + agg + ")";
        }
        return name + "(" + Utils.join(variables, ", ") + ", " + agg + ")";
    }

    /**
     * Overriding equals. Head objects should be equal if they have the same name and variables(and sum agg).
     * @param o The object to be compared with.
     * @return true if the object is an equivalent Head object, else false.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Head)) return false;
        Head head = (Head) o;
        return name.equals(head.name) && variables.equals(head.variables) && agg.equals(head.agg);
    }

    /**
     * Overriding hashcode since we overridden equals.
     * @return int hashcode.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, variables, agg);
    }

    /**
     * Method that checks if a given variable is in the head.
     * @param variable Variable to check if it is in the head.
     * @return true if the variable is in the head, else false.
     */
    public boolean containsVariable(Variable variable) {
        return variables.contains(variable);
    }
}