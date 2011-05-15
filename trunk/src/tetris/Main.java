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
        p.move(Pole.MoveCCW);
        while (!p.end_of_game()) {
            /*if (sw)
                p.move(Pole.MoveLeft);
            else
                p.move(Pole.MoveRight);
            sw = !sw;*/
            p.move(Pole.MoveDown);
            //p.move(Pole.MoveLeft);
            p.step();
            print_stakan(p.ret_stakan());            
            Thread.sleep(500);
        }
    }
}
