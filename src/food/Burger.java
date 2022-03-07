package food;

public class Burger extends Food {

    public Burger(int id, String maker) {
        super.id = id;
        super.maker = maker;
    }
    
    @Override
    public String toString() {
        return "Burger " + id;
    }
    
}
