package model;

import java.util.ArrayList;
import java.util.List;

public class EqubGroup {

    private String name;
    private double contribution;

    private int currentCycle = 1;
    private int winnerIndex = 0;

    private List<Member> members = new ArrayList<>();

    public EqubGroup(String name, double contribution) {
        this.name = name;
        this.contribution = contribution;
    }

    // ================= GETTERS =================

    public String getName() {
        return name;
    }

    public double getContribution() {
        return contribution;
    }

    public int getCurrentCycle() {
        return currentCycle;
    }

    public List<Member> getMembersList() {
        return members;
    }

    public double getTotal() {
        return members.size() * contribution;
    }

    // ================= MEMBER =================

    public void addMember(Member member) {
        members.add(member);
    }

    public int getMembersCount() {
        return members.size();
    }

    // ================= WINNER =================

    public Member getCurrentWinner() {

        if (members.isEmpty()) {
            return null;
        }

        return members.get(winnerIndex);
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