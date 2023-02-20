package ed.inf.adbs.minibase.base;

import ed.inf.adbs.minibase.Utils;

import java.util.List;

public class Query {
    private Head head;

    private List<Atom> body;

    public Query(Head head, List<Atom> body) {
        this.head = head;
        this.body = body;
    }

    public Head getHead() {
        return head;
    }

    public List<Atom> getBody() {
        return body;
    }

    @Override
    public String toString() {
        return head + " :- " + Utils.join(body, ", ");
    }


    /**
     * Method that removes a given atom from the body of the query.
     * @param atom Atom to remove from the body.
     */
    public void removeFromBody(RelationalAtom atom) {
        body.remove(atom);
    }
}
