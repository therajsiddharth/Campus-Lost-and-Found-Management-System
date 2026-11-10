package campus.lostfound.model;

import java.time.LocalDateTime;

public abstract class Item {
    private final int itemId;
    private final int reporterId;
    private final int categoryId;
    private final String itemName;
    private final String description;
    private final String location;
    private final String status;
    private final LocalDateTime dateReported;

    protected Item(int itemId, int reporterId, int categoryId, String itemName,
                   String description, String location, String status, LocalDateTime dateReported) {
        this.itemId=itemId; this.reporterId=reporterId; this.categoryId=categoryId;
        this.itemName=itemName; this.description=description; this.location=location;
        this.status=status; this.dateReported=dateReported;
    }
    public int getItemId(){return itemId;}
    public int getReporterId(){return reporterId;}
    public int getCategoryId(){return categoryId;}
    public String getItemName(){return itemName;}
    public String getDescription(){return description;}
    public String getLocation(){return location;}
    public String getStatus(){return status;}
    public LocalDateTime getDateReported(){return dateReported;}
    public abstract String getItemType();
    public String getItemDescription(){return getItemType()+": "+itemName+" at "+location;}
}
