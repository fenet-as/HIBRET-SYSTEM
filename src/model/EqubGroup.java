package model;

import java.util.ArrayList;
import java.util.List;

public class EqubGroup {

    private String name;
    private int membersCount;
    private double contribution;

    private int currentCycle = 1;
    private int winnerIndex = 0;

    private List<Member> members = new ArrayList<>();

    public EqubGroup(String name, int membersCount, double contribution) {
        this.name = name;
        this.membersCount = membersCount;
        this.contribution = contribution;
    }

    // ================= MEMBER MANAGEMENT =================
    public void addMember(Member m) {
        members.add(m);
    }

    public List<Member> getMembersList() {
        return members;
    }

    public String getName() {
        return name;
    }

    public int getMembersCount() {
        return members.size();
    }

    public double getContribution() {
        return contribution;
    }

    public double getTotal() {
        return getMembersCount() * contribution;
    }

    public int getCurrentCycle() {
        return currentCycle;
    }

    public String getCurrentWinner() {
        if (members.isEmpty()) return "No Members";
        return members.get(winnerIndex).getName();
    }

    // ================= ROTATION =================
    public void nextCycle() {

        currentCycle++;

        if (!members.isEmpty()) {
            winnerIndex++;
            if (winnerIndex >= members.size()) {
                winnerIndex = 0;
            }
        }
    }
}