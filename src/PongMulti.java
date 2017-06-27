/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.swing.JFrame;
import java.net.*;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JPanel;


/**
 *
 * @author Animesh
 */
public class PongMulti extends JPanel implements KeyListener{
    private final static int WIDTH = 700, HEIGHT = 450;
    private int BallXPosition = 0600; 
    private int BallYPosition = 0;
    private int BallXVelocity = 1;
    private int BallYVelocity = 1;
    private int Paddle1Position = 200;
    private int Paddle2Position = 200;
    private int Paddle1Direction = 0;
    private int Paddle2Direction = 0;
    private int counter = 0;
    ServerSocket serverSocket;
    int port;
    Socket clientSocket;
    OutputStream ostream;
    PrintWriter pwriter;
    char c;
    /**
     * @param args the command line arguments
     */
    int x = WIDTH / 2;
    int y = HEIGHT / 2;
    
     public class KeyMulti implements Runnable{
      BufferedReader reader;
             
     
     
      public KeyMulti(Socket socket){
        try{
            
            clientSocket = socket;
            InputStreamReader isReader = new InputStreamReader(clientSocket.getInputStream());
            reader = new BufferedReader(isReader);
        }catch(IOException ex){
            System.out.println(ex);
            }
        }
      
      public void run(){
          String message;
            
            try {
                while(true){
                message = reader.readLine();
                c = message.charAt(0);
                System.out.println(c);
                if(c == 'i'){
                    Paddle2Direction = -1;
                }
                else if(c == 'k'){
                    Paddle2Direction = 1;
                }
                else if(c == 'q'){
                    Paddle2Direction = 0;
                }
         
                }
            } catch (IOException ex) {
                System.out.println();
            }
      }
     }
    
    
    
    private void reset(){
    BallXPosition = 400; 
    BallYPosition = 0;
    BallXVelocity = 1;
    BallYVelocity = 1;
    Paddle1Position = 200;
    Paddle2Position = 200;
    Paddle1Direction = 0;
    Paddle2Direction = 0;
    
    }
    
    private void wallCollision(){
       BallXPosition += BallXVelocity;
       BallYPosition += BallYVelocity;
       
       
       if(BallXPosition + 30 > WIDTH - 10){
           reset();
       }
       if(BallXPosition + 30 < 30){
           reset();
       }
       if(BallYPosition + 30 > HEIGHT - 40){
           BallYVelocity += -1.5;
       }
       if(BallYPosition + 30 < 30){
           BallYVelocity += 1.5;
       }
    }
    
    private void paddle1Collision(){
        if(BallXPosition + 30 < 110 && BallYPosition < Paddle1Position + 50 && BallYPosition > Paddle1Position - 50){
            BallXVelocity = -BallXVelocity;
        }
    }
    
    private void paddle2Collision(){
        if(BallXPosition - 30 > 550 && BallYPosition < Paddle2Position + 50 && BallYPosition > Paddle2Position - 50){
            BallXVelocity = -BallXVelocity;
        }
    }
    
    private void moveBall(){
       wallCollision();
       paddle1Collision();
       paddle2Collision();
    }
    
    private void movePaddle(){
        if(Paddle1Position > 0){            
            Paddle1Position += Paddle1Direction;
        }else{
            Paddle1Position = 1;
        }
        
        if(Paddle1Position < HEIGHT - 110){
            Paddle1Position += Paddle1Direction;
        }else{
            Paddle1Position = HEIGHT - 110;
        }
        
        if(Paddle2Position > 0){            
            Paddle2Position += Paddle2Direction;
        }else{
            Paddle2Position = 1;
        }
        
        if(Paddle2Position < HEIGHT - 110){
            Paddle2Position += Paddle2Direction;
        }else{
            Paddle2Position = HEIGHT - 110;
        }
    }
    
    public PongMulti() throws InterruptedException, IOException{
        JFrame frame2 = new JFrame();
        frame2.setSize(WIDTH, HEIGHT);
        frame2.add(this);
        frame2.setTitle("Pong 1");
        frame2.setBackground(Color.WHITE);
        frame2.setResizable(false);
        frame2.setVisible(true);
        frame2.addKeyListener(this);
        frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        serverSocket = new ServerSocket(5555);
        clientSocket = serverSocket.accept();
        pwriter = new PrintWriter(clientSocket.getOutputStream(), true);
        Thread listener = new Thread(new KeyMulti(clientSocket));
        listener.start();
        while(true){
            moveBall();
            movePaddle();
            repaint();
            Thread.sleep(5);
        }
    }
 
    @Override
    public void paint(Graphics g){
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.fillOval(BallXPosition, BallYPosition, 30, 30);
        g2d.fillRect(50, Paddle1Position, 30, 80);
        g2d.fillRect(600, Paddle2Position, 30, 80);
    }
    
    
    public static void main(String[] args) throws InterruptedException, IOException{
        PongMulti p = new PongMulti();
    }

    @Override
    public void keyTyped(KeyEvent e) {
       
    }
    
    @Override
       public void keyPressed(KeyEvent e){
        if(e.getKeyCode() == KeyEvent.VK_W){
            Paddle1Direction = -1;
            pwriter.println("w");
            pwriter.flush();
        }
        if(e.getKeyCode() == KeyEvent.VK_S){
            Paddle1Direction = 1;
            pwriter.println("s");
            pwriter.flush();
        }

    }
    
    
    @Override
    public void keyReleased(KeyEvent e){
        if(e.getKeyCode() == KeyEvent.VK_W){
            Paddle1Direction = 0;
            pwriter.println("q");
            pwriter.flush();
        }
        if(e.getKeyCode() == KeyEvent.VK_S){
            Paddle1Direction = 0;
            pwriter.println("q");
            pwriter.flush();
        }

        
    }
    
    
}
