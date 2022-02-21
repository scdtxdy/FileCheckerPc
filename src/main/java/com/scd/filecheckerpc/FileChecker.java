package com.scd.filecheckerpc;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Timer;
import java.util.*;

/**
 * 文件内容检索工具
 *
 * @author liaolongjun 2015年5月30日 下午2:39:07
 */
final class FileChecker {

  private final String HISTORY_SEARCH_CONTENT = "historySearchContent";
  private final String HISTORY_SEARCH_PATH = "historySearchPath";
  private JButton btnSelect,

  btnOK,

  btnClear;
  private JPanel checkBoxPanel,

  seachLimitPanel,

  fileSelectPanel;
  private List exts;
  /**
   * JFileChooser 文件选择组件
   */
  private JFileChooser fc;
  private String filePath;
  private JFrame fm;
  /**
   * 保存上一次查询痕迹的文件
   */
  private File historyFile;
  private boolean isBreak;
  /**
   * 文件类型
   */
  private JCheckBox java,

  html,

  xml,

  jsp,

  js,

  css,

  isbreak,

  all,

  docx,

  isAutoClear;
  /**
   * 检索的文件数目
   */
  private int numFiles;
  /**
   * 满足查询条件的数目
   */
  private int numResults;
  /**
   * 内容输出区域
   */
  private JTextArea outputArea;
  private Properties props;
  private JScrollPane scrollPane;
  private String searchContent;
  private List<JCheckBox> suffixs;
  private JTextField tfSearchContent,

  tfFilePath;

  public FileChecker() {


    init();

    initFm();

    initBtns();

    initTextFileds();

    initFileChooser();

    initCheckBoxPanel();

    initSeachLimitPanel();

    initFileSelectPanel();

    initOutputArea();

    initListener();

//    add(checkBoxPanel, java, html, xml, jsp, js, css, isbreak, all);

    add(checkBoxPanel, docx, isbreak);

    add(seachLimitPanel, checkBoxPanel, tfSearchContent);

    add(fileSelectPanel, btnSelect, tfFilePath, btnOK, btnClear, isAutoClear);

    add(fm, seachLimitPanel, fileSelectPanel, scrollPane);

    fm.setVisible(true);

    fm.validate();

  }

//  public static void main(String[] args) {
//
//
//    new FileChecker();
//
//  }

  /**
   * 添加组件
   */
  private void add(Container parent, JComponent... components) {


    for (JComponent c : components) {
      parent.add(c);
    }



  }

  private void check(final File file) {


    if (isAutoClear.isSelected()) {
      btnClear.doClick();
    }

    outputArea.append("文件检索中......\n\n");

    numFiles = 0;

    numResults = 0;

    new Timer().schedule(new TimerTask() {


      @Override
      public void run() {


        try {


          if (exts == null) {
            check1(file);
          } else {
            check2(file);
          }

          outputArea.append("\n*\n* 执行结束，一共检索了 " + numFiles + " 个文件。 符合条件：" + numResults + "\n*\n");

          outputArea.append("\nHISTORY：" + historyFile + "\n\n");

        } catch (Exception e) {


          outputException(e);

        }

        JScrollBar jscrollBar = scrollPane.getVerticalScrollBar();

        jscrollBar.setValue(jscrollBar.getMaximum());

      }

    }, 50);

  }

  /**
   * 没有文件类型限制
   */
  private void check1(File file) throws IOException {


    File[] files = file.listFiles();

    for (File f : files) {
      if (f.isFile()) {
        read(f);
      } else {
        check1(f);
      }
    }

  }

  /**
   * 有文件类型限制
   */
  private void check2(File file) throws IOException {


    File[] files = file.listFiles();

    for (File f : files) {
      if (f.isFile()) {


        String ext = getExt(f);

        if (ext != null && exts.contains(ext)) {
          read(f);
        }

      } else {
        check2(f);
      }
    }

  }

  private void fillSuffixs(JCheckBox... cbs) {


    for (JCheckBox cb : cbs) {
      suffixs.add(cb);
    }

  }

  /**
   * 获取文件后缀(文件扩展名)
   */
  private String getExt(File file) {


    String fileName = file.getName();

    return fileName.lastIndexOf(".") == -1 ? null : fileName.substring(fileName.lastIndexOf(".") + 1);

  }

  /**
   * 获取要限制查找范围的文件后缀名(文件扩展名)
   */
  private List getExts() {


    List exts = new ArrayList();

    for (JCheckBox cb : suffixs) {
      if (cb.isSelected()) {
        exts.add(cb.getText());
      }
    }

    return exts.size() > 0 ? exts : null; // 如果没有文件限制，返回null

  }

  private void init() {


    try {


// 设置为当前操作系统的皮肤

      String lookAndFeel = UIManager.getSystemLookAndFeelClassName();

      UIManager.setLookAndFeel(lookAndFeel);

    } catch (Exception e) {


      outputException(e);

    }

// 读取历史记录

    URL url = FileChecker.class.getResource("history.properties");// 如果没有，则在当前class目录下创建

    historyFile = url == null ? new File(FileChecker.class.getResource("").getPath() + "/history.properties") : new File(url.getPath());

    props = new Properties();

    try {


      props.load(new FileInputStream(historyFile));

      filePath = props.getProperty(HISTORY_SEARCH_PATH);

      searchContent = props.getProperty(HISTORY_SEARCH_CONTENT);

    } catch (Exception e) {


// outputException(e); // 抓到异常不处理

    }

  }

  private void initBtns() {


    btnSelect = new MyBtn("OPEN.....");

    btnOK = new MyBtn("OK");

    btnClear = new MyBtn("CLEAR");

  }

  /**
   * 文件类型过滤(.suffix)
   */
  private void initCheckBoxPanel() {


    java = new JCheckBox("java", true);

    html = new JCheckBox("html", true);

    xml = new JCheckBox("xml");

    jsp = new JCheckBox("jsp", true);

    js = new JCheckBox("js");

    css = new JCheckBox("css");

    docx = new JCheckBox("docx");

    isbreak = new JCheckBox("isbreak", true); // 匹配到一个后是否跳出当前文件

    isAutoClear = new JCheckBox("AUTOCLEAR", true); // 自动清除

    all = new JCheckBox("all");// 全选

    checkBoxPanel = new JPanel();

    suffixs = new ArrayList();

//    fillSuffixs(java, html, xml, jsp, js, css, docx);
    fillSuffixs(docx);

  }

  private void initFileChooser() {


// 文件选择器设置桌面默认路径

    fc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

// 设置只能选择文件夹

    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

  }

  private void initFileSelectPanel() {


    fileSelectPanel = new JPanel();

    fileSelectPanel.setBounds(3, 55, 980, 40);

    fileSelectPanel.setBackground(Color.white);

  }

  private void initFm() {


    fm = new JFrame("FILE CONTENT CHECKER . liaolongjun . ^(o o)^") {

      private static final long serialVersionUID = 1L;

      @Override
      protected void processWindowEvent(WindowEvent e) {


        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
          saveHistory(); // 关闭窗口前，先保存上一次的查询记录
        }

        super.processWindowEvent(e);

      }

    };

    fm.setLayout(null);

    fm.setSize(1000, 700);

    fm.setResizable(false);

    fm.setLocationRelativeTo(null);

    fm.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    Random ran = new Random();

// 每次打开时, 背景色都是随机的

    fm.getContentPane().setBackground(new Color(ran.nextInt(255), ran.nextInt(255), ran.nextInt(255)));

  }

  private void initListener() {


    btnSelect.addActionListener(new ActionListener() {


      @Override
      public void actionPerformed(ActionEvent e) {


        if (fc.showOpenDialog(fm) == JFileChooser.APPROVE_OPTION) {
          tfFilePath.setText(fc.getSelectedFile().toString());
        }

      }

    });

    btnOK.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {


// 点确定后，重置查询限制条件

        resetLimitation();

        if (filePath == null || filePath.trim().length() == 0) {
          JOptionPane.showMessageDialog(fm, "未指定路径");
        } else if (searchContent == null || searchContent.trim().length() == 0) {
          JOptionPane.showMessageDialog(fm, "未指定查询内容");
        } else {
          check(new File(filePath));
        }

      }

    });

    btnClear.addActionListener(new ActionListener() {


      @Override
      public void actionPerformed(ActionEvent e) {


        outputArea.setText("");

      }

    });

    all.addActionListener(new ActionListener() {


      @Override
      public void actionPerformed(ActionEvent e) {


        setAllSelected(all.isSelected(), suffixs);

      }

    });

  }

  private void initOutputArea() {


    outputArea = new JTextArea();

    outputArea.setAutoscrolls(true);

// JTextArea 要放在 JScrollPane 里才有滚动效果(同JTable)

    scrollPane = new JScrollPane(outputArea);

    scrollPane.setBounds(7, 100, 980, 565);

  }

  private void initSeachLimitPanel() {


    seachLimitPanel = new JPanel();

    seachLimitPanel.setBounds(7, 5, 980, 45);

    seachLimitPanel.setBackground(Color.white);

  }

  private void initTextFileds() {


// 文件路径

    tfFilePath = new JTextField(filePath);

    tfFilePath.setPreferredSize(new Dimension(560, 30));

// 搜索内容

    tfSearchContent = new JTextField(searchContent);

    tfSearchContent.setPreferredSize(new Dimension(400, 30));

  }

  /**
   * 打印异常信息
   */
  private void outputException(Exception e) {


    outputArea.append(e + "\n");

    for (StackTraceElement s : e.getStackTrace()) {
      outputArea.append("" + s + "\n");
    }

  }

  private void read(File file) throws IOException {


    numFiles++;

    InputStreamReader fr=new InputStreamReader(new FileInputStream(file),"GB2312");
//    FileReader fr = new FileReader(file);

    BufferedReader br = new BufferedReader(fr);



    String str;

    int rowNum = 0; // 行号

    if (isBreak) {
      while ((str = br.readLine()) != null) {


        rowNum++;

        if (str.indexOf(searchContent) != -1) {


          numResults++;

          outputArea.append(file.toString() + "\n" + "行号: " + rowNum + "" + str + "\n");

          break;

        }

      }
    } else {


      StringBuilder sb = new StringBuilder(file.toString() + "\n");

      boolean flag = false;

      while ((str = br.readLine()) != null) {


        rowNum++;

        if (str.indexOf(searchContent) != -1) {


          flag = true;

          sb.append("行号: " + rowNum + "" + str + "\n");

        }

      }

      if (flag) {


        numResults++;

        outputArea.append(sb.toString());

      }

    }

    br.close();

  }

  /**
   * 重置查询的限制条件
   */
  private void resetLimitation() {


    exts = getExts();

    isBreak = isbreak.isSelected();

    filePath = tfFilePath.getText();

    searchContent = tfSearchContent.getText();

  }

  /**
   * 保存查询痕迹
   */
  private void saveHistory() {


    try {


      props.setProperty(HISTORY_SEARCH_PATH, filePath == null ? "" : filePath);

      props.setProperty(HISTORY_SEARCH_CONTENT, searchContent == null ? "" : searchContent);

      props.store(new FileOutputStream(historyFile), "--no comment--");

    } catch (IOException e) {


      e.printStackTrace();

    }

  }

  /**
   * 是否全选
   */
  private void setAllSelected(boolean boo, List<JCheckBox> cbs) {


    for (JCheckBox cb : cbs) {
      cb.setSelected(boo);
    }

    isbreak.setSelected(boo); // isbreak不在suffixs里, 所以在这里单独设置上

  }

  /**
   * 订制规格一致的按钮
   */
  private class MyBtn extends JButton {

    private static final long serialVersionUID = 1L;

    public MyBtn(String btnName) {


      super(btnName);

      setPreferredSize(new Dimension(100, 30));

    }

  }

}
