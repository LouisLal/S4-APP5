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
    System.err.println("Erreur lexicale à pos " + pos + " : " + msg);
    System.exit(1);
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
      return new Terminal(Type.NUM, nombre);
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
      return new Terminal(Type.ID, id);
    } else {
      // Reconnaissance des opérateurs et parenthèses
      pos++;
      switch (current) {
        case '+': return new Terminal(Type.PLUS, "+");
        case '-': return new Terminal(Type.MOINS, "-");
        case '*': return new Terminal(Type.MULT, "*");
        case '/': return new Terminal(Type.DIV, "/");
        case '(': return new Terminal(Type.PARENG, "(");
        case ')': return new Terminal(Type.PAREND, ")");
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
    Reader r = new Reader(args[0]);
    AnalLex lexical = new AnalLex(r.toString());

    Terminal t = null;
    while (lexical.resteTerminal()) {
      t = lexical.prochainTerminal();
      toWrite += t.chaine + "\n";
    }

    System.out.println(toWrite);
    Writer w = new Writer(args[1], toWrite);
    System.out.println("Fin d'analyse lexicale");
  }
}
