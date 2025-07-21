package app6;

public abstract class ElemAST {
  public abstract String LectAST();
  public abstract int EvalAST();
  public abstract String PostfixAST();
  public abstract String toStringTree(String indent, boolean isLast);
  public String toStringTree() {
    return toStringTree("", true);
  }



  public void ErreurEvalAST(String msg) {
    System.err.println("Erreur d'évaluation AST : " + msg);
    System.exit(1);
  }
}
