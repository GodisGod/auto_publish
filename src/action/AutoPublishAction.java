package action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import gui.AutoPublishDialog;
import utils.Constant;

import javax.swing.*;
import java.awt.*;

public class AutoPublishAction extends AnAction {

    public int width = Toolkit.getDefaultToolkit().getScreenSize().width;
    public int height = Toolkit.getDefaultToolkit().getScreenSize().height;
    int windowsWidth = 680;
    int windowsHeight = 500;

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        Constant.setProjectPath(e.getProject().getBasePath());
        Messages.showMessageDialog("Auto Publish !路径 = " + e.getProject().getBasePath(), "Information:" + e.getProject().getBasePath(), Messages.getInformationIcon());
//        Messages.showMessageDialog("Auto Publish !", "Information", Messages.getInformationIcon());
        AutoPublishDialog autoPublishDialog = new AutoPublishDialog();

        DataContext dataContext = e.getDataContext();
        Project project = e.getData(PlatformDataKeys.PROJECT);
//        Module module =(Module)dataContext.getData(DataConstants.MODULE);

        autoPublishDialog.initView();

        JFrame frame = new JFrame("AutoPublishDialog");
        frame.setContentPane(autoPublishDialog.getAuto_publish_panel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        frame.setResizable(false);
        frame.setAlwaysOnTop(true);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setBounds((width - windowsWidth) / 2,
                (height - windowsHeight) / 2, windowsWidth, windowsHeight);

    }
}