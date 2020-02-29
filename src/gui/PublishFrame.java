package gui;

import com.intellij.ui.components.JBScrollPane;
import org.jdesktop.swingx.VerticalLayout;
import utils.GradleTools;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class PublishFrame extends JFrame {

    private static final long serialVersionUID = -1L;

    public static void main(String[] args) {
        PublishFrame publishFrame = new PublishFrame();
        publishFrame.setVisible(true);
    }

    public int width = Toolkit.getDefaultToolkit().getScreenSize().width;
    public int height = Toolkit.getDefaultToolkit().getScreenSize().height;
    int windowsWidth = 680;
    int windowsHeight = 600;

    public PublishFrame() {
        setResizable(false);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setBounds((width - windowsWidth) / 2,
                (height - windowsHeight) / 2, windowsWidth, windowsHeight);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2));
        setContentPane(panel);
        panel.add(initArea());
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {

            }

            @Override
            public void windowClosed(WindowEvent e) {
                //todo 停止编译
                GradleTools.instance().stopAll();
            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
    }

    public JComponent initArea() {
        //添加text
        JScrollPane scrollPane = new JBScrollPane();
        JPanel panel = new JPanel();
        panel.setLayout(new VerticalLayout());
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        //添加打印
        JTextArea jTextArea = new JTextArea();
        jTextArea.setEditable(false);
        jTextArea.setBackground(null);
        jTextArea.setLineWrap(true);
        jTextArea.setWrapStyleWord(true);
        scrollPane.setViewportView(panel);
        panel.add(jTextArea);

        GradleTools.instance().setExecuteListener(new GradleTools.ExecuteListener() {
            @Override
            public void onExecuteStart() {
                jTextArea.setText("");
            }

            @Override
            public void onExecute(String line) {
                jTextArea.append("\n");
                jTextArea.append(line);
                jTextArea.paintImmediately(jTextArea.getBounds());
                jTextArea.paintImmediately(jTextArea.getX(), jTextArea.getY(), jTextArea.getWidth(), jTextArea.getHeight());
                jTextArea.setCaretPosition(jTextArea.getDocument().getLength());
            }

            @Override
            public void onExecuteEnd() {

            }
        });

        return scrollPane;

    }

}
