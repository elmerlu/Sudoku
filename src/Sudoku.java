import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;


public class Sudoku extends JApplet {
	/**
     * �w��Ů椧�p�ƾ�
     */
    int latticeCounter = 0;
    /**
     * �ھ����שҲ��ͥX�Ӫ��D�ظ�
     */
    int[][] title = new int[10][10];
    /**
     * �ƿW��l�ѽL�����s
     */
    JButton[][] cells;
    /**
     * �ɶ�(��)
     */
    long sec;
    /**
     * �ɶ���r���
     */
    JLabel timeL;
    /**
     * �ɶ���ť��
     */
    Timer time;
    /**
     * �ɶ��Ȱ�
     */
    boolean stop = false;
    /**
     * �ɶ��Ȱ��ɴѽL�W���O��
     */
    boolean[][] stopFlag = new boolean[9][9];
    /**
     * �Ȱ��ɥδ���TEXT
     */
    String[][] tmpCells = new String[9][9];
    /**
     * �U�ث��s
     */
    JButton btnTimeStop, btnNewHardGame, btnNewMiddleGame, btnNewEasyGame, btnShowErrors;
    /**
     * �p�ƾ�����r���
     */
    JLabel[] lattice = {new JLabel("�Ѿl�Ů��:"), new JLabel(" ")};
    /**
     * The current number which will be set to cell
     */
    String currentNumber = null; // current choose value [1..9]
    /**
     * ����
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
     * default constructor to initialize Sudoku's GUI�A �W�[���r�e���B�p�ɾ��B�Ů�ƭp��
     */
    public Sudoku() {
        // create listeners
        ButtonsListner btnsListner = new ButtonsListner();
        RadioButtonsListner radoiButtonsListner = new RadioButtonsListner();
        // create 9x9 buttons
        JPanel panelCells = new JPanel();
        getContentPane().add(panelCells, BorderLayout.CENTER);
        panelCells.setLayout(new GridLayout(3, 3, 5, 5));  //GridLayout(int rows, int cols, int hgap(�������Z), int vgap(�������Z))
        JPanel[][] JP = new JPanel[3][3];  //�ت������r�e��
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                panelCells.add(JP[i][j] = new JPanel());
                JP[i][j].setLayout(new GridLayout(3, 3, 2, 2));
            }
        }
        panelCells.setBackground(new Color(0, 150, 255));  //�ت������r�e��
        cells = new JButton[9][9];
        for (int i = 0; i < cells[0].length; i++) {
            for (int j = 0; j < cells[1].length; j++) {
                JP[i / 3][j / 3].add(cells[i][j] = new JButton());
                cells[i][j].addActionListener(btnsListner);
                cells[i][j].setEnabled(false);
                cells[i][j].setFont(new Font("Dialog",0, 24));//�N���s�r���j
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
        numberButtons[0].setSelected(true);//�N�w���֨���ֳ]���Ʀr1
        // create new game button (note that you may extend to hard, easy ..buttons)
        JPanel panelFunctions = new JPanel();
        getContentPane().add(panelFunctions, BorderLayout.SOUTH);
        panelFunctions.setLayout(new GridLayout(2, 1, 9, 9));

        JPanel[] tJP = new JPanel[2];
        panelFunctions.add(tJP[0] = new JPanel());
        panelFunctions.add(tJP[1] = new JPanel());
        timeL = new JLabel("0��0��");
        tJP[0].add(timeL);
        btnTimeStop = new JButton("�Ȱ��C��"); // �ɶ��Ȱ�
        btnTimeStop.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {   //�Ȱ��C���P�O�ԹC�����s�ʧ@
                if (stop) {
                    stop = false;
                    btnTimeStop.setText("�Ȱ��C��");
                    time.start();//�q�_�O�ɾ�
                    btnNewEasyGame.setEnabled(true);
                    btnNewHardGame.setEnabled(true);
                    btnNewMiddleGame.setEnabled(true);
                    btnShowErrors.setEnabled(true);
                } else {
                    stop = true;
                    btnTimeStop.setText("�~��C��");
                    time.stop();//�Ȱ��O�ɾ�
                    btnNewEasyGame.setEnabled(false);
                    btnNewHardGame.setEnabled(false);
                    btnNewMiddleGame.setEnabled(false);
                    btnShowErrors.setEnabled(false);
                }
                for (int i = 0; i < 9; i++) {  //�g��������Text�A��Text��i�w���g�n���Ű}�C�ì~��
                    for (int j = 0; j < 9; j++) {
                        if (stop) {
                            tmpCells[i][j] = cells[i][j].getText();
                            cells[i][j].setText(" ");
                        } else {
                            cells[i][j].setText(tmpCells[i][j]);//��Text�q�s���^��
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
        btnNewEasyGame = new JButton("\u7C21\u55AE"); // ²��
        tJP[1].add(btnNewEasyGame);
        btnNewEasyGame.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                createEasyGame();
            }
        });
        btnNewMiddleGame = new JButton("\u4E2D\u7B49");  // ����
        btnNewMiddleGame.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                createMiddleGame();
            }
        });
        tJP[1].add(btnNewMiddleGame);
        btnNewHardGame = new JButton("\u56F0\u96E3"); // �x��
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
        time = new Timer(1000, timeActionListener); //�ɶ��Ƶ{�A�Ψӹ�@�O�ɾ�

    }

    /**
     * check and show error�A������ά������
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
     * ����
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
     * �@��������P���׶i����
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
     * �����C��
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
        javax.swing.JOptionPane.showMessageDialog(null, "�A�����F�ƿW,�@��F" + sec / 60 + "��" + sec % 60 + "��!");
    }

    /**
     * �p��w����
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
        if (this.latticeCounter == 0) {//�p�G�S���Ů�F�A�h�i�槹�����
            finalCheck();
        }
    }

    /**
     * �]�w�x����
     */
    void setRank(Rank rank) {
        this.rank = rank;
    }

    /**
     * ��s�ɶ�
     */
    void setTime() {
        timeL.setText(sec / 60 + "��" + sec % 60 + "��");
        sec++;
    }

    /**
     * �X�D��
     */
    void setSudokuTitle() {
        sec = 0;//�O�ɾ��k�s
        time.start();//�}�l�O��
        btnTimeStop.setEnabled(true);
        btnShowErrors.setEnabled(true);
        System.out.println("create a new game:" + rank);
        for (int i = 0; i < title.length; i++) {            //��378�欰���q�����D��
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
        ArrayList<Integer> a = new ArrayList<Integer>(); //��416��A�ت����D�ت��h�˩�
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
            if (a.isEmpty() || b.isEmpty()) {  //�קK�W�z�ت����D�ز��ͦh�˩ʤ��{���X���ѡA�y���X�D���~�A�q�s���ͣ��ӷs���D�ش��N
                setSudokuTitle();
                return;
            }
            title[1][i] = a.get((int) (Math.random() * a.size()));
            title[i][1] = b.get((int) (Math.random() * b.size()));
            a.clear();
            b.clear();
        }
        if (!ansTitle(1, 1)) {  //�קK�����D�إ���
            System.out.println("F");
            setSudokuTitle();
            return;
        }
        for (int i = 0; i < 9; i++) {  //�ⲣ�ͪ��D�ة�W���sText�ݩ�
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
        int x = 0;  //�̵��ų]�w�Ů��
        if (rank == Rank.MIDDLE) {
            x = 30;
        } else if (rank == Rank.EASY) {
            x = 20;
        } else if (rank == Rank.HARD) {
            x = 40;
        }
        for (int i, j, c = 1; c <= x; c++) {  //���Ů�
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
     * �X�D�ΡA�Q�λ��j���ͣ�����ƿW
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
     * �ѼƿW���ˬd�W�h�ΡAROW�BCOL�B�p�E�c�椺1~9���Ʀr���o����
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
     * �L�X�ѵ��ADEBUG�ɥ�
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
