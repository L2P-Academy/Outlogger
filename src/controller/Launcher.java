
package controller;

import javax.swing.SwingUtilities;

import view.AdminView;

public class Launcher {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new AdminView();
        });
    }
}

