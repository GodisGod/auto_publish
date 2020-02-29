package gui;

import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBScrollPane;
import org.jdesktop.swingx.VerticalLayout;
import utils.ExecuteResult;
import utils.GradleTools;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.atomic.AtomicBoolean;

public class AutoPublishDialog {
    private JTextField a496TextField;
    private JTextField textField1;
    private JRadioButton debugRadioButton;
    private JRadioButton releaseRadioButton;
    private JRadioButton channelRadioButton;
    private JTextField textField2;
    private JButton publishButton;
    private JPanel auto_publish_panel;
    private JButton logButton;
    private JScrollPane scrollPane;
    private JPanel areaJpane;
    private JTextArea textArea1;

    private static AtomicBoolean execute = new AtomicBoolean();
    private boolean isExecute = false;

    public JPanel getAuto_publish_panel() {
        return auto_publish_panel;
    }

    public void initView() {

        areaJpane.setLayout(new VerticalLayout());
        areaJpane.setBorder(new EmptyBorder(5, 5, 5, 5));

        //不可编辑
        textArea1.setEditable(false);
        //自动换行
        textArea1.setLineWrap(true);
        //断行不断字
        textArea1.setWrapStyleWord(true);

        scrollPane.setViewportView(textArea1);

        ButtonGroup group = new ButtonGroup();
        group.add(debugRadioButton);
        group.add(releaseRadioButton);
        group.add(channelRadioButton);

        //设置默认选中debug
        group.setSelected(debugRadioButton.getModel(), true);

        switchStatus(false);
        logButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Messages.showMessageDialog("Auto Publish !", "展示日志", Messages.getInformationIcon());
            }
        });

        publishButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (debugRadioButton.isSelected()) {
                    ExecuteResult result = GradleTools.instance().deploy(GradleTools.PUBLISH_TYPE.DEBUG, "app", "测试");
                }
                if (releaseRadioButton.isSelected()) {
                    ExecuteResult result = GradleTools.instance().deploy(GradleTools.PUBLISH_TYPE.RELEASE, "app", "测试");
                }
            }
        });

        GradleTools.instance().setExecuteListener(new GradleTools.ExecuteListener() {
            @Override
            public void onExecuteStart() {
                textArea1.setText("");
            }

            @Override
            public void onExecute(String line) {
                textArea1.append("\n");
                textArea1.append(line);
                textArea1.paintImmediately(textArea1.getBounds());
                textArea1.paintImmediately(textArea1.getX(), textArea1.getY(), textArea1.getWidth(), textArea1.getHeight());
                textArea1.setCaretPosition(textArea1.getDocument().getLength());
                textArea1.invalidate();
                textArea1.repaint();
            }

            @Override
            public void onExecuteEnd() {
                textArea1.append("\n");
                textArea1.append("执行结束");
            }
        });

    }

    public void switchStatus(boolean deploying) {
        execute.getAndSet(deploying);
        isExecute = deploying;
        if (deploying) {
            publishButton.setText("停止");
        } else {
            publishButton.setText("发布");
        }
    }

    public void startPublish() {
        boolean isDebug = debugRadioButton.isSelected();
        boolean isRelease = releaseRadioButton.isSelected();
        boolean isChannel = channelRadioButton.isSelected();

        if (isDebug) {
            //todo 执行debug流程
            //clean
            //
        }

        if (isRelease) {

        }

        if (isChannel) {

        }

    }


}
