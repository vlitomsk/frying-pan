package tetris;

import java.util.Arrays;

public class Main {
    static void print_stakan(int[][] stakan) {
        for (int i = 0; i < stakan[0].length; ++i) {
            for (int j = 0; j < stakan.length; ++j)
                System.out.print(stakan[j][i]);

            System.out.println();
        }

        System.out.println("- - - - - -");
    }

    public static void main(String[] args) throws InterruptedException {
        Pole p = new Pole();
        boolean sw = false;
        p.step();
        while (!p.end_of_game()) {
            if (sw)
                p.move_left();
            else
                p.move_right();
            sw = !sw;
            p.step();
            print_stakan(p.ret_stakan());            
            Thread.sleep(1000);
        }
    }
}
