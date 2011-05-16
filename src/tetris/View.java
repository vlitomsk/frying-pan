package tetris;

import java.awt.*;
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
            System.out.println("oilala");
            for (int i = 0; i < st.length; ++i)
                for (int j = 0; j < st[0].length; ++j)
                    if ((st[i][j] != stakan[i][j])) {
                        diff.push(new Point(i, j));
                        System.out.println("i = " + i);
                        System.out.println("j = " + j);
                    }
        } else
            for (int i = 0; i < stakan.length; ++i)
                for (int j = 0; j < stakan[0].length; ++j)
                    diff.push(new Point(i, j));

        st = new int[stakan.length][stakan[0].length];
        for (int i = 0; i < st.length; ++i)
            for (int j = 0; j < st[0].length; ++j)
                st[i][j] = stakan[i][j];
    }

    public void paint(Graphics g) {
        /*g.setColor(Color.white);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(Color.black);*/
        while (!diff.empty()) {
            Point p = diff.pop();
            if (st[p.x][p.y] != 0)
                g.fillRect(p.x * 20, (p.y - 4) * 20, 20, 20);
            else {
                g.setColor(Color.white);
                g.fillRect(p.x * 20 + 1, (p.y - 4) * 20 + 1, 19, 19);
                g.setColor(Color.black);
            }
        }

   /*     for (int i = 0; i < 14; ++i)
            for (int j = 4; j < 24; ++j)
                if (st[i][j] != 0)
                    g.fillRect(i * 20, (j - 4) * 20, 20, 20);*/
        for (int i = 0; i <= 14; ++i) 
            g.drawLine(i * 20, 0, i * 20, 20*20);
        for (int i = 0; i <= 20; ++i)
            g.drawLine(0, i * 20, 14*20, i*20);
        
    }
}

public class View {
    static void print_stakan(int[][] stakan) {
        for (int i = 4; i < stakan[0].length; ++i) {
            for (int j = 0; j < stakan.length; ++j)
                System.out.print(stakan[j][i]);

            System.out.println();
        }

        System.out.println("- - - - - -");
    }

    public static void main(String[] args) throws InterruptedException {
        Pole p = new Pole();
        p.step();

        JFrame frm;
        frm = new JFrame("Frying pan");
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setSize(20 * 14 + 150, 20 * 20 + 30);

        TetrisView tv = new TetrisView(p.ret_stakan());
        frm.add(tv);     

        frm.setVisible(true);

        while (!p.end_of_game()) {
            p.move(Pole.MoveCW);
            p.end_of_game();
            p.step();
            tv.set_st(p.ret_stakan());
            tv.repaint();
           // print_stakan(p.ret_stakan());
            Thread.sleep(500);
        }

        System.exit(0);
    }
}
