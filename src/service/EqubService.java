package service;

import model.EqubGroup;

public interface EqubService {

    void createGroup(String name, double contribution);

    void makePayment(
            EqubGroup group,
            String memberName,
            double amount
    );

    void executePayout(EqubGroup group);

    void nextRotation(EqubGroup group);
}