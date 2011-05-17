package tetris;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

class TetrisView extends JApplet {

    private int[][] st = null;
    //private Stack<Point> diff;
    private int[][] diff;
    private int dlen = 0;

    public TetrisView(int[][] stakan) {
        //diff = new Stack<Point>();
        diff = new int[14*24][2];
        set_st(stakan);
    }

    public void set_st(int[][] stakan) {
        if (st != null) {
            dlen = 0;
            for (int i = 0; i < st.length; ++i) {
                for (int j = 0; j < st[0].length; ++j) {
                    if ((st[i][j] != stakan[i][j])) {
                        diff[dlen][0] = i;
                        diff[dlen++][1] = j;
                    }
                }
            }
        } else {
            dlen = 0;
            for (int i = 0; i < stakan.length; ++i) {
                for (int j = 0; j < stakan[0].length; ++j) {
                    diff[dlen][0] = i;
                    diff[dlen++][1] = j;
                }
            }
        }

        st = new int[stakan.length][stakan[0].length];
        for (int i = 0; i < st.length; ++i) {
            for (int j = 0; j < st[0].length; ++j) {
                st[i][j] = stakan[i][j];
            }
        }
    }

    private int score = -1;
    private boolean score_diff = true;
    void set_score(int s) {
        if (s != score)
            score_diff = true;
        score = s;        
    }

    private boolean next_diff = true;
    private int level = 1;
    void set_lev(int l) {
        if (level != l)
            next_diff = true;
        level = l;
    }

    private int ft = 0;

    public void paint(Graphics g) {
        while (dlen != 0) {
            System.out.println("len = " + diff.length);
            int x = diff[dlen - 1][0];
            int y = diff[dlen - 1][1];
            dlen--;
            if (st[x][y] != 0) {
                if (st[x][y] == 1)
                    g.setColor(new Color(0x66, 0xc4, 0x66));
                if (st[x][y] == 2)
                    g.setColor(new Color(0x99, 0x21, 0xde));
            } else 
                g.setColor(Color.white);

            g.fillRect(x * 20 + 1, (y - 4) * 20 + 1, 19, 19);
            g.setColor(Color.black);
        }

        if (score_diff) {
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            g.drawString("Score: ", 290, 30);
            g.setColor(Color.yellow);
            g.fillRect(290, 35, this.getHeight() - 290, 20);
            g.setColor(Color.black);
            g.drawString(String.valueOf(score), 290, 50);
            score_diff = false;
        }

        if (ft < 2) {
            for (int i = 0; i <= 14; ++i)
                g.drawLine(i * 20, 0, i * 20, 20 * 20);
            for (int i = 0; i <= 20; ++i)
                g.drawLine(0, i * 20, 14 * 20, i * 20);
            ft++;
        }

    }
}

public class View {

    static void print_stakan(int[][] stakan) {
        for (int i = 4; i < stakan[0].length; ++i) {
            for (int j = 0; j < stakan.length; ++j) {
                System.out.print(stakan[j][i]);
            }

            System.out.println();
        }

        System.out.println("- - - - - -");
    }

    public static void main(String[] args) throws InterruptedException {
        final Pole p = new Pole();
        p.step();

        JFrame frm;
        frm = new JFrame("Frying pan");
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setSize(20 * 14 + 150, 20 * 20 + 30);

        final TetrisView tv = new TetrisView(p.ret_stakan());

        frm.addKeyListener(new KeyListener() {

            public void keyTyped(KeyEvent ke) {
            }

            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() == ke.VK_LEFT) 
                    p.move(Pole.MoveLeft);
                if (ke.getKeyCode() == ke.VK_RIGHT) 
                    p.move(Pole.MoveRight);
                if (ke.getKeyCode() == ke.VK_DOWN) 
                    p.move(Pole.MoveDown);
                if (ke.getKeyCode() == ke.VK_UP) 
                    p.move(Pole.MoveCW);
                if (ke.getKeyCode() == ke.VK_SPACE) 
                    p.move(Pole.MoveThr);

                p.end_of_game();
                tv.set_st(p.ret_stakan());
                tv.repaint();
            }

            public void keyReleased(KeyEvent ke) {
            }
        });

        frm.add(tv);

        frm.setVisible(true);

        int score = 0;
        int lev = 1;
        while (!p.end_of_game()) {
            p.step();
            int ls = p.get_lastscore();

            switch (ls) {
                case 1: score += 100;
                    break;
                case 2: score += 300;
                    break;
                case 3: score += 700;
                    break;
                case 4: score += 1500;
                    break;
            }

            lev = score / 500 + 1;

            tv.set_st(p.ret_stakan());
            tv.set_score(score);
            tv.repaint();
            // print_stakan(p.ret_stakan());

            int stime = 500 - 30 * (lev - 1);
            if (stime < 0)
                break;

            Thread.sleep(500 - 30*(lev - 1));
        }

        if (p.end_of_game())
            JOptionPane.showMessageDialog(frm, "Thanks for the fish, but game is over.\nYour score is: " + String.valueOf(score) + " point(s)!");
        else
            JOptionPane.showMessageDialog(frm, "You won!\nYour score is: " + String.valueOf(score) + " point(s)!");
    }
}
