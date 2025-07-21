package app6;

public class DescenteRecursive {
  private AnalLex analyseur;
  private Terminal courant;
  private ElemAST RacineAST;

  public DescenteRecursive(String fichier) {
    Reader r = new Reader(fichier);
    analyseur = new AnalLex(r.toString());
    courant = analyseur.prochainTerminal();
  }

  public void ErreurSynt(String msg) {
    System.err.println("Erreur syntaxique : " + msg + " à : " + courant.chaine);
    System.exit(1);
  }

  public void AnalSynt() {
    RacineAST = E();
    if (courant.type != Type.EOF) {
      ErreurSynt("Fin d'expression attendue");
    }
  }

  // E  → T E'
  private ElemAST E() {
    ElemAST gauche = T();
    return Ep(gauche);
  }

  // E' → + T E' | - T E' | ε
  private ElemAST Ep(ElemAST gauche) {
    if (courant.type == Type.PLUS || courant.type == Type.MOINS) {
      String op = courant.chaine;
      courant = analyseur.prochainTerminal();
      ElemAST droit = T();
      NoeudAST noeud = new NoeudAST(op, gauche, droit);
      return Ep(noeud);
    } else {
      return gauche;
    }
  }

  // T  → F T'
  private ElemAST T() {
    ElemAST gauche = F();
    return Tp(gauche);
  }

  // T' → * F T' | / F T' | ε
  private ElemAST Tp(ElemAST gauche) {
    if (courant.type == Type.MULT || courant.type == Type.DIV) {
      String op = courant.chaine;
      courant = analyseur.prochainTerminal();
      ElemAST droit = F();
      NoeudAST noeud = new NoeudAST(op, gauche, droit);
      return Tp(noeud);
    } else {
      return gauche;
    }
  }

  // F → (E) | ID | NUM
  private ElemAST F() {
    if (courant.type == Type.PARENG) {
      courant = analyseur.prochainTerminal();
      ElemAST e = E();
      if (courant.type == Type.PAREND) {
        courant = analyseur.prochainTerminal();
        return e;
      } else {
        ErreurSynt("Parenthèse fermante attendue");
      }
    } else if (courant.type == Type.ID || courant.type == Type.NUM) {
      ElemAST f = new FeuilleAST(courant.chaine);
      courant = analyseur.prochainTerminal();
      return f;
    } else {
      ErreurSynt("Identifiant, nombre ou parenthèse ouvrante attendu");
    }
    return null;
  }

  public static void main(String[] args) {
    String toWrite = "";
    System.out.println("Début d'analyse syntaxique");

    if (args.length == 0){
      args = new String[2];
      args[0] = "ExpArith.txt";
      args[1] = "ResultatSyntaxique.txt";
    }

    DescenteRecursive dr = new DescenteRecursive(args[0]);
    dr.AnalSynt();

    toWrite += "Lecture de l'AST trouvé : " + dr.RacineAST.LectAST() + "\n";
    toWrite += "Valeur de l'expression : " + dr.RacineAST.EvalAST() + "\n";

    System.out.println(toWrite);
    Writer w = new Writer(args[1], toWrite);
    System.out.println("Fin d'analyse syntaxique");
  }
}
