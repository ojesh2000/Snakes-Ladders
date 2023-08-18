import java.util.*;
import java.io.*;

class Block{
    private int num;
    private Mover toMove;
    private Player occupiedBy;

    public Block(int num , Mover toMove , Player occupiedBy){
        this.num = num;
        this.toMove = toMove;
        this.occupiedBy = occupiedBy;
    }

    public int getNum(){
        return num;
    }

    public void setNum(int num) throws NullPointerException{
        this.num = num;
    }

    public Player getPlayerOccupied(){
        return occupiedBy;
    }

    public void setPlayerOccupied(Player occupiedBy){
        this.occupiedBy = occupiedBy;
    }

    public Mover getToMove(){
        return toMove;
    }

    public void setToMove(Mover toMove){
        this.toMove = toMove;
    }
}

interface Mover{
    public void moveTo(Player curPlayer);

    default void move(Block start , Block end , Player curPlayer){
        if(start != null){
            start.setPlayerOccupied(null);
        }
        if(end.getToMove() != null){
            Mover mv = end.getToMove();
            mv.moveTo(curPlayer);
        }
        else{
            beats(curPlayer , end);
            curPlayer.setCurPos(end);
            end.setPlayerOccupied(curPlayer);
        }
    }

    default void beats(Player curPlayer , Block curBlock){
        Player beaten = curBlock.getPlayerOccupied();
        if(beaten == null){
            return ;
        }
        System.out.println("Player: " + curPlayer + " beats Player: " + beaten);
        beaten.setCurPos(null);
    }
}

class Ladder implements Mover{
    private Block start;
    private Block end;

    public Ladder(Block start , Block end){
        this.start = start;
        this.end = end;
    }

    protected Block getStart(){
        return start;
    }

    protected Block getEnd(){
        return end;
    }

    protected void setStart(Block start){
        this.start = start;
    }

    protected void setEnd(Block end){
        this.end = end;
    }

    @Override
    public void moveTo(Player curPlayer){
        System.out.print("Wow!! Player: " + curPlayer);
        System.out.println(" climbed a ladder.");
        move(start , end , curPlayer);
    }
    
}

class Snake implements Mover{
    private Block start;
    private Block end;

    public Snake(Block start , Block end){
        this.start = start;
        this.end = end;
    }

    protected Block getStart(){
        return start;
    }

    protected Block getEnd(){
        return end;
    }

    protected void setStart(Block start){
        this.start = start;
    }

    protected void setEnd(Block end){
        this.end = end;
    }

    @Override
    public void moveTo(Player curPlayer){
        System.out.println("OOPS!! A snake bit Player: " +  curPlayer);
        move(start , end , curPlayer);
    }

}

class DiceMover implements Mover{
    private Block start;
    private Block end;

    public DiceMover(Block start , Block end){
        this.start = start;
        this.end = end;
    }

    @Override
    public void moveTo(Player curPlayer){
        move(start , end , curPlayer);
    }
}

class Player{
    private int playerNo;
    private Block curPos;

    public Player(int playerNo , Block curPos){
        this.playerNo = playerNo;
        this.curPos = curPos;
    }

    public int getPlayerNo(){
        return playerNo;
    }

    public Block getCurPos(){
        return curPos;
    }

    public void setCurPos(Block curPos){
        this.curPos = curPos;
    }

    @Override
    public String toString(){
        return Integer.toString(playerNo);
    }
}

class Board{
    Block[] board = new Block[101];

    public Board(){
        for(int i = 0;i < 101;++i){
            board[i] = new Block(i , null , null);
        }

        //Inducing Ladders
        board[2].setToMove(new Ladder(board[2] , board[23]));
        board[8].setToMove(new Ladder(board[8] , board[12]));
        board[17].setToMove(new Ladder(board[17] , board[93]));
        board[29].setToMove(new Ladder(board[29] , board[54]));
        board[32].setToMove(new Ladder(board[32] , board[51]));
        board[39].setToMove(new Ladder(board[39] , board[80]));
        board[62].setToMove(new Ladder(board[62] , board[78]));
        board[70].setToMove(new Ladder(board[70] , board[89]));
        board[75].setToMove(new Ladder(board[75] , board[96]));

        //Inducing Snakes
        board[99].setToMove(new Snake(board[99] , board[5]));
        board[92].setToMove(new Snake(board[92] , board[76]));
        board[67].setToMove(new Snake(board[67] , board[50]));
        board[59].setToMove(new Snake(board[59] , board[37]));
        board[41].setToMove(new Snake(board[41] , board[20]));
        board[30].setToMove(new Snake(board[30] , board[7]));
    }

    public void rollDice(Player curPlayer)throws IOException , NullPointerException{
        System.out.println("Enter y to roll dice");
        BufferedReader bfr = new BufferedReader(new InputStreamReader(System.in));
        String s = bfr.readLine();
        String check = "y";
        if(!s.equals(check))return ;
        Random rand = new Random();
        int moveBy = rand.nextInt(6) + 1;

        System.out.println("You rolled a " + moveBy);

        if(curPlayer.getCurPos() == null){
            if(moveBy == 1 || moveBy == 6){
                DiceMover d = new DiceMover(null , board[1]);
                d.moveTo(curPlayer);
            }
            return ;
        }

        int curIndex = curPlayer.getCurPos().getNum();
        int nextIndex = curIndex + moveBy;

        if(nextIndex <= 100){
            DiceMover d = new DiceMover(board[curIndex] , board[nextIndex]);
            d.moveTo(curPlayer);
        }
    }

    public boolean hasWon(Player curPlayer){
        return (curPlayer.getCurPos() == board[100]);
    }
}

public class Game{
    public static void main(String[] args)throws NullPointerException , IOException{
        
        Board brd = new Board();

        Player[] p = new Player[2];
        p[0] = new Player(0 , null);
        p[1] = new Player(1 , null);


        int x = 0;
        while(!brd.hasWon(p[0]) && !brd.hasWon(p[1])){
            for(int i = 0;i < 2;++i){
                System.out.print("Player " + i);
                System.out.print(" is at the position " + (p[i].getCurPos() == null ? 0 : p[i].getCurPos().getNum()));
                System.out.print("        ");
            }
            System.out.println();
            int turn = (x++) % 2;
            System.out.println("Its Player: " + turn + "'s turn");

            brd.rollDice(p[turn]);
        }

        System.out.println("Congratulations!! Player: " + (brd.hasWon(p[0]) ? "0 " : "1 ") + "won this game");
    }
}