package campus.lostfound.model;
import java.time.LocalDateTime;
public class Claim {
    private final int claimId, itemId, claimantId;
    private final String proof, status;
    private final LocalDateTime claimDate;
    public Claim(int claimId,int itemId,int claimantId,String proof,String status,LocalDateTime claimDate){
        this.claimId=claimId;this.itemId=itemId;this.claimantId=claimantId;this.proof=proof;this.status=status;this.claimDate=claimDate;
    }
    public int getClaimId(){return claimId;} public int getItemId(){return itemId;}
    public int getClaimantId(){return claimantId;} public String getProof(){return proof;}
    public String getStatus(){return status;} public LocalDateTime getClaimDate(){return claimDate;}
    @Override public String toString(){return "Claim #"+claimId+" | Item #"+itemId+" | User #"+claimantId+" | "+status;}
}
