package ed.inf.adbs.minibase.base;

public class Variable extends Term {
    private String name;

    public Variable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean equals (Variable v) {
        return v.name.equals(this.name);
    }

    @Override
    public String toString() {
        return name;
    }
}
