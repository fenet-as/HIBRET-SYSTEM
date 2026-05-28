package service.impl;

import dao.impl.GroupDAOImpl;
import model.EqubGroup;
import model.GroupStore;
import model.Member;
import service.EqubService;

public class EqubServiceImpl implements EqubService {

    private GroupDAOImpl dao =
            new GroupDAOImpl();


    public void createGroup(
            String name,
            double contribution
    ) {

        EqubGroup group =
                new EqubGroup(name, contribution);

        dao.saveGroup(group);

        GroupStore.groups.add(group);
    }

    @Override
    public void makePayment(
            EqubGroup group,
            String memberName,
            double amount
    ) {

        if (group == null) {
            return;
        }

        for (Member m : group.getMembersList()) {

            if (m.getName().equalsIgnoreCase(memberName)) {

                m.pay(amount);

                break;
            }
        }
    }

    @Override
    public void executePayout(EqubGroup group) {

        if (group == null) {
            return;
        }

        Member winner =
                group.getCurrentWinner();

        if (winner != null) {

            System.out.println(
                    "Payout sent to "
                            + winner.getName()
            );
        }
    }

    @Override
    public void nextRotation(EqubGroup group) {

        if (group != null) {

            group.nextCycle();
        }
    }
}
