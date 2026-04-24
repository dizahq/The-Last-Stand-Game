package Codes;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;

public abstract class OverlayPanel extends JPanel{
    private int panelWidth, panelHeight;
    private JPanel containerPanel = new JPanel();
    public OverlayPanel(int panelWidth, int panelHeight){
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;

        setBounds(0, 0, panelWidth, panelHeight);
        setLayout(new GridBagLayout());
        setOpaque(false);
        addMouseListener(new MouseAdapter(){});
        
        containerPanel.setPreferredSize(new Dimension(400, 400));
        containerPanel.setBackground(Color.GRAY);
        add(containerPanel);

        setVisible(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(0, 0, panelWidth, panelHeight);
    }

    public JPanel getContainerPanel() {
        return containerPanel;
    }
}
