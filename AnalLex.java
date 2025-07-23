package app6;

public class AnalLex {
  private String input;
  private int pos;
  private int length;

  public AnalLex(String input) {
    this.input = input;
    this.pos = 0;
    this.length = input.length();
  }

  public void ErreurLex(String msg) {
    // Instead of exiting, throw a runtime exception to be caught in main
    throw new RuntimeException("Erreur lexicale à pos " + pos + " : " + msg);
  }

  public boolean resteTerminal() {
    return pos < length;
  }

  public Terminal prochainTerminal() {
    // Ignore les espaces
    while (resteTerminal() && Character.isWhitespace(input.charAt(pos))) {
      pos++;
    }

    if (!resteTerminal()) {
      return new Terminal(Type.EOF, "EOF");
    }

    char current = input.charAt(pos);

    if (Character.isDigit(current)) {
      // Reconnaissance d'un nombre entier
      int start = pos;
      while (resteTerminal() && Character.isDigit(input.charAt(pos))) {
        pos++;
      }
      String nombre = input.substring(start, pos);
      return new Terminal(Type.NOMBRE, nombre);
    } else if (Character.isLetter(current) || current == '_') {
      if (!Character.isUpperCase(current)) {
        ErreurLex("Un identifiant doit commencer par une lettre majuscule");
      }

      // Reconnaissance d'un identificateur (lettres, chiffres, underscore)
      int start = pos;
      pos++;

      boolean prevUnderscore = false;

      //verification des règles pour les _
      while (resteTerminal()) {
        char ch = input.charAt(pos);

        if (Character.isLetter(ch)) {
          prevUnderscore = false;
          pos++;
        } else if (ch == '_') {
          if (prevUnderscore) {
            ErreurLex("Identifiant invalide : underscore double non permis");
          }
          prevUnderscore = true;
          pos++;

          // underscore ne doit pas être à la fin ou suivi de non-lettre
          if (!resteTerminal() || !Character.isLetter(input.charAt(pos))) {
            ErreurLex("Identifiant invalide : underscore final ou sans lettre après");
          }
        } else {
          break;
        }
      }
      String id = input.substring(start, pos);
      return new Terminal(Type.IDENTIFICATION, id);
    } else {
      // Reconnaissance des opérateurs et parenthèses
      pos++;
      switch (current) {
        case '+': return new Terminal(Type.PLUS, "+");
        case '-': return new Terminal(Type.MOINS, "-");
        case '*': return new Terminal(Type.MULTIPLICATION, "*");
        case '/': return new Terminal(Type.DIVISION, "/");
        case '(': return new Terminal(Type.PARENTHESE_G, "(");
        case ')': return new Terminal(Type.PARENTHESE_D, ")");
        default:
          ErreurLex("Caractère non reconnu: '" + current + "'");
          return null; // jamais atteint
      }
    }
  }

  public static void main(String[] args) {
    String toWrite = "";
    System.out.println("Debut d'analyse lexicale");
    if (args.length == 0){
      args = new String [2];
      args[0] = "ExpArith.txt";
      args[1] = "ResultatLexical.txt";
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
        AnalLex lexical = new AnalLex(expr);
        Terminal t = null;
        while (lexical.resteTerminal()) {
          t = lexical.prochainTerminal();
          toWrite += t.type.toString() + " : " + t.chaine + "\n";
        }
      } catch (Exception ex) {
        toWrite += "Erreur lors de l'analyse lexicale: " + ex.getMessage() + "\n";
      }
      toWrite += "\n";
      testNum++;
    }

    System.out.println(toWrite);
    Writer w = new Writer(args[1], toWrite);
    System.out.println("Fin d'analyse lexicale");
  }
}
