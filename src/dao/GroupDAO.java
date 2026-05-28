package dao;

import model.EqubGroup;
import java.util.List;

public interface GroupDAO {
    void saveGroup(EqubGroup group);
    void updateGroup(EqubGroup group);
    void deleteGroup(String groupName);
    List<EqubGroup> getAllGroups();
    EqubGroup findGroupByName(String groupName);
}
