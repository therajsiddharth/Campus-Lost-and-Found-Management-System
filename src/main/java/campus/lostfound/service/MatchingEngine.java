package campus.lostfound.service;

import campus.lostfound.model.Item;
import java.util.*;

/**
 * DSA demonstration: ArrayList for candidate items and HashMap for O(1) average ID lookup.
 * Simple matching score: exact category + similar item name + same/related location.
 */
public class MatchingEngine {
    public Map<Integer,Item> indexById(List<Item> items) {
        Map<Integer,Item> index=new HashMap<>();
        for(Item item:items) index.put(item.getItemId(),item);
        return index;
    }
    public List<Item> possibleMatches(Item target,List<Item> candidates) {
        List<Item> matches=new ArrayList<>();
        for(Item candidate:candidates){
            if(candidate.getItemId()==target.getItemId()) continue;
            boolean opposite=("LOST".equals(target.getItemType()) && "FOUND".equals(candidate.getItemType()))
                || ("FOUND".equals(target.getItemType()) && "LOST".equals(candidate.getItemType()));
            if(!opposite) continue;
            boolean sameCategory=target.getCategoryId()==candidate.getCategoryId();
            boolean similarName=target.getItemName().equalsIgnoreCase(candidate.getItemName())
                || target.getItemName().toLowerCase().contains(candidate.getItemName().toLowerCase())
                || candidate.getItemName().toLowerCase().contains(target.getItemName().toLowerCase());
            boolean sameLocation=target.getLocation().equalsIgnoreCase(candidate.getLocation());
            if(sameCategory && (similarName || sameLocation)) matches.add(candidate);
        }
        matches.sort(Comparator.comparing(Item::getDateReported).reversed());
        return matches;
    }
}
