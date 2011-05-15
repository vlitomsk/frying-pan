package tetris;

import java.util.*;

class Figurek {
    private int[][] f;
    public int x, y;
    private int cx, cy;
    public Figurek(int[][] reference, int cx, int cy) {
        f = reference;
        x = 4;
        y = 0;
        this.cx = cx;
        this.cy = cy;
    }

    public int[][] getF() {
        return f;
    }

    public int getW() {
        return f.length;
    }

    public int getH() {
        return f[0].length;
    }   

    public void rotate_cw(int count) {
        for (int fish = 0; fish < count; ++fish) {
            int[][] tmp = new int[f[0].length][f.length];
            for (int i = 0; i < tmp.length; i++) {
                int x = tmp[i].length - 1;
                for (int j = 0; j < tmp[i].length; j++) {
                    tmp[i][j] = f[x--][i];
                }
            }
            f = tmp;
        }
    }
}

// Праведный классъ Костылизма Русскаго
class FigureGen {
    private int[][] arr;
    private int cx, cy;

    public FigureGen(int[][] arr, int cx, int cy) {
        this.arr = arr;
        this.cx = cx;
        this.cy = cy;
    }

    public Figurek gen() {
        return new Figurek(arr, cx, cy);
    }
}

public class Pole {
    private final int fibox[][] = {{1, 1},
                                   {1, 1}};

    private final int fil1[][] = {{1, 1},
                                  {1, 0},
                                  {1, 0}};

    private final int fil2[][] = {{1, 1},
                                  {0, 1},
                                  {0, 1}};

    private final int fis1[][] = {{0, 1, 1},
                                  {1, 1, 0}};

    private final int fis2[][] = {{1, 1, 0},
                                  {0, 1, 1}};

    private final int fif[][] = {{0, 1, 0},
                                 {1, 1, 1}};

    private final int filine[][] = {{1}, {1}, {1}, {1}};

    private final int Width = 10;
    private final int Height = 20;
    private int stakan[][] = new int[Width][Height];
    private HashMap<Integer, FigureGen> figures;

    private Figurek cur = null, next = null;
    private boolean genflag = false;

    private final int Free = 0;
    private final int Heap = 1;
    private final int Current = 2;

    private int max_height;

    public Pole() {
        figures = new HashMap<Integer, FigureGen>();
        figures.put(new Integer(0), new FigureGen(fibox, 0, 0));
        figures.put(new Integer(1), new FigureGen(fil1, 1, 0));
        figures.put(new Integer(2), new FigureGen(fil2, 1, 1));
        figures.put(new Integer(3), new FigureGen(fis1, 1, 1));
        figures.put(new Integer(4), new FigureGen(fis2, 1, 1));
        figures.put(new Integer(5), new FigureGen(fif, 1, 1));
        figures.put(new Integer(6), new FigureGen(filine, 1, 0));
        genflag = true;
        max_height = Height;
    }

    private void gen_couple() {
        Random r = new Random();
        if (cur == null)
            cur = figures.get(new Integer(r.nextInt(figures.size()))).gen();
        else
            cur = next;
        next = figures.get(new Integer(r.nextInt(figures.size()))).gen();
    }

    // убирает текущую фигурку из стакана
    private void clean_current() {
        for (int i = 0; i < Width; ++i)
            for (int j = 0; j < Height; ++j)
                if (stakan[i][j] == Current)
                    stakan[i][j] = Free;
    }

    // перерисовать фигурку исходя из ее координат
    private void reproect_current() {
        clean_current();
        for (int i = 0; i < cur.getW(); ++i) 
            for (int j = 0; j < cur.getH(); ++j) 
                stakan[cur.x + i][cur.y + j] = (cur.getF()[i][j] == 1) ? Current : Free;
    }

    private void upd_maxheight() {
        for (int i = 0; i < Width; ++i) {
            for (int j = 0; j < Height; ++j) 
                if ((j < max_height) && (stakan[i][j] == Heap))
                    max_height = j;
        }
    }

    // превращает текущую фигурку в кучу
    private void to_heap() {
        for (int i = 0; i < Width; ++i)
            for (int j = 0; j < Height; ++j)
                if (stakan[i][j] == Current)
                    stakan[i][j] = Heap;
    }

    // проверяет полноту линии n от низа стакана. n>=0
    private boolean check_line(int n) {
        boolean full = true;
        for (int i = 0; i < Width; ++i) 
            if (stakan[i][Height - n - 1] == Free) {
                full = false;
                break;
            }

        return full;
    }

    // схлопнуть линию n от низа стакана. n>=0
    private void squash_line(int n) {
        for (int i = 0; i < Width; ++i) 
            for (int j = Height - n - 1; j > 0; --j)  // МОЖЕТ БЫТЬ J>=0 ИЛИ J>1 !!!
                stakan[i][j] = stakan[i][j + 1];       
    }

    // зачистить полные линии
    private void cleanup_lines() {
        for (int i = 0; i < Height; ) 
            if (check_line(i)) 
               squash_line(i);
            else
                ++i;
    }

    // проверить, настал ли конец текущей фигурке. если да, то в кучу ее.
    private boolean current_endofway() {
        for (int i = 0; i < Width; ++i)
                for (int j = 0; j < Height; ++j)
                    if (stakan[i][j] == Current) {
                        boolean endofway = false;
                        if (j == Height - 1)
                            endofway = true;
                        else if (stakan[i][j + 1] == Heap)
                            endofway = true;

                        if (endofway) {
                            genflag = true;
                            to_heap();
                            return true;
                        }
                    }

        return false;
    }

    // сдвинуть фигурку на 1 вниз. не проверяет, пришел ли ей конец.
    private void move_fig() {
        for (int i = Height - 1; i >= 0; --i)
                for (int j = Width - 1; j > 0; --j)
                    if (stakan[j][i] == Current) {
                        stakan[j][i + 1] = Current;
                        stakan[j][i] = Free;
                    }
    }

    public boolean step() {
        // зачистка полных линий
        cleanup_lines();

        if (genflag) {
            gen_couple();
            genflag = false;
            reproect_current();
        } else {
            // пришел ли конец текущей фигурке
            if (current_endofway())
                return true; // да, пришел
            else {
                move_fig(); // если нет, то сдвинем ее на один шаг
                cur.y++;
            }
        }

        return false;
    }

    // пришел ли конец игре
    public boolean end_of_game() {
        upd_maxheight();
        if (max_height <= 0)
            return true;

        return false;
    }

    public static final int MoveRight = 0;
    public static final int MoveLeft = 1;
    public static final int MoveDown = 2;
    public static final int MoveThr = 3;
    public static final int MoveCW = 4;
    public static final int MoveCCW = 5;

    public void move(int movement) {
        if (!genflag) {
            switch (movement) {
                case MoveRight: if (cur.x + cur.getW() + 1 <= Width)
                                    cur.x++;
                                break;
                case MoveLeft: if (cur.x > 0)
                                   cur.x--;
                               break;
                case MoveDown: if (cur.y + cur.getH() + 1 <= Height)
                                   cur.y++;
                               break;
                case MoveThr:  while (!step()) {};
                               break;
                case MoveCW: cur.rotate_cw(1);
                             break;
                case MoveCCW: cur.rotate_cw(3);
                              break;
                default: return;
            }

            reproect_current();
        }
    }

    // получить стакан в виде массива
    public int[][] ret_stakan() {
        return stakan;
    }
}
