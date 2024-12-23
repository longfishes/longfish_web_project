package com.longfish.gomoku;

import com.longfish.network.RoomInitializer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Notice {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private JFrame frame;
    private final Component related;

    public Notice(Component related) {
        this.related = related;
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(300, 80);
            frame.setResizable(false);

            // 监听窗口关闭事件
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    SwingUtilities.invokeLater(() -> new RoomInitializer().setVisible(true));
                }
            });
        });
    }

    public void show(String message) {
        // 使用线程池来避免阻塞主线程
        executor.submit(() -> SwingUtilities.invokeLater(() -> {
            // 创建消息标签
            JLabel label = new JLabel(message, SwingConstants.CENTER);
            label.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            frame.add(label, BorderLayout.CENTER);

            // 设置弹窗居中显示
            frame.setLocationRelativeTo(related);
            frame.setVisible(true);
        }));
    }

    // 手动关闭提示框的方法
    public void close() {
        if (frame != null) {
            frame.dispose();
        }
    }
}
