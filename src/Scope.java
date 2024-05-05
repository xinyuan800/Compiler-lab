import java.util.Map;

public interface Scope {
    String getName();
    void setName(String name);

    Map<String, Symbol> getSymbols();

    Scope getEnclosingScope();

    void define(Symbol symbol);

    Symbol findCurrentScope(String name);

    Symbol findWholeScope(String name);
}