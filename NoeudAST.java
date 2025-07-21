package app6;

public class NoeudAST extends ElemAST {
  private String operateur;
  private ElemAST gauche;
  private ElemAST droite;

  public NoeudAST(String operateur, ElemAST gauche, ElemAST droite) {
    this.operateur = operateur;
    this.gauche = gauche;
    this.droite = droite;
  }

  @Override
  public String LectAST() {
    return "(" + gauche.LectAST() + " " + operateur + " " + droite.LectAST() + ")";
  }

  @Override
  public int EvalAST() {
    int g = gauche.EvalAST();
    int d = droite.EvalAST();

    if (g == Integer.MIN_VALUE || d == Integer.MIN_VALUE) {
      return Integer.MIN_VALUE;
    }

    switch (operateur) {
      case "+":
        return g + d;
      case "-":
        return g - d;
      case "*":
        return g * d;
      case "/":
        return g / d;
      default:
        ErreurEvalAST("Opérateur non supporté : " + operateur);
        return 0;
    }
  }
}
