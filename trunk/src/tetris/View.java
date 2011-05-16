package tetris;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import javax.swing.*;

class Grid extends JComponent {

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.yellow);
        g.fillRect(0, 0, 100, 100);
    }
}

class TetrisView extends JApplet {

    private int[][] st = null;
    private Stack<Point> diff;

    public TetrisView(int[][] stakan) {
        diff = new Stack<Point>();
        set_st(stakan);
    }

    public void set_st(int[][] stakan) {
        if (st != null) {
            for (int i = 0; i < st.length; ++i) {
                for (int j = 0; j < st[0].length; ++j) {
                    if ((st[i][j] != stakan[i][j])) {
                        diff.push(new Point(i, j));
                    }
                }
            }
        } else {
            for (int i = 0; i < stakan.length; ++i) {
                for (int j = 0; j < stakan[0].length; ++j) {
                    diff.push(new Point(i, j));
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

    public void paint(Graphics g) {
        while (!diff.empty()) {
            Point p = diff.pop();
            if (st[p.x][p.y] != 0) {
                if (st[p.x][p.y] == 1)
                    g.setColor(new Color(0x66, 0xc4, 0x66));
                if (st[p.x][p.y] == 2)
                    g.setColor(new Color(0x99, 0x21, 0xde));
                g.fillRect(p.x * 20, (p.y - 4) * 20, 20, 20);
                g.setColor(Color.black);
            } else {
                g.setColor(Color.white);
                g.fillRect(p.x * 20 + 1, (p.y - 4) * 20 + 1, 19, 19);
                g.setColor(Color.black);
            }
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

        for (int i = 0; i <= 14; ++i)
            g.drawLine(i * 20, 0, i * 20, 20 * 20);
        for (int i = 0; i <= 20; ++i)
            g.drawLine(0, i * 20, 14 * 20, i * 20);

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
                if (ke.getKeyCode() == ke.VK_LEFT) {
                    p.move(Pole.MoveLeft);
                }
                if (ke.getKeyCode() == ke.VK_RIGHT) {
                    p.move(Pole.MoveRight);
                }
                if (ke.getKeyCode() == ke.VK_DOWN) {
                    p.move(Pole.MoveDown);
                }
                if (ke.getKeyCode() == ke.VK_UP) {
                    p.move(Pole.MoveCW);
                }
                if (ke.getKeyCode() == ke.VK_SPACE) {
                    p.move(Pole.MoveThr);
                }

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
            tv.set_st(p.ret_stakan());
            tv.set_score(score);
            tv.repaint();
            // print_stakan(p.ret_stakan());
            Thread.sleep(500);
        }

        JOptionPane.showMessageDialog(frm, "Thanks for the fish, but game is over.\nYour score is: " + String.valueOf(score) + " point(s)!");
    }
}
