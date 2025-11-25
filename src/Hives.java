import java.util.ArrayList;
import java.util.List;

public class Hives {
    private List<BeeHive> hives;
    private List<String> locations;

    public Hives() {
        this.hives = new ArrayList<>();
        this.locations = new ArrayList<>();
    }

    public void addBeeHive(BeeHive hive) {
        hives.add(hive);
        // Add location if it's not already in the list
        if (!locations.contains(hive.getLocation())) {
            locations.add(hive.getLocation());
        }
    }

    public void addBeeHive(int tagNumber, String tagColor, String location) {
        hives.add(new BeeHive(tagNumber, tagColor, location));
        // Add location if it's not already in the list
        if (!locations.contains(location)) {
            locations.add(location);
        }
    }

    public List<BeeHive> getAllHives() {
        return new ArrayList<>(hives);
    }

    public List<BeeHive> getHivesByLocation(String location) {
        List<BeeHive> result = new ArrayList<>();
        for (BeeHive hive : hives) {
            if (hive.getLocation().equals(location)) {
                result.add(hive);
            }
        }
        return result;
    }

    public List<BeeHive> getHivesNeedingAttention() {
        List<BeeHive> result = new ArrayList<>();
        for (BeeHive hive : hives) {
            if (hive.needsAttention()) {
                result.add(hive);
            }
        }
        return result;
    }

    public List<String> getLocations() {
        return new ArrayList<>(locations);
    }

    public int getHiveCount() {
        return hives.size();
    }

    public int getHiveCountByLocation(String location) {
        return getHivesByLocation(location).size();
    }

    public void removeHive(BeeHive hive) {
        hives.remove(hive);
    }
}