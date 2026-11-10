package campus.lostfound.model;
import java.time.LocalDateTime;
public class LostItem extends Item {
    public LostItem(int id,int reporter,int category,String name,String description,String location,String status,LocalDateTime date){
        super(id,reporter,category,name,description,location,status,date);
    }
    @Override public String getItemType(){return "LOST";}
}
