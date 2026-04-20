package Codes;

import javax.swing.JButton;
import javax.swing.JPanel;

public class ExitConfirmPanel extends OverlayPanel{
    private JPanel container;
    private JButton confirmExitBtn = new JButton("Yes");
    private JButton cancelExitBtn = new JButton("No");
    public ExitConfirmPanel(int panelWidth, int panelHeight){
        super(panelWidth, panelHeight);
        container = getContainerPanel();

        confirmExitBtn.addActionListener(e -> {
            System.exit(0);
        });
        cancelExitBtn.addActionListener(e -> {
            setVisible(false);
        });

        container.add(confirmExitBtn);
        container.add(cancelExitBtn);
    }
}
