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

  // Add a new constructor to accept a string expression directly
  public DescenteRecursive(String expression, boolean isExpression) {
    analyseur = new AnalLex(expression);
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
    if (courant.type == Type.PLUS || courant.type == Type.MOINS) {
      String op = courant.chaine;
      courant = analyseur.prochainTerminal();
      ElemAST droit = E();  // récursion à droite
      return new NoeudAST(op, gauche, droit);
    }
    return gauche;
  }

  // T  → F T'
  private ElemAST T() {
    ElemAST gauche = F();
    if (courant.type == Type.MULTIPLICATION || courant.type == Type.DIVISION) {
      String op = courant.chaine;
      courant = analyseur.prochainTerminal();
      ElemAST droit = T();  // récursion à droite
      return new NoeudAST(op, gauche, droit);
    }
    return gauche;
  }


  // F → (E) | ID | NUM
  private ElemAST F() {
    if (courant.type == Type.PARENTHESE_G) {
      courant = analyseur.prochainTerminal();
      ElemAST e = E();
      if (courant.type == Type.PARENTHESE_D) {
        courant = analyseur.prochainTerminal();
        return e;
      } else {
        ErreurSynt("Parenthèse fermante attendue");
      }
    } else if (courant.type == Type.IDENTIFICATION || courant.type == Type.NOMBRE) {
      ElemAST f = new FeuilleAST(courant.chaine);
      courant = analyseur.prochainTerminal();
      return f;
    } else {
      //erreur générale
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

    // Read all lines from the input file
    java.util.List<String> lines = new java.util.ArrayList<>();
    try {
      java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(args[0]));
      String line;
      while ((line = br.readLine()) != null) {
        if (!line.trim().isEmpty()) {
          lines.add(line.trim());
        }
      }
      br.close();
    } catch (Exception e) {
      System.out.println("Erreur lors de la lecture du fichier d'entrée: " + e);
      e.printStackTrace();
      System.exit(1);
    }

    int testNum = 1;
    for (String expr : lines) {
      toWrite += "===== Test case " + testNum + " =====\n";
      toWrite += "Expression: " + expr + "\n";
      try {
        DescenteRecursive dr = new DescenteRecursive(expr, true);
        dr.AnalSynt();
        int val = dr.RacineAST.EvalAST();
        toWrite += "Lecture de l'AST trouvé : " + dr.RacineAST.LectAST() + "\n";
        toWrite += "Expression postfixée : " + dr.RacineAST.PostfixAST() + "\n";
        if (val == Integer.MIN_VALUE) {
          toWrite += "Valeur de l'expression : indéterminée (contient des variables)\n";
        } else {
          toWrite += "Valeur de l'expression : " + val + "\n";
        }
        toWrite += "AST hiérarchique :\n" + dr.RacineAST.toStringTree() + "\n";
      } catch (Exception ex) {
        toWrite += "Erreur lors de l'analyse de l'expression: " + ex.getMessage() + "\n";
      }
      toWrite += "\n";
      testNum++;
    }

    System.out.println(toWrite);
    Writer w = new Writer(args[1], toWrite);
    System.out.println("Fin d'analyse syntaxique");
  }
}
