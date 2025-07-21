package app6;

public abstract class ElemAST {
  public abstract String LectAST();
  public abstract int EvalAST();

  public void ErreurEvalAST(String msg) {
    System.err.println("Erreur d'évaluation AST : " + msg);
    System.exit(1);
  }
}
