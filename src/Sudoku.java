import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;


public class Sudoku extends JApplet {
	/**
     * 已填空格之計數器
     */
    int latticeCounter = 0;
    /**
     * 根據難度所產生出來的題目解
     */
    int[][] title = new int[10][10];
    /**
     * 數獨格子棋盤之按鈕
     */
    JButton[][] cells;
    /**
     * 時間(秒)
     */
    long sec;
    /**
     * 時間文字方塊
     */
    JLabel timeL;
    /**
     * 時間監聽器
     */
    Timer time;
    /**
     * 時間暫停
     */
    boolean stop = false;
    /**
     * 時間暫停時棋盤上的記號
     */
    boolean[][] stopFlag = new boolean[9][9];
    /**
     * 暫停時用替換TEXT
     */
    String[][] tmpCells = new String[9][9];
    /**
     * 各種按鈕
     */
    JButton btnTimeStop, btnNewHardGame, btnNewMiddleGame, btnNewEasyGame, btnShowErrors;
    /**
     * 計數器之文字方塊
     */
    JLabel[] lattice = {new JLabel("剩餘空格數:"), new JLabel(" ")};
    /**
     * The current number which will be set to cell
     */
    String currentNumber = null; // current choose value [1..9]
    /**
     * 難度
     */
    Rank rank;

    /**
     * Handle the events from 9x9 buttons
     */
    class ButtonsListner implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            if (((JButton) arg0.getSource()).getForeground() == Color.red) {
                ((JButton) arg0.getSource()).setForeground(Color.black);
            }
            ((JButton) arg0.getSource()).setText(currentNumber);
            setLatticeCounter();
        }
    }

    /**
     * Handle the events from 9 radio buttons
     */
    class RadioButtonsListner implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            // set which number will go to cells
            currentNumber = arg0.getActionCommand();
        }
    }

    /**
     * default constructor to initialize Sudoku's GUI， 增加井字畫面、計時器、空格數計算
     */
    public Sudoku() {
        // create listeners
        ButtonsListner btnsListner = new ButtonsListner();
        RadioButtonsListner radoiButtonsListner = new RadioButtonsListner();
        // create 9x9 buttons
        JPanel panelCells = new JPanel();
        getContentPane().add(panelCells, BorderLayout.CENTER);
        panelCells.setLayout(new GridLayout(3, 3, 5, 5));  //GridLayout(int rows, int cols, int hgap(水平間距), int vgap(垂直間距))
        JPanel[][] JP = new JPanel[3][3];  //目的為井字畫面
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                panelCells.add(JP[i][j] = new JPanel());
                JP[i][j].setLayout(new GridLayout(3, 3, 2, 2));
            }
        }
        panelCells.setBackground(new Color(0, 150, 255));  //目的為井字畫面
        cells = new JButton[9][9];
        for (int i = 0; i < cells[0].length; i++) {
            for (int j = 0; j < cells[1].length; j++) {
                JP[i / 3][j / 3].add(cells[i][j] = new JButton());
                cells[i][j].addActionListener(btnsListner);
                cells[i][j].setEnabled(false);
                cells[i][j].setFont(new Font("Dialog",0, 24));//將按鈕字體放大
            }
        }
        // create 9 Radio buttons
        JPanel panelButtons = new JPanel();
        getContentPane().add(panelButtons, BorderLayout.NORTH);
        ButtonGroup btg = new ButtonGroup();
        JRadioButton[] numberButtons = new JRadioButton[9];
        for (int i = 0; i < 9; i++) {
            panelButtons.add(numberButtons[i] = new JRadioButton("" + (i + 1)));
            btg.add(numberButtons[i]);
            numberButtons[i].addActionListener(radoiButtonsListner);
            numberButtons[i].setMnemonic(49 + i);
        }
        numberButtons[0].setSelected(true);//將預先核取方快設為數字1
        // create new game button (note that you may extend to hard, easy ..buttons)
        JPanel panelFunctions = new JPanel();
        getContentPane().add(panelFunctions, BorderLayout.SOUTH);
        panelFunctions.setLayout(new GridLayout(2, 1, 9, 9));

        JPanel[] tJP = new JPanel[2];
        panelFunctions.add(tJP[0] = new JPanel());
        panelFunctions.add(tJP[1] = new JPanel());
        timeL = new JLabel("0分0秒");
        tJP[0].add(timeL);
        btnTimeStop = new JButton("暫停遊戲"); // 時間暫停
        btnTimeStop.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {   //暫停遊戲與記敘遊戲按鈕動作
                if (stop) {
                    stop = false;
                    btnTimeStop.setText("暫停遊戲");
                    time.start();//從起記時器
                    btnNewEasyGame.setEnabled(true);
                    btnNewHardGame.setEnabled(true);
                    btnNewMiddleGame.setEnabled(true);
                    btnShowErrors.setEnabled(true);
                } else {
                    stop = true;
                    btnTimeStop.setText("繼續遊戲");
                    time.stop();//暫停記時器
                    btnNewEasyGame.setEnabled(false);
                    btnNewHardGame.setEnabled(false);
                    btnNewMiddleGame.setEnabled(false);
                    btnShowErrors.setEnabled(false);
                }
                for (int i = 0; i < 9; i++) {  //讚停時隱藏Text，把Text放進預先寫好的空陣列並洗白
                    for (int j = 0; j < 9; j++) {
                        if (stop) {
                            tmpCells[i][j] = cells[i][j].getText();
                            cells[i][j].setText(" ");
                        } else {
                            cells[i][j].setText(tmpCells[i][j]);//把Text從新拿回來
                        }
                        if (stopFlag[i][j]) {
                            if (stop) {
                                cells[i][j].setEnabled(false);
                            } else {
                                cells[i][j].setEnabled(true);
                            }
                        }
                    }
                }
            }
        });
        tJP[0].add(btnTimeStop);
        btnTimeStop.setEnabled(false);
        tJP[0].add(lattice[0]);
        tJP[0].add(lattice[1]);
        btnNewEasyGame = new JButton("\u7C21\u55AE"); // 簡單
        tJP[1].add(btnNewEasyGame);
        btnNewEasyGame.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                createEasyGame();
            }
        });
        btnNewMiddleGame = new JButton("\u4E2D\u7B49");  // 中等
        btnNewMiddleGame.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                createMiddleGame();
            }
        });
        tJP[1].add(btnNewMiddleGame);
        btnNewHardGame = new JButton("\u56F0\u96E3"); // 困難
        btnNewHardGame.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                createHardGame();
            }
        });
        tJP[1].add(btnNewHardGame);
        btnShowErrors = new JButton("Show Errors");
        tJP[1].add(btnShowErrors);
        btnShowErrors.setEnabled(false);
        btnShowErrors.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                showErrors();
            }
        });
        ActionListener timeActionListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setTime();
            }
        };
        time = new Timer(1000, timeActionListener); //時間排程，用來實作記時器

    }

    /**
     * check and show error，把錯的用紅色顯示
     */
    void showErrors() {
        for (int i = 1; i < 10; i++) {
            for (int j = 1; j < 10; j++) {
                if (!cells[i - 1][j - 1].getText().equals(" ")) {
                    if (!(title[i][j] == Integer.parseInt(cells[i - 1][j - 1].getText()))) {
                        cells[i - 1][j - 1].setForeground(Color.red);
                    }
                }
            }
        }
    }

    /**
     * 等級
     */
    enum Rank {

        EASY, MIDDLE, HARD
    }

    /**
     * create a new game() {
     */
    void createEasyGame() {
        setRank(Rank.EASY);
        setSudokuTitle();
    }

    /**
     * create a middle game
     */
    void createMiddleGame() {
        setRank(Rank.MIDDLE);
        setSudokuTitle();


    }

    /**
     * create a new Hard game
     */
    void createHardGame() {
        setRank(Rank.HARD);
        setSudokuTitle();
    }

    /**
     * 作答完畢後與答案進行比對
     */
    boolean finalCheck() {
        for (int i = 1; i < 10; i++) {
            for (int j = 1; j < 10; j++) {
                if (!(title[i][j] == Integer.parseInt(cells[i - 1][j - 1].getText()))) {
                    return false;
                }
            }
        }
        goodGame();
        return true;
    }

    /**
     * 完成遊戲
     */
    void goodGame() {
        time.stop();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                cells[i][j].setText("");
                cells[i][j].setEnabled(false);
            }
        }
        btnShowErrors.setEnabled(false);
        btnTimeStop.setEnabled(false);
        javax.swing.JOptionPane.showMessageDialog(null, "你完成了數獨,共花了" + sec / 60 + "分" + sec % 60 + "秒!");
    }

    /**
     * 計算已填格數
     */
    void setLatticeCounter() {
        this.latticeCounter = 0;
        for (int i = 0; i < 9; i++) {
            for (int ii = 0; ii < 9; ii++) {
                if (cells[i][ii].getText().equals(" ")) {
                    this.latticeCounter++;
                }
            }
        }
        lattice[1].setText(Integer.toString(this.latticeCounter));
        if (this.latticeCounter == 0) {//如果沒有空格了，則進行完全比對
            finalCheck();
        }
    }

    /**
     * 設定困難度
     */
    void setRank(Rank rank) {
        this.rank = rank;
    }

    /**
     * 更新時間
     */
    void setTime() {
        timeL.setText(sec / 60 + "分" + sec % 60 + "秒");
        sec++;
    }

    /**
     * 出題目
     */
    void setSudokuTitle() {
        sec = 0;//記時器歸零
        time.start();//開始記時
        btnTimeStop.setEnabled(true);
        btnShowErrors.setEnabled(true);
        System.out.println("create a new game:" + rank);
        for (int i = 0; i < title.length; i++) {            //至378行為普通產生題目
            for (int ii = 0; ii < title[i].length; ii++) {
                title[i][ii] = 0;
            }
        }
        int[] tmp = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] tmp2;//D
        for (int i = 1; i <= 3; i++) {
            for (int t = 0; t < 9; t++) {
                int x = 0, tmpValue = 0;
                tmpValue = tmp[t];
                tmp[t] = tmp[x = ((int) (Math.random() * 9))];
                tmp[x] = tmpValue;
            }
            for (int z = 0, x = i * 3 - 2; x <= i * 3; x++) {
                for (int y = i * 3 - 2; y <= i * 3; y++) {
                    title[x][y] = tmp[z++];
                }
            }
        }
        ArrayList<Integer> a = new ArrayList<Integer>(); //至416行，目的為題目的多樣性
        ArrayList<Integer> b = new ArrayList<Integer>(); 
        for (int i = 4; i < 10; i++) {
            tmp = new int[10];
            tmp2 = new int[10];
            for (int ii = 1; ii < i; ii++) {
                if (title[1][ii] != 0) {
                    tmp[title[1][ii]]++;
                }
                if (title[ii][1] != 0) { 
                    tmp2[title[ii][1]]++;
                }
            }
            for (int iii = 4; iii < 10; iii++) {
                if (title[iii][i] != 0) {
                    tmp[title[iii][i]]++;
                }
                if (title[i][iii] != 0) {
                    tmp2[title[i][iii]]++;
                }
            }
            for (int ii = 1; ii < tmp.length; ii++) {
                if (tmp[ii] == 0) {
                    a.add(ii);
                }
                if (tmp2[ii] == 0) {
                    b.add(ii);
                }
            }
            if (a.isEmpty() || b.isEmpty()) {  //避免上述目的為題目產生多樣性之程式碼失敗，造成出題錯誤，從新產生ㄧ個新的題目替代
                setSudokuTitle();
                return;
            }
            title[1][i] = a.get((int) (Math.random() * a.size()));
            title[i][1] = b.get((int) (Math.random() * b.size()));
            a.clear();
            b.clear();
        }
        if (!ansTitle(1, 1)) {  //避免產生題目失敗
            System.out.println("F");
            setSudokuTitle();
            return;
        }
        for (int i = 0; i < 9; i++) {  //把產生的題目放上按鈕Text屬性
            for (int j = 0; j < 9; j++) {
                if (title[i + 1][j + 1] == 0) {
                    cells[i][j].setText(" ");
                } else {
                    cells[i][j].setText(Integer.toString(title[i + 1][j + 1]));
                }
                cells[i][j].setEnabled(false);
                stopFlag[i][j] = false;
            }
        }
        int x = 0;  //依等級設定空格數
        if (rank == Rank.MIDDLE) {
            x = 30;
        } else if (rank == Rank.EASY) {
            x = 20;
        } else if (rank == Rank.HARD) {
            x = 40;
        }
        for (int i, j, c = 1; c <= x; c++) {  //挖空格
            if (cells[i = (int) (Math.random() * 9)][j = (int) (Math.random() * 9)].getText().equals(" ")) {
                c--;
            } else {
                cells[i][j].setText(" ");
                cells[i][j].setEnabled(true);
                stopFlag[i][j] = true;
            }
        }
        setLatticeCounter();
    }

    /**
     * 出題用，利用遞迴產生ㄧ完整數獨
     */
    boolean ansTitle(int row, int col) {
        for (; row < 10; row++) {
            for (col = 1; col < 10; col++) {
                if (row == 9 && col == 9 && title[row][col] != 0) {
                    p(title);
                    return true;
                } else if (title[row][col] == 0) {
                    for (int number = 1; number < 10; number++) {
                        if (check(title, row, col, number)) {
                            title[row][col] = number;
                            if (ansTitle(row, col)) {
                                return true;
                            }
                        }
                    }
                    title[row][col] = 0;
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 解數獨時檢查規則用，ROW、COL、小九宮格內1~9隻數字不得重複
     */
    boolean check(int[][] s, int i, int j, int x) {
        for (int i2 = 0, j2 = 0; j2 < 10; j2++, i2++) {
            if (s[i][j2] == x) {
                return false;
            }
            if (s[i2][j] == x) {
                return false;
            }
        }
        for (int i1 = 0; i1 < 3; i1++) {
            for (int j1 = 0; j1 < 3; j1++) {
                if (s[i - (i - 1) % 3 + i1][j - (j - 1) % 3 + j1] == x) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 印出解答，DEBUG時用
     */
    void p(int[][] s) {
        for (int i = 1; i < 10; i++) {
            for (int j = 1; j < 10; j++) {
                System.out.print(" " + s[i][j]);
            }
            System.out.println("");
        }
    }
}
