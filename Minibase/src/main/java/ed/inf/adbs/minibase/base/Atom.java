package ed.inf.adbs.minibase.base;

import java.util.*;

public class Atom {

    public String getRelationName(){
        return this.toString().split("\\(", 2)[0];
    }

    public List<Term> getAtomTerms(){

        String[] stringTerms = this.toString().split("[(,)]");

        List<Term> terms = new ArrayList<>();

        for (String stringTerm : stringTerms){
            try {
                int term = Integer.parseInt(stringTerm);
                terms.add(new IntegerConstant(term));
            } catch (NumberFormatException e) {
                if (stringTerm.startsWith("'")){
                    terms.add(new StringConstant(stringTerm));
                }
                else {
                    terms.add(new Variable(stringTerm));
                }
            }
        }

        return terms;

    }

}
