package gui;

import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBScrollPane;
import org.jdesktop.swingx.VerticalLayout;
import utils.ExecuteResult;
import utils.GradleTools;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private JTextArea textArea1;

    static JScrollBar scrollBar;
    private DefaultCaret defaultCaret;
    private static AtomicBoolean execute = new AtomicBoolean();
    private boolean isExecute = false;
    private long gradleKey;

    public JPanel getAuto_publish_panel() {
        return auto_publish_panel;
    }

    public void initView() {


        //不可编辑
        textArea1.setEditable(false);
        //自动换行
        textArea1.setLineWrap(true);
        //断行不会分割单词
        textArea1.setWrapStyleWord(true);
        textArea1.setBorder(new EmptyBorder(5, 10, 10, 5));

        scrollPane.setViewportView(textArea1);
        scrollBar = scrollPane.getVerticalScrollBar();

        ButtonGroup group = new ButtonGroup();
        group.add(debugRadioButton);
        group.add(releaseRadioButton);
        group.add(channelRadioButton);

        //设置默认选中debug
        group.setSelected(debugRadioButton.getModel(), true);

        logButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Messages.showMessageDialog("Auto Publish !", "展示日志", Messages.getInformationIcon());
            }
        });

        switchStatus(false);
        publishButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!execute.get()) {
                    publish("正式版本，日志测试");
                } else {
                    if (isExecute) {
                        GradleTools.instance().stopGradle(gradleKey);
                    }
                }
            }
        });

        defaultCaret = (DefaultCaret) textArea1.getCaret();
        GradleTools.instance().setExecuteListener(new GradleTools.ExecuteListener() {
            @Override
            public void onExecuteStart() {
                System.out.println("LHD -----onExecuteStart------当前线程id = " + Thread.currentThread().getId());
                textArea1.setText("开始");
                isExecute = true;
            }

            @Override
            public void onExecute(String line) {
                System.out.println("LHD -----onExecute------当前线程id = " + Thread.currentThread().getId());
//                System.out.println("LHD -----读取线程输出 = " + line + "---------LHD");
                textArea1.append("\n");
                textArea1.append(line);
                textArea1.paintImmediately(textArea1.getBounds());
//                textArea1.paintImmediately(textArea1.getX(), textArea1.getY(), textArea1.getWidth(), textArea1.getHeight());
//                textArea1.setCaretPosition(textArea1.getDocument().getLength());

//                defaultCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

//                textArea1.requestFocus();

            }

            @Override
            public void onExecuteEnd() {
                System.out.println("LHD -----onExecuteEnd------当前线程id = " + Thread.currentThread().getId());
                textArea1.append("\n");
                textArea1.append("执行结束");
                isExecute = false;
            }
        });

    }

    public void publish(String log) {
        switchStatus(true);
        new Thread(() -> {
            gradleKey = Thread.currentThread().getId();
            System.out.println("LHD  当前发布线程的id = " + gradleKey);
            ExecuteResult result = null;
            if (debugRadioButton.isSelected()) {
                result = GradleTools.instance().deploy(GradleTools.PUBLISH_TYPE.DEBUG, "app", log);
            }
            if (releaseRadioButton.isSelected()) {
                result = GradleTools.instance().deploy(GradleTools.PUBLISH_TYPE.RELEASE, "app", log);
            }
            if (result == null) {
                return;
            }
            if (result.isSuccess()) {
                //发布成功去检查一下最新版本
            }
            switchStatus(false);
        }).start();
    }


    public void switchStatus(boolean deploying) {
        System.out.println("LHD --switchStatus--发布中止 id1 = " + Thread.currentThread().getId());
        execute.getAndSet(deploying);
        isExecute = deploying;
        if (deploying) {
            publishButton.setText("中止");
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

    private void checkBtnState(boolean isPublish) {
        publishButton.setText(isPublish ? "中止" : "发布");
    }

}
