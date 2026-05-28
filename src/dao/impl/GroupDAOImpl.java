package dao.impl;

import dao.GroupDAO;
import model.EqubGroup;
import java.util.ArrayList;
import java.util.List;

public class GroupDAOImpl implements GroupDAO {
    // Simulated Database
    private static List<EqubGroup> database = new ArrayList<>();

    @Override
    public void saveGroup(EqubGroup group) {
        // SQL: INSERT INTO groups ...
        database.add(group);
    }

    @Override
    public void updateGroup(EqubGroup group) {
        // SQL: UPDATE groups SET ... WHERE name = ...
    }

    @Override
    public void deleteGroup(String groupName) {
        // SQL: DELETE FROM groups WHERE name = ...
        database.removeIf(g -> g.getName().equals(groupName));
    }

    @Override
    public List<EqubGroup> getAllGroups() {
        // SQL: SELECT * FROM groups
        return new ArrayList<>(database);
    }

    @Override
    public EqubGroup findGroupByName(String groupName) {
        return database.stream()
                .filter(g -> g.getName().equalsIgnoreCase(groupName))
                .findFirst()
                .orElse(null);
    }
}
