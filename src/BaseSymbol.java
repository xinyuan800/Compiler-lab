public class BaseSymbol implements Symbol{
    final String name;
    Type type;

    public BaseSymbol(String name,Type type){
        this.name = name;
        this.type = type;
    }
    public void setType(Type type){
        this.type = type;
    }
    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }
}
