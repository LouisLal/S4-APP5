package app6;

public class FeuilleAST extends ElemAST {
    private String valeur; // peut être une variable ou constante

    public FeuilleAST(String valeur) {
        this.valeur = valeur;
    }

    @Override
    public String LectAST() {
        return valeur;
    }

    @Override
    public int EvalAST() {
        try {
            return Integer.parseInt(valeur);
        } catch (NumberFormatException e) {
            return Integer.MIN_VALUE; // ou un autre flag (-1, etc.)
        }
    }
}
