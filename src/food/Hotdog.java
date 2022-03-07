package food;

public class Hotdog extends Food {
    
    public Hotdog(int id, String maker) {
        super.id = id;
        super.maker = maker;
    }
    
    @Override
    public String toString() {
        return "Hotdog " + id;
    }
}
