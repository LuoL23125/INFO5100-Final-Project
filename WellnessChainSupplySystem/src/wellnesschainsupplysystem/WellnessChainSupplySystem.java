/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package wellnesschainsupplysystem;

import wellnesschainsupplysystem.ui.CustomerManagementFrame;

import javax.swing.*;

/**
 *
 * @author luole
 */
public class WellnessChainSupplySystem {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
         System.out.println("Wellness Chain Supply System - Starting...");

        // Start Swing UI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            CustomerManagementFrame frame = new CustomerManagementFrame();
            frame.setVisible(true);
        });
    }
}
    