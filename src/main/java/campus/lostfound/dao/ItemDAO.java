package campus.lostfound.dao;

import campus.lostfound.db.DB;
import campus.lostfound.model.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class ItemDAO {
    public List<Item> listItems(String search, String type, boolean admin) throws SQLException {
        List<Item> items=new ArrayList<>();
        StringBuilder sql=new StringBuilder("SELECT * FROM items WHERE (?='' OR item_name LIKE ? OR location LIKE ?) AND (?='ALL' OR item_type=?) ");
        if(!admin) sql.append("AND item_status IN ('APPROVED','CLAIM_PENDING','RETURNED') ");
        sql.append("ORDER BY date_reported DESC");
        try(Connection c=DB.getConnection();PreparedStatement p=c.prepareStatement(sql.toString())){
            String q=search==null?"":search.trim();
            p.setString(1,q);p.setString(2,"%"+q+"%");p.setString(3,"%"+q+"%");
            p.setString(4,type==null?"ALL":type);p.setString(5,type==null?"ALL":type);
            try(ResultSet r=p.executeQuery()){while(r.next()){
                LocalDateTime d=r.getTimestamp("date_reported").toLocalDateTime();
                String t=r.getString("item_type");
                Item item="LOST".equals(t)
                    ? new LostItem(r.getInt("item_id"),r.getInt("reporter_id"),r.getInt("category_id"),r.getString("item_name"),r.getString("description"),r.getString("location"),r.getString("item_status"),d)
                    : new FoundItem(r.getInt("item_id"),r.getInt("reporter_id"),r.getInt("category_id"),r.getString("item_name"),r.getString("description"),r.getString("location"),r.getString("item_status"),d);
                items.add(item);
            }}
        } return items;
    }
    public void report(int userId,int categoryId,String name,String desc,String location,String type) throws SQLException {
        String sql="INSERT INTO items(reporter_id,category_id,item_name,description,location,item_type) VALUES(?,?,?,?,?,?)";
        try(Connection c=DB.getConnection();PreparedStatement p=c.prepareStatement(sql)){
            p.setInt(1,userId);p.setInt(2,categoryId);p.setString(3,name);p.setString(4,desc);p.setString(5,location);p.setString(6,type);p.executeUpdate();
        }
    }
    public void adminUpdateStatus(int itemId,String status,int adminId) throws SQLException {
        try(Connection c=DB.getConnection()){
            c.setAutoCommit(false);
            try(PreparedStatement p=c.prepareStatement("UPDATE items SET item_status=?,verified_by=?,verified_at=NOW() WHERE item_id=?")){
                p.setString(1,status);p.setInt(2,adminId);p.setInt(3,itemId);p.executeUpdate();
            }
            try(PreparedStatement p=c.prepareStatement("INSERT INTO admin_logs(admin_id,action,target_type,target_id,details) VALUES(?,?,?,?,?)")){
                p.setInt(1,adminId);p.setString(2,"UPDATE_ITEM_STATUS");p.setString(3,"ITEM");p.setInt(4,itemId);p.setString(5,"Status changed to "+status);p.executeUpdate();
            }
            c.commit();
        }
    }
    public int[] stats() throws SQLException {
        int[] s=new int[4];
        try(Connection c=DB.getConnection();Statement st=c.createStatement();
            ResultSet r=st.executeQuery("SELECT COUNT(*),SUM(item_type='LOST'),SUM(item_type='FOUND'),SUM(item_status='RETURNED') FROM items")){
            if(r.next()){for(int i=0;i<4;i++)s[i]=r.getInt(i+1);}
        } return s;
    }
    public Map<Integer,Item> buildItemIndex(List<Item> items){
        Map<Integer,Item> index=new HashMap<>();
        for(Item item:items) index.put(item.getItemId(),item);
        return index;
    }
}
