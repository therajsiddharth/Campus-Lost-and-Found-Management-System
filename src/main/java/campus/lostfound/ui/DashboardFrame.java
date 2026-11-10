package campus.lostfound.ui;

import campus.lostfound.dao.*;
import campus.lostfound.model.*;
import campus.lostfound.service.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class DashboardFrame extends JFrame {
    private final User user;
    private final ItemDAO items=new ItemDAO();
    private final ClaimDAO claims=new ClaimDAO();
    private final AdminDAO adminDAO=new AdminDAO();
    private final MatchingEngine matching=new MatchingEngine();
    private final DefaultTableModel itemModel=new DefaultTableModel(
        new String[]{"ID","Type","Name","Category ID","Location","Status","Reported"},0){
        public boolean isCellEditable(int r,int c){return false;}
    };
    private final JTable itemTable=new JTable(itemModel);

    public DashboardFrame(User user){
        this.user=user;setTitle("Campus Lost & Found | "+user.dashboardTitle());
        setDefaultCloseOperation(EXIT_ON_CLOSE);setSize(1000,650);setLocationRelativeTo(null);
        JTabbedPane tabs=new JTabbedPane();
        tabs.addTab("Browse items",browsePanel());
        if("STUDENT".equals(user.getRole())) {
            tabs.addTab("Report item",reportPanel());
            tabs.addTab("Submit claim",claimPanel());
            tabs.addTab("My activity",activityPanel());
        } else {
            tabs.addTab("Admin overview",adminPanel());
            tabs.addTab("Claim management",adminClaimsPanel());
            tabs.addTab("Item moderation",moderationPanel());
            tabs.addTab("User management",userManagementPanel());
            tabs.addTab("Categories",categoryPanel());
        }
        JPanel top=new JPanel(new BorderLayout());top.setBorder(BorderFactory.createEmptyBorder(8,12,8,12));
        top.add(new JLabel("Welcome, "+user.getFullName()+"  |  "+user.dashboardTitle()),BorderLayout.WEST);
        JButton logout=new JButton("Logout");logout.addActionListener(e->{dispose();new LoginFrame().setVisible(true);});top.add(logout,BorderLayout.EAST);
        add(top,BorderLayout.NORTH);add(tabs,BorderLayout.CENTER);refreshItems();
    }
    private JPanel browsePanel(){
        JPanel p=new JPanel(new BorderLayout(8,8));JPanel tools=new JPanel();
        JTextField search=new JTextField(20);JComboBox<String> type=new JComboBox<>(new String[]{"ALL","LOST","FOUND"});
        JButton find=new JButton("Search");find.addActionListener(e->refreshItems(search.getText(),(String)type.getSelectedItem()));
        tools.add(new JLabel("Search"));tools.add(search);tools.add(type);tools.add(find);
        p.add(tools,BorderLayout.NORTH);p.add(new JScrollPane(itemTable),BorderLayout.CENTER);
        JButton reload=new JButton("Refresh");reload.addActionListener(e->refreshItems());p.add(reload,BorderLayout.SOUTH);return p;
    }
    private JPanel reportPanel(){
        JPanel p=new JPanel(new GridLayout(0,2,10,10));p.setBorder(BorderFactory.createEmptyBorder(30,80,30,80));
        JTextField name=new JTextField(),category=new JTextField("1"),location=new JTextField(),desc=new JTextField();
        JComboBox<String> type=new JComboBox<>(new String[]{"LOST","FOUND"});
        p.add(new JLabel("Item name"));p.add(name);p.add(new JLabel("Category ID (see Categories)"));p.add(category);
        p.add(new JLabel("Location"));p.add(location);p.add(new JLabel("Description"));p.add(desc);
        p.add(new JLabel("Report type"));p.add(type);JButton submit=new JButton("Submit report");p.add(new JLabel());p.add(submit);
        submit.addActionListener(e->{try{
            if(name.getText().isBlank()||location.getText().isBlank())throw new IllegalArgumentException("Item name and location are required.");
            items.report(user.getUserId(),Integer.parseInt(category.getText().trim()),name.getText().trim(),desc.getText().trim(),location.getText().trim(),(String)type.getSelectedItem());
            JOptionPane.showMessageDialog(this,"Report submitted. It will appear publicly after admin approval.");refreshItems();
        }catch(Exception ex){error(ex);}});
        return p;
    }
    private JPanel claimPanel(){
        JPanel p=new JPanel(new GridLayout(0,2,10,10));p.setBorder(BorderFactory.createEmptyBorder(50,100,50,100));
        JTextField id=new JTextField(),proof=new JTextField();p.add(new JLabel("Found item ID"));p.add(id);p.add(new JLabel("Ownership proof (do not reveal public details)"));p.add(proof);
        JButton submit=new JButton("Submit claim");p.add(new JLabel());p.add(submit);
        submit.addActionListener(e->{try{if(id.getText().isBlank()||proof.getText().isBlank())throw new IllegalArgumentException("Enter item ID and proof.");
            claims.submit(Integer.parseInt(id.getText().trim()),user.getUserId(),proof.getText().trim());JOptionPane.showMessageDialog(this,"Claim submitted for admin review.");}
            catch(Exception ex){error(ex);}});return p;
    }
    private JPanel activityPanel(){
        JPanel p=new JPanel(new BorderLayout());JButton b=new JButton("View my claims");JTextArea out=new JTextArea();out.setEditable(false);
        b.addActionListener(e->{try{StringBuilder s=new StringBuilder();for(Claim c:claims.listClaims(false))if(c.getClaimantId()==user.getUserId())s.append(c).append("\n");out.setText(s.length()==0?"No claims yet.":s.toString());}catch(Exception ex){error(ex);}});
        p.add(b,BorderLayout.NORTH);p.add(new JScrollPane(out));return p;
    }
    private JPanel adminPanel(){
        JPanel p=new JPanel(new BorderLayout());JButton refresh=new JButton("Refresh statistics");JTextArea stats=new JTextArea();stats.setEditable(false);
        refresh.addActionListener(e->{try{AccessControl.requireAdmin(user);int[] s=items.stats();stats.setText("Total items: "+s[0]+"\nLost items: "+s[1]+"\nFound items: "+s[2]+"\nReturned items: "+s[3]);}catch(Exception ex){error(ex);}});
        p.add(refresh,BorderLayout.NORTH);p.add(new JScrollPane(stats));refresh.doClick();return p;
    }
    private JPanel adminClaimsPanel(){
        JPanel p=new JPanel(new BorderLayout());DefaultTableModel m=new DefaultTableModel(new String[]{"Claim ID","Item ID","Claimant ID","Proof","Status","Date"},0);
        JTable t=new JTable(m);JButton load=new JButton("Load pending claims"),approve=new JButton("Approve selected"),reject=new JButton("Reject selected");
        load.addActionListener(e->{try{AccessControl.requireAdmin(user);m.setRowCount(0);for(Claim c:claims.listClaims(true))m.addRow(new Object[]{c.getClaimId(),c.getItemId(),c.getClaimantId(),c.getProof(),c.getStatus(),c.getClaimDate()});}
            catch(Exception ex){error(ex);}});
        approve.addActionListener(e->reviewSelected(t,m,true));reject.addActionListener(e->reviewSelected(t,m,false));
        JPanel buttons=new JPanel();buttons.add(load);buttons.add(approve);buttons.add(reject);p.add(buttons,BorderLayout.NORTH);p.add(new JScrollPane(t));load.doClick();return p;
    }
    private void reviewSelected(JTable t,DefaultTableModel m,boolean approve){
        int row=t.getSelectedRow();if(row<0){JOptionPane.showMessageDialog(this,"Select a claim first.");return;}
        try{AccessControl.requireAdmin(user);claims.review((int)m.getValueAt(row,0),user.getUserId(),approve);JOptionPane.showMessageDialog(this,"Claim "+(approve?"approved":"rejected")+".");m.removeRow(row);}
        catch(Exception ex){error(ex);}
    }
    private JPanel moderationPanel(){
        JPanel p=new JPanel(new BorderLayout());JComboBox<String> status=new JComboBox<>(new String[]{"APPROVED","REJECTED","RETURNED","CLOSED"});
        JTextField id=new JTextField(8);JButton update=new JButton("Update selected item status");
        update.addActionListener(e->{try{AccessControl.requireAdmin(user);int itemId=Integer.parseInt(id.getText().trim());
            items.adminUpdateStatus(itemId,(String)status.getSelectedItem(),user.getUserId());JOptionPane.showMessageDialog(this,"Item status updated.");refreshItems();}
            catch(Exception ex){error(ex);}});
        JPanel bar=new JPanel();bar.add(new JLabel("Item ID"));bar.add(id);bar.add(status);bar.add(update);
        p.add(bar,BorderLayout.NORTH);p.add(new JLabel("Admin can moderate all listings. Enter an item ID from Browse items.",SwingConstants.CENTER));return p;
    }

    private JPanel userManagementPanel(){
        JPanel p=new JPanel(new BorderLayout());
        DefaultTableModel m=new DefaultTableModel(new String[]{"ID","Name","Student ID","Email","Role","Status","Created"},0){
            public boolean isCellEditable(int r,int c){return false;}
        };
        JTable t=new JTable(m);
        JButton load=new JButton("Refresh users"),activate=new JButton("Activate"),deactivate=new JButton("Deactivate");
        Runnable refresh=()->{try{AccessControl.requireAdmin(user);m.setRowCount(0);for(Object[] row:adminDAO.users())m.addRow(row);}catch(Exception ex){error(ex);}};
        load.addActionListener(e->refresh.run());
        activate.addActionListener(e->changeUserStatus(t,m,"ACTIVE"));
        deactivate.addActionListener(e->changeUserStatus(t,m,"INACTIVE"));
        JPanel bar=new JPanel();bar.add(load);bar.add(activate);bar.add(deactivate);
        p.add(bar,BorderLayout.NORTH);p.add(new JScrollPane(t));refresh.run();return p;
    }
    private void changeUserStatus(JTable t,DefaultTableModel m,String status){
        int row=t.getSelectedRow();if(row<0){JOptionPane.showMessageDialog(this,"Select a user first.");return;}
        try{AccessControl.requireAdmin(user);int id=(int)m.getValueAt(row,0);adminDAO.setUserStatus(id,status,user.getUserId());
            JOptionPane.showMessageDialog(this,"Student account "+status.toLowerCase()+".");}
        catch(Exception ex){error(ex);}
    }
    private JPanel categoryPanel(){
        JPanel p=new JPanel(new BorderLayout());
        DefaultTableModel m=new DefaultTableModel(new String[]{"ID","Category","Active"},0){
            public boolean isCellEditable(int r,int c){return false;}
        };
        JTable t=new JTable(m);JButton load=new JButton("Refresh"),add=new JButton("Add category"),toggle=new JButton("Activate / deactivate selected");
        Runnable refresh=()->{try{AccessControl.requireAdmin(user);m.setRowCount(0);for(Object[] row:adminDAO.categories())m.addRow(row);}catch(Exception ex){error(ex);}};
        load.addActionListener(e->refresh.run());
        add.addActionListener(e->{String name=JOptionPane.showInputDialog(this,"New category name:");if(name==null||name.isBlank())return;
            try{AccessControl.requireAdmin(user);adminDAO.addCategory(name);refresh.run();}catch(Exception ex){error(ex);}});
        toggle.addActionListener(e->{int row=t.getSelectedRow();if(row<0){JOptionPane.showMessageDialog(this,"Select a category first.");return;}
            try{AccessControl.requireAdmin(user);int id=(int)m.getValueAt(row,0);boolean active=(boolean)m.getValueAt(row,2);adminDAO.setCategoryActive(id,!active);refresh.run();}
            catch(Exception ex){error(ex);}});
        JPanel bar=new JPanel();bar.add(load);bar.add(add);bar.add(toggle);p.add(bar,BorderLayout.NORTH);p.add(new JScrollPane(t));refresh.run();return p;
    }

    private void refreshItems(){refreshItems("","ALL");}
    private void refreshItems(String q,String type){
        try{itemModel.setRowCount(0);List<Item> list=items.listItems(q,type,"ADMIN".equals(user.getRole()));
            // DSA: ArrayList stores rows; HashMap provides item-id lookup for the matching workflow.
            Map<Integer,Item> index=matching.indexById(list);
            for(Item i:list)itemModel.addRow(new Object[]{i.getItemId(),i.getItemType(),i.getItemName(),i.getCategoryId(),i.getLocation(),i.getStatus(),i.getDateReported()});
        }catch(Exception ex){error(ex);}
    }
    private void error(Exception ex){JOptionPane.showMessageDialog(this,ex.getMessage(),"Operation failed",JOptionPane.ERROR_MESSAGE);}
}
