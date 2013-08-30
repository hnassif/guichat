package gui;

import java.awt.Container;
import javax.swing.*;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

@SuppressWarnings("serial")
public class HistoryGUI extends JFrame{
    private JTextArea allMessages; //main text box
    private JScrollPane allMessagescrollTab;

    public HistoryGUI(int id, String history){
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        Container container=this.getContentPane();
        GroupLayout layout=new GroupLayout(container);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        container.setLayout(layout);

        allMessages=new JTextArea("",30,20);
        allMessages.setName("allMessages");
        allMessages.setEditable(false);
        allMessages.setText(history);

        allMessagescrollTab= new JScrollPane(allMessages);
        allMessagescrollTab.setName("allMessages");

        ParallelGroup paraGroup=layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        SequentialGroup settingsS=layout.createSequentialGroup();

        SequentialGroup messageManagement=layout.createSequentialGroup();
        paraGroup.addGroup(settingsS);
        paraGroup.addComponent(allMessages);
        paraGroup.addGroup(messageManagement);

        SequentialGroup seqGroup=layout.createSequentialGroup();
        ParallelGroup userManagement=layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        ParallelGroup messagingGroup=layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        seqGroup.addGroup(userManagement);
        seqGroup.addComponent(allMessages);
        seqGroup.addGroup(messagingGroup);

        layout.setHorizontalGroup(paraGroup);
        layout.setVerticalGroup(seqGroup);

        // callHistory(convID);

        this.pack();

    }
   
   
}