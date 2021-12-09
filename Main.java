import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Image;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;

//Shane Frantz

public class Main {

    //boolean setting to show the location of the coin square (for testing)
    static boolean showCoin = true;

    static Coin[] coins = new Coin[64];
    //TODO maybe remove this line later when retrying is added (and put it at the beginning of retry button)
    private static final int KEY_LOCATION = (int) (Math.random() * 64);

    static boolean hasFlippedCoin = false;
    static int coinToFlip;

    //string to display either correct or wrong after flipping a coin
    static String displayAfterFlip = "";

    //paint color of displayAfter Flip text
    static Color paintColor = new Color(19, 121, 22);
    public static void main(String[] args) throws IOException {

        //instantiating variables for file paths and images of the coins

        File headsImageFile = null;
        File tailsImageFile = null;

        Image imageH = null;
        Image imageT = null;

        //trying to fill file and image variables

        try {
            headsImageFile = new File ("C:\\Users\\shane\\Desktop\\CoinImages\\headsH.png");
            imageH = new BufferedImage(96, 96, BufferedImage.TYPE_INT_ARGB);
            imageH = ImageIO.read(headsImageFile);
        }

        catch (IOException error) {
            System.out.print("Error making Heads Image: " + error);
        }

        try {
            tailsImageFile = new File ("C:\\Users\\shane\\Desktop\\CoinImages\\tailsT.png");
            imageT = new BufferedImage(96, 96, BufferedImage.TYPE_INT_ARGB);
            imageT = ImageIO.read(tailsImageFile);
        }

        catch (IOException error) {
            System.out.print("Error making Tails Image: " + error);
        }

        //populating variables to be used in for loops (can't be used as before bc the variables were populated in a try block
        Image headsImage = imageH;
        Image tailsImage = imageT;


        //populating coins array

        for (int i = 0; i < 64; i++)
            coins[i] = new Coin(Math.random() < .5);


        //creating window

        JFrame frame = new JFrame("The Impossible Chess Puzzle");
        frame.setSize(1024, 1024);

        //centers window regardless of monitor resolution
        frame.setLocationRelativeTo(null);

        //setting more parameters for the window
        frame.setResizable(false);
        frame.setVisible(true);

        JPanel panel = new JPanel() {
            @Override
            public void paint(Graphics painter) {
                //boolean used to determine the color of the square you're currently on
                //the top left square of a chess board is always white
                boolean white = true;
                int count = 0;
                //nested for loop to get x and y coordinates of current square (starting from top left)
                for (int yCoord = 0; yCoord < 8; yCoord++) {
                    for (int xCoord = 0; xCoord < 8; xCoord++) {
                        if (white)
                            painter.setColor(Color.WHITE);
                        else
                            painter.setColor(Color.BLACK);
                        painter.fillRect(xCoord * 96, yCoord * 96, 96, 96);
                        //swapping the color of the next square to keep a checker pattern
                        white = !white;

                        //populating each square with coins as we go
                            if (coins[count].isHeads)
                                painter.drawImage(headsImage, xCoord * 96, yCoord * 96, this);
                            else
                                painter.drawImage(tailsImage, xCoord * 96, yCoord * 96, this);
                            count++;
                        }

                    //extra swap of colors needed when switching rows
                    //bc the last color in one row is the same as the first color in the second row
                    white = !white;
                }



                //making text for key location
                painter.setColor(Color.BLACK);
                painter.setFont(new Font("Helvetica", Font.PLAIN, 24));
                painter.drawString("Key Location: Square " + KEY_LOCATION, 100, 850);
                if (showCoin)
                    painter.drawString ("Flip= " + coinToFlip, 400, 850);

                //setting paint color for display text
                painter.setColor(paintColor);
                painter.drawString(displayAfterFlip, 100, 900);

            }
        };

        frame.add(panel);
        //exits program when window is closed
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent event) {

                int xPosition = ((event.getX() / 96));
                int yPosition = (event.getY() / 96);

                int squareNumber = xPosition + (8 * yPosition);

                //only activates if you have not flipped a coin yet
                if (xPosition < 8 && yPosition < 8 && !hasFlippedCoin) {

                    hasFlippedCoin = true;

                    if (squareNumber == coinToFlip) {
                        displayAfterFlip = "Correct";
                    }
                    else {
                        paintColor = Color.RED;
                        displayAfterFlip = "Wrong (Correct Coin: " + coinToFlip + ")";
                    }

                    coins[squareNumber].isHeads = !coins[squareNumber].isHeads;
                    panel.repaint();
                }
            }
        });

        for (int i = 0; i < 64; i++) {
            if (xorBitwise(getBoardState(), decimalToBinary(i)).equals(decimalToBinary(KEY_LOCATION)))
                coinToFlip = i;
        }
    }
    //function to find current board state and convert to six digit binary
    public static String getBoardState() {
        String boardState = "";
        int bitToCheck = 0;
        int headsCount = 0;
        while (bitToCheck <= 5) {
            for (int i = 0; i < 64; i++) {
                if (decimalToBinary(i).charAt(bitToCheck) == '1' && coins[i].isHeads)
                    headsCount++;
            }
            if (headsCount % 2 == 0)
                boardState = boardState + ("0");
            else
                boardState = boardState + ("1");
            headsCount = 0;
            bitToCheck++;
        }
        return boardState.toString();
    }


    //method to convert decimal to 6 bit binary (string representation)
    public static String decimalToBinary(int number) {
        //stores binary number
        int[] binaryNumber = new int[32];

        int i = 0;
        while (number > 0) {
            binaryNumber[i] = number % 2;
            number = number / 2;
            i++;
        }

        //string to store binary number until it is made literal
        String binaryString = "";

        //reversing order of bits
        for (int j = i - 1; j >= 0; j--)
            binaryString = binaryString + (binaryNumber[j]);
        //returning String and adding 0s to beginning

        if (binaryString.length() < 6) {
            String zeros = "";
            for (int k = 0; k < 6 - binaryString.length(); k++)
                zeros = zeros + "0";
            return zeros + binaryString;
        }
        return binaryString;

    }

    //does bitwise xor on two strings
    public static String xorBitwise(String a, String b) {
        String answer = "";

        for (int i = 0; i < 6; i++) {
            if (a.charAt(i) == b.charAt(i))
                answer = answer + "0";
            else
                answer = answer + "1";
        }
        return answer;
    }
}

