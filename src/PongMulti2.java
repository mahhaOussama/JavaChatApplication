/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JPanel;


/**
 *
 * @author Animesh
 */
public class PongMulti2 extends JPanel implements KeyListener{
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
    char c;
    int P1counter = 0;
    int P2counter = 0;
    
    
    String username;
    Socket sock;
    Boolean isConnected = false;
    int port;
    OutputStream ostream;
    PrintWriter pwrite;
    /**
     * @param args the command line arguments
     */
    int x = WIDTH / 2;
    int y = HEIGHT / 2;
    
    private void reset(){

    Paddle1Position = 200;
    Paddle2Position = 200;
    Paddle1Direction = 0;
    Paddle2Direction = 0;
    }
    
    public class KeyMulti implements Runnable{
        BufferedReader reader;
        PrintWriter client;
        public KeyMulti(Socket socket){
        
        try{

                sock = socket;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);
            }catch(Exception ex){
                System.out.println(ex);
            }
  
    
        }
        @Override
        public void run() {
            String message;
            
            try {
                while(true){
                message = reader.readLine();
                c = message.charAt(0);
                if(c == 'w'){
                    Paddle1Direction = -1;
                }
                else if(c == 's'){
                    Paddle1Direction = 1;
                }
                else if(c == 'q'){
                    Paddle1Direction = 0;
                }
                else if(c == 'X'){
                    BallXPosition = Integer.parseInt(message.substring(1));
                }
                else if(c == 'Y'){
                    BallYPosition = Integer.parseInt(message.substring(1));
                }
                }
                
            } catch (IOException ex) {
                System.out.println();
            }
        }
    }    
    private void wallCollision(){


       
       if(BallXPosition + 30 > WIDTH - 10){
           reset();
           P2counter++;
       }
       if(BallXPosition + 30 < 30){
           reset();
           P1counter++;
       }

    }
    
 
    
    private void moveBall(){
       wallCollision();

     
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
    
  
    public PongMulti2(String IPAdress) throws InterruptedException, IOException{
        JFrame frame1 = new JFrame();
        frame1.setSize(WIDTH, HEIGHT);
        frame1.add(this);
        frame1.setTitle("Pong 2");
        frame1.setBackground(Color.WHITE);
        frame1.setResizable(false);
        frame1.setVisible(true);
        frame1.addKeyListener(this);
        frame1.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame1.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent evt){
                frame1.dispose();
            }
        });
        try{
                    sock = new Socket(IPAdress, 5555);
                    isConnected = true;
                    ostream = sock.getOutputStream();
                    pwrite = new PrintWriter(ostream, true);
                    Thread listener = new Thread(new KeyMulti(sock));
                    listener.start();

                }catch(IOException ex){
                 System.out.println(ex);   
                }
        while(true){
            moveBall();
            movePaddle();
            repaint();
            Thread.sleep(5);
            if(P1counter == 1000){
                frame1.dispose();
       }
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
        Font font = new Font("Serif", Font.PLAIN, 30);
        g2d.setFont(font);
        g2d.drawString(Integer.toString(P1counter), 50, 25);
        g2d.drawString(Integer.toString(P2counter), 500, 25);
    }
    

    @Override
    public void keyTyped(KeyEvent e) {
       
    }
    
    @Override
       public void keyPressed(KeyEvent e){
        if(e.getKeyCode() == KeyEvent.VK_I){
            Paddle2Direction = -1;
            pwrite.println("i");
            pwrite.flush();
        }
        if(e.getKeyCode() == KeyEvent.VK_K){
            Paddle2Direction = 1;
            pwrite.println("k");
            pwrite.flush();
        }

        
    }
    
    
    @Override
    public void keyReleased(KeyEvent e){
        if(e.getKeyCode() == KeyEvent.VK_I){
            Paddle2Direction = 0;
            pwrite.println("q");
            pwrite.flush();
        }
        if(e.getKeyCode() == KeyEvent.VK_K){
            Paddle2Direction = 0;
            pwrite.println("q");
            pwrite.flush();
        }

        
    }
    
    
}
