package gg.codie.mineonline.gui;

import gg.codie.mineonline.gui.rendering.DisplayManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ProgressDialog extends JDialog {

    private JLabel message;
    private JLabel subMessage;
    private JProgressBar progressBar;
    private SwingWorker worker;
    private int progress;

    private static ProgressDialog singleton;

    public static void setMessage(String message) {
        if (singleton != null) {
            singleton.message.setText(message);
        }
    }

    public static void setSubMessage(String message) {
        if (singleton != null) {
            if (message == null || message.isEmpty()) message = " ";
            singleton.subMessage.setText(message);
        }
    }

    private ProgressDialog(String title) {
        super((Dialog)null);
        this.setTitle(title);

        Image img = Toolkit.getDefaultToolkit().getImage(DisplayManager.class.getResource("/img/favicon.png"));
        this.setIconImage(img);
    }

    public static void setProgress(int progress) {
        if (singleton != null) {
            singleton.progress = progress;

            if (progress >= 100) {
                singleton.dispose();
                singleton = null;
            }
        }
    }

    private void start() {
        ((JComponent)getContentPane()).setBorder(new EmptyBorder(8, 8, 8, 8));

        message = new JLabel("Loading");
        subMessage = new JLabel(" ");
        progressBar = new JProgressBar();

        setPreferredSize(new Dimension(300, 120));
        setResizable(false);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        add(message, gbc);

        gbc.gridy++;
        add(subMessage, gbc);

        gbc.gridy++;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(progressBar, gbc);

        pack();

        this.worker.addPropertyChangeListener(new PropertyChangeHandler());
        switch (this.worker.getState()) {
            case PENDING:
                this.worker.execute();
                break;
        }
    }

    public static boolean isOpen() {
        return singleton != null && singleton.progress < 100;
    }

    public static void showProgress(String title, WindowAdapter closeListener) {
        try {
            if (singleton != null) {
                singleton.dispose();
                singleton = null;
            }

            if (GraphicsEnvironment.isHeadless())
                return;

            ProgressDialog dialog = new ProgressDialog(title);

            SwingWorker worker = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    while (dialog.progress < 100) {
                        setProgress(dialog.progress);
                    }
                    singleton = null;
                    return null;
                }

            };

            dialog.worker = worker;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    dialog.start();
                    dialog.setLocationRelativeTo(null);
                    dialog.setVisible(true);

                    if (closeListener != null)
                        dialog.addWindowListener(closeListener);
                }
            }).run();

            singleton = dialog;
        } catch (AWTError ex) {
            setProgress(100);
        }
    }

    public class PropertyChangeHandler implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("state")) {
                SwingWorker.StateValue state = (SwingWorker.StateValue) evt.getNewValue();
                switch (state) {
                    case DONE:
                        dispose();
                        break;
                }
            } else if (evt.getPropertyName().equals("progress")) {
                progressBar.setValue((int)evt.getNewValue());
            }
        }

    }

}