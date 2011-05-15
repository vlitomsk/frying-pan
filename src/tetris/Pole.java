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

    public void rotate_cw(int count, int[][] stakan) {
        if (x + getH() > stakan.length) {System.err.println("oilala");return;}
        if (y + getW() > stakan[0].length) {return;}
        for (int fish = 0; fish < count; ++fish) {
            int[][] tmp = new int[f[0].length][f.length];
            for (int i = 0; i < tmp.length; i++) {
                int a = tmp[i].length - 1;
                for (int j = 0; j < tmp[i].length; j++) {
                    tmp[i][j] = f[a--][i];
                    if (stakan[x + i][y + j] == Pole.Heap) {
                        return;
                    }
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
    private final int Height = 24;
    private final int UpHeight = 4;
    private int stakan[][] = new int[Width][Height];
    private HashMap<Integer, FigureGen> figures;

    private Figurek cur = null, next = null;
    private boolean genflag = false;

    public static final int Free = 0;
    public static final int Heap = 1;
    public static final int Current = 2;

    private int max_height;
    private int stub;

    private boolean game_stop = false;

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
        stub = 0;
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
                if (cur.getF()[i][j] == 1)
                    stakan[cur.x + i][cur.y + j] = Current;
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
                            //genflag = true;
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
        if (game_stop) return true;
        // зачистка полных линий
        cleanup_lines();

        if (genflag) {
            if (stub == 0) {
                gen_couple();
                genflag = false;
                reproect_current();
            }
            stub = 1;
        } else {
            // пришел ли конец текущей фигурке
            if (current_endofway()) {
                genflag = true;
                gen_couple();
                genflag = false;
                reproect_current();
                return true; // да, пришел
            } else {
     //           move_fig(); // если нет, то сдвинем ее на один шаг
                cur.y++;
                reproect_current();
            }
        }

        return false;
    }

    // пришел ли конец игре
    public boolean end_of_game() {
        upd_maxheight();
        if (max_height <= UpHeight) {
            game_stop = true;
            return true;
        }
        return false;
    }

    private int sign(int a) {
        return a == 0 ? 0 : a / Math.abs(a);
    }

    private boolean can_move(int dx, int dy) {       
        if (dx == 0) { // можно ли двигать по вертикали?
            dy = sign(dy);
            if (dy == -1)
                return false;
            if ((dy == 1) && !(cur.y + cur.getH() + 1 <= Height)) 
                return false;
        } else if (dy == 0) { // можно ли двигать по горизонтали?
            dx = sign(dx);
            if ((dx == 1) && !(cur.x + cur.getW() + 1 <= Width))
                return false;
            if ((dx == -1) && !(cur.x > 0))
                return false;           
        } else
            return false;
        
        for (int i = ((dx == 1) || (dx == 0)) ? 0 : 1; i < Width - ((dx == -1) ? 1 : 0); ++i)
                for (int j = 0; j < Height - 1; ++j)
                    if (stakan[i][j] == Current)
                        if (stakan[i + dx][j + dy] == Heap) 
                            return false;

        return true;
    }

    public static final int MoveRight = 0;
    public static final int MoveLeft = 1;
    public static final int MoveDown = 2;
    public static final int MoveThr = 3;
    public static final int MoveCW = 4;
    public static final int MoveCCW = 5;

    public void move(int movement) {
        if ((!genflag) && (!game_stop)) {
            switch (movement) {
                case MoveRight: if (can_move(1, 0))
                                    cur.x++;
                                break;
                case MoveLeft: if (can_move(-1, 0))
                                   cur.x--;
                               break;
                case MoveDown: if (can_move(0, 1))
                                   cur.y++;
                               break;
                case MoveThr:  while (!step()) {};
                               break;
                case MoveCW: cur.rotate_cw(1, stakan);
                             break;
                case MoveCCW: cur.rotate_cw(3, stakan);
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
