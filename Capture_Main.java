import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Point;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.ArrayList;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.Container;
import java.awt.BorderLayout;
import javax.imageio.ImageIO;

public class Capture_Main{
  public static Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();

  public static void main(String[] args){
    Capture_Main app = new Capture_Main();
    app.run();
    Action.frame.setUndecorated(true);
  }

  void run(){
    SystemTray tray = SystemTray.getSystemTray();
    Image image = Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemResource("img/icon.png"));
    PopupMenu popup = new PopupMenu();
    TrayIcon icon = new TrayIcon(image, "Capture By Syulis", popup);
    icon.setImageAutoSize(true);

    MenuItem capture = new MenuItem("Capture All");
    capture.setActionCommand("capture");
    capture.addActionListener(new Menu_Listener());
    popup.add(capture);

    MenuItem record = new MenuItem("Record All");
    record.setActionCommand("record_start");
    record.addActionListener(new Menu_Listener());
    popup.add(record);

    MenuItem range = new MenuItem("Select Area");
    range.setActionCommand("range");
    range.addActionListener(new Menu_Listener());
    popup.add(range);

    // MenuItem config = new MenuItem("Config");
    // config.setActionCommand("config");
    // config.addActionListener(new Menu_Listener());
    // popup.add(config);

    MenuItem end = new MenuItem("End");
    end.setActionCommand("end");
    end.addActionListener(new Menu_Listener());
    popup.add(end);

    try {
        SystemTray.getSystemTray().add(icon);
    } catch (AWTException e){
        e.printStackTrace();
    }
  }
}


class Menu_Listener implements ActionListener{
  public void actionPerformed(ActionEvent e){
    try{
      Action action = new Action();
      if(e.getActionCommand() == "capture"){
        action.Capture();
      }
      if(e.getActionCommand() == "record_start"){
        Range_Listener.mouse_point[0] = 0;
        Range_Listener.mouse_point[1] = 0;
        Range_Listener.mouse_point[2] = Capture_Main.ss.width;
        Range_Listener.mouse_point[3] = Capture_Main.ss.height;
        action.Record_Start();
      }
      if(e.getActionCommand() == "record_end"){
        action.Record_End();
      }
      if(e.getActionCommand() == "record_calcel"){
        action.Record_Cancel();
      }
      if(e.getActionCommand() == "range"){
        action.Range_Set();
      }
      if(e.getActionCommand() == "range_capture"){
        action.Range_End();
        action.Range_Capture();
      }

      if(e.getActionCommand() == "range_record"){
        action.Record_Start();
      }

      if(e.getActionCommand() == "range_re"){
        Range_Listener.mouse_point[0] = 0;
        Range_Listener.mouse_point[1] = 0;
        Range_Listener.mouse_point[2] = Capture_Main.ss.width;
        Range_Listener.mouse_point[3] = Capture_Main.ss.height;
        action.Range_End();
        action.Range_Set();
      }

      if(e.getActionCommand() == "range_set"){
        action.Range_End();
      }

      if(e.getActionCommand() == "range_end"){
        Range_Listener.mouse_point[0] = 0;
        Range_Listener.mouse_point[1] = 0;
        Range_Listener.mouse_point[2] = Capture_Main.ss.width;
        Range_Listener.mouse_point[3] = Capture_Main.ss.height;
        action.Range_End();
      }

      if(e.getActionCommand() == "config"){
        // action.Range_Set();
      }

      if(e.getActionCommand() == "end"){
        System.exit(0);
      }
    }catch(AWTException ee){
      System.out.println(ee);
    }catch(IOException eee){
      System.out.println(eee);
    }catch(InterruptedException eeee){
      System.out.println(eeee);
    }
  }
}

class Action{
  public static JFrame frame = new JFrame("Range");
  public static String save_pass = "Captured/";
  public static boolean Started_Record = false;
  public static JFrame dialog = new JFrame();

  public void Capture() throws AWTException, IOException{
    Robot robot = new Robot();
    BufferedImage image = robot.createScreenCapture(new Rectangle(0, 0, Capture_Main.ss.width, Capture_Main.ss.height));
    Calendar cl = Calendar.getInstance();
    String Y = String.valueOf(cl.get(Calendar.YEAR)) + "-";
    String M = String.valueOf(cl.get(Calendar.MONTH)) + "-";
    String D = String.valueOf(cl.get(Calendar.DATE)) + "-";
    String h = String.valueOf(cl.get(Calendar.HOUR_OF_DAY)) + "-";
    String m = String.valueOf(cl.get(Calendar.MINUTE)) + "-";
    String s = String.valueOf(cl.get(Calendar.SECOND)) + "-";
    String file_name = Y + M + D + h + m + s + "Capture.png";
    ImageIO.write(image, "PNG", new File(Action.save_pass + file_name));
  }

  public void Record_Start() throws AWTException, IOException{
    if(Started_Record){
      return;
    }
    Action.frame.setVisible(false);
    Action.dialog.setVisible(false);
    Range_Listener.dialog.setVisible(false);
    Started_Record = true;
    Recording rd = new Recording();
    Thread record_thread = new Thread(rd);
    record_thread.start();
    dialog.setBounds(10, 10, 50, 110);
    dialog.addWindowListener(new WindowClosing());
    dialog.setVisible(true);

    JPanel record_panel = new JPanel();

    JButton red = new JButton();
    red.setActionCommand("record_end");
    red.addActionListener(new Menu_Listener());
    dialog.add(red, BorderLayout.NORTH);
    ImageIcon red_image = new ImageIcon("img/button/record.png");
    red.setIcon(red_image);

    JButton rcl = new JButton();
    rcl.setActionCommand("record_calcel");
    rcl.addActionListener(new Menu_Listener());
    dialog.add(rcl, BorderLayout.SOUTH);
    ImageIcon rcl_image = new ImageIcon("img/button/batu.png");
    rcl.setIcon(rcl_image);
  }

  public void Record_End() throws AWTException, IOException, InterruptedException{
    Started_Record = false;
    dialog.setVisible(false);

    JFrame wait = new JFrame("LOADING NOW");
    wait.setBounds(10, 10, 275, 0);
    wait.setAlwaysOnTop(true);
    wait.setVisible(true);

    File file = File.createTempFile("record_data", "");
    file.delete();
    file.mkdir();

    for(int i = 0; i < Recording.imgs_list.size(); i++){
      String file_name = "/" + String.valueOf(i) + ".png";
      ImageIO.write(Recording.imgs_list.get(i), "PNG", new File(file.getAbsolutePath() + file_name));
    }


    Calendar cl = Calendar.getInstance();
    String Y = String.valueOf(cl.get(Calendar.YEAR)) + "-";
    String M = String.valueOf(cl.get(Calendar.MONTH)) + "-";
    String D = String.valueOf(cl.get(Calendar.DATE)) + "-";
    String h = String.valueOf(cl.get(Calendar.HOUR_OF_DAY)) + "-";
    String m = String.valueOf(cl.get(Calendar.MINUTE)) + "-";
    String s = String.valueOf(cl.get(Calendar.SECOND)) + "-";
    String movie_name = Y + M + D + h + m + s + "Record.mp4";

    Process p = Runtime.getRuntime().exec("cmd /c start /wait ffmpeg/bin/ffmpeg -r 10 -i " + file.getAbsolutePath() + "/%d.png -vcodec libx264 -pix_fmt yuv420p -r 30 Captured/" + movie_name);
    p.waitFor();

    File[] filed = file.listFiles();
    for(int i=0; i<filed.length; i++) {
      filed[i].delete();
    }
    wait.setVisible(false);
    file.delete();
    Recording.imgs_list.clear();
  }

  public void Record_Cancel() throws AWTException, IOException{
    Started_Record = false;
    dialog.setVisible(false);
    Recording.imgs_list.clear();
  }

  public void Range_Set() throws AWTException{
    frame.setBounds(0, 0, Capture_Main.ss.width, Capture_Main.ss.height);
    frame.setOpacity(0.5f);
    frame.setVisible(true);
    JPanel panel = new JPanel();
    frame.getContentPane().add(panel);
    frame.addMouseListener(new Range_Listener());
  }

  public void Range_Capture() throws AWTException, IOException{
    Robot robot = new Robot();
    BufferedImage image = robot.createScreenCapture(new Rectangle(Range_Listener.mouse_point[0], Range_Listener.mouse_point[1], Range_Listener.mouse_point[2] - Range_Listener.mouse_point[0], Range_Listener.mouse_point[3] - Range_Listener.mouse_point[1]));
    Calendar cl = Calendar.getInstance();
    String Y = String.valueOf(cl.get(Calendar.YEAR)) + "-";
    String M = String.valueOf(cl.get(Calendar.MONTH)) + "-";
    String D = String.valueOf(cl.get(Calendar.DATE)) + "-";
    String h = String.valueOf(cl.get(Calendar.HOUR_OF_DAY)) + "-";
    String m = String.valueOf(cl.get(Calendar.MINUTE)) + "-";
    String s = String.valueOf(cl.get(Calendar.SECOND)) + "-";
    String file_name = Y + M + D + h + m + s + "Capture.png";
    ImageIO.write(image, "PNG", new File(save_pass + file_name));
  }

  public void Range_End(){
    frame.setVisible(false);
    Range_Listener.dialog.setVisible(false);
  }
}

class Recording implements Runnable{
  public static ArrayList<BufferedImage> imgs_list = new ArrayList<>();
  public void run(){
    try{
      Robot robot = new Robot();
      while(Action.Started_Record == true){
        BufferedImage image = robot.createScreenCapture(new Rectangle(Range_Listener.mouse_point[0], Range_Listener.mouse_point[1], Range_Listener.mouse_point[2] - Range_Listener.mouse_point[0], Range_Listener.mouse_point[3] - Range_Listener.mouse_point[1]));
        imgs_list.add(image);
        robot.delay(5);
      }
    }catch(AWTException e){
      System.out.println(e);
    }
  }
}

class Range_Listener implements MouseListener{
  Graphics g = Action.frame.getGraphics();
  public static JDialog dialog = new JDialog();
  public static int mouse_point[] = new int [4];
  boolean choiced = false;
  public void mouseClicked(MouseEvent e){
  }

  public void mouseEntered(MouseEvent e){
  }

  public void mouseExited(MouseEvent e){
  }

  public void mousePressed(MouseEvent e){
    if(choiced){
      return;
    }
    Point point = e.getPoint();
    mouse_point[0] = point.x;
    mouse_point[1] = point.y;
    g.setColor(new Color(255, 0, 255));
    g.drawLine(mouse_point[0] - 15, mouse_point[1], mouse_point[0] + 15, mouse_point[1]);
    g.drawLine(mouse_point[0], mouse_point[1] - 15, mouse_point[0], mouse_point[1] + 15);
  }

  public void mouseReleased(MouseEvent e){
    if(choiced){
      return;
    }
    g.drawLine(mouse_point[2], mouse_point[3], mouse_point[2], mouse_point[3]);
    Point point = e.getPoint();
    mouse_point[2] = point.x;
    mouse_point[3] = point.y;
    g.drawLine(mouse_point[2] - 15, mouse_point[3], mouse_point[2] + 15, mouse_point[3]);
    g.drawLine(mouse_point[2], mouse_point[3] - 15, mouse_point[2], mouse_point[3] + 15);
    g.drawLine(mouse_point[0], mouse_point[1], mouse_point[2], mouse_point[1]);
    g.drawLine(mouse_point[0], mouse_point[1], mouse_point[0], mouse_point[3]);
    g.drawLine(mouse_point[0], mouse_point[3], mouse_point[2], mouse_point[3]);
    g.drawLine(mouse_point[2], mouse_point[1], mouse_point[2], mouse_point[3]);

    if(mouse_point[0] > mouse_point[2]){
      mouse_point[0] = mouse_point[0] + mouse_point[2];
      mouse_point[2] = mouse_point[0] - mouse_point[2];
      mouse_point[0] = mouse_point[0] - mouse_point[2];
    }
    if(mouse_point[3] < mouse_point[1]){
      mouse_point[1] = mouse_point[1] + mouse_point[3];
      mouse_point[3] = mouse_point[1] - mouse_point[3];
      mouse_point[1] = mouse_point[1] - mouse_point[3];
    }

    dialog.setBounds(10, 10, 50, 160);
    dialog.setAlwaysOnTop(true);
    dialog.addWindowListener(new WindowClosing());
    dialog.setVisible(true);

    JPanel range_panel = new JPanel();

    JButton rre = new JButton();
    rre.setActionCommand("range_re");
    rre.addActionListener(new Menu_Listener());
    dialog.add(rre, BorderLayout.NORTH);
    ImageIcon rre_image = new ImageIcon("img/button/rerange.jpg");
    rre.setIcon(rre_image);

    JButton rcap = new JButton();
    rcap.setActionCommand("range_capture");
    rcap.addActionListener(new Menu_Listener());
    dialog.add(rcap, BorderLayout.WEST);
    ImageIcon rcap_image = new ImageIcon("img/icon.png");
    rcap.setIcon(rcap_image);

    JButton rst = new JButton();
    rst.setActionCommand("range_set");
    rst.addActionListener(new Menu_Listener());
    dialog.add(rst, BorderLayout.CENTER);
    ImageIcon rst_image = new ImageIcon("img/button/set_point.jpg");
    rst.setIcon(rst_image);

    JButton rrc = new JButton();
    rrc.setActionCommand("range_record");
    rrc.addActionListener(new Menu_Listener());
    dialog.add(rrc, BorderLayout.EAST);
    ImageIcon rrc_image = new ImageIcon("img/button/record.png");
    rrc.setIcon(rrc_image);

    JButton rend = new JButton();
    rend.setActionCommand("range_end");
    rend.addActionListener(new Menu_Listener());
    dialog.add(rend, BorderLayout.SOUTH);
    ImageIcon rend_image = new ImageIcon("img/button/batu.png");
    rend.setIcon(rend_image);

    choiced = true;
  }
}

class WindowClosing extends WindowAdapter{
  public void windowClosing(WindowEvent e){
    Action.Started_Record = false;
    Action.frame.setVisible(false);
    Action.dialog.setVisible(false);
    Range_Listener.dialog.setVisible(false);
  }
}
