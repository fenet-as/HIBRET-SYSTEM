package ui.edir;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import model.EdirGroup;
import model.EmergencyCase;
import service.EdirService;
import service.impl.EdirServiceImpl;

public class DistributeFundPanel extends JPanel {
    private final JPanel containerPanel;
    private final EdirGroup group;
    private final EdirService edirService = new EdirServiceImpl();
    private JComboBox<EmergencyCase> comboPendingCases;

    public DistributeFundPanel(JPanel containerPanel, EdirGroup group) {
        this.containerPanel = containerPanel;
        this.group = group;

        setOpaque(false);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("💱 Approve Capital Vault Disbursement Requests");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        gbc.gridy = 0; gbc.gridx = 0; gbc.gridwidth = 2;
        add(title, gbc);

        gbc.gridwidth = 1; gbc.gridy = 1; gbc.gridx = 0;
        add(new JLabel("Select Verified Active Claim:"), gbc);

        comboPendingCases = new JComboBox<>();
        loadActiveClaimsPipeline();
        gbc.gridx = 1; add(comboPendingCases, gbc);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> ((CardLayout) containerPanel.getLayout()).show(containerPanel, "EdirGroupDetail"));

        JButton btnDisburse = new JButton("Authorize & Disburse Bank Cashout");
        btnDisburse.addActionListener(e -> {
            EmergencyCase chosenCase = (EmergencyCase) comboPendingCases.getSelectedItem();
            if (chosenCase == null) {
                JOptionPane.showMessageDialog(this, "No verified claims currently require distribution.");
                return;
            }

            edirService.disburseEmergencyFunds(chosenCase);
            JOptionPane.showMessageDialog(this, "Disbursement successful. Record written to Transaction Ledger.");
            ((CardLayout) containerPanel.getLayout()).show(containerPanel, "EdirGroupDetail");
        });

        actions.add(btnBack); actions.add(btnDisburse);
        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 2;
        add(actions, gbc);
    }

    private void loadActiveClaimsPipeline() {
        comboPendingCases.removeAllItems();
        List<EmergencyCase> cases = edirService.getPendingPayoutCasesForGroup(group.getId());
        for (EmergencyCase ec : cases) {
            comboPendingCases.addItem(ec);
        }
    }
}