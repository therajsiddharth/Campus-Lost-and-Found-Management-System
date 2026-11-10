package campus.lostfound.dao;

import campus.lostfound.db.DB;
import campus.lostfound.model.Claim;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class ClaimDAO {
    public void submit(int itemId,int claimantId,String proof) throws SQLException {
        String sql="INSERT INTO claims(item_id,claimant_id,proof_description) VALUES(?,?,?)";
        try(Connection c=DB.getConnection();PreparedStatement p=c.prepareStatement(sql)){
            p.setInt(1,itemId);p.setInt(2,claimantId);p.setString(3,proof);p.executeUpdate();
        }
    }
    public List<Claim> listClaims(boolean pendingOnly) throws SQLException {
        List<Claim> list=new ArrayList<>();
        String sql="SELECT * FROM claims "+(pendingOnly?"WHERE claim_status='PENDING' ":"")+"ORDER BY claim_date";
        try(Connection c=DB.getConnection();Statement s=c.createStatement();ResultSet r=s.executeQuery(sql)){
            while(r.next()) list.add(new Claim(r.getInt("claim_id"),r.getInt("item_id"),r.getInt("claimant_id"),
                r.getString("proof_description"),r.getString("claim_status"),r.getTimestamp("claim_date").toLocalDateTime()));
        } return list;
    }
    public Queue<Claim> pendingQueue() throws SQLException {
        Queue<Claim> q=new ArrayDeque<>();
        q.addAll(listClaims(true));
        return q;
    }
    public void review(int claimId,int adminId,boolean approve) throws SQLException {
        try(Connection c=DB.getConnection()){
            c.setAutoCommit(false);
            int itemId;
            try(PreparedStatement find=c.prepareStatement("SELECT item_id FROM claims WHERE claim_id=? AND claim_status='PENDING' FOR UPDATE")){
                find.setInt(1,claimId);try(ResultSet r=find.executeQuery()){if(!r.next())throw new SQLException("Pending claim not found.");itemId=r.getInt(1);}
            }
            String status=approve?"APPROVED":"REJECTED";
            try(PreparedStatement p=c.prepareStatement("UPDATE claims SET claim_status=?,reviewed_by=?,reviewed_at=NOW() WHERE claim_id=?")){
                p.setString(1,status);p.setInt(2,adminId);p.setInt(3,claimId);p.executeUpdate();
            }
            if(approve) try(PreparedStatement p=c.prepareStatement("UPDATE items SET item_status='CLAIM_PENDING' WHERE item_id=?")){
                p.setInt(1,itemId);p.executeUpdate();
            }
            try(PreparedStatement p=c.prepareStatement("INSERT INTO admin_logs(admin_id,action,target_type,target_id,details) VALUES(?,?,?,?,?)")){
                p.setInt(1,adminId);p.setString(2,"REVIEW_CLAIM");p.setString(3,"CLAIM");p.setInt(4,claimId);p.setString(5,status);p.executeUpdate();
            }
            c.commit();
        }
    }
}
