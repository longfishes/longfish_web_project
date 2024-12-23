package com.longfish;

import com.longfish.network.RoomInitializer;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RoomInitializer().setVisible(true));
    }

}
