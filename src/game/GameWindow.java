package game;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

public class GameWindow extends JFrame implements WindowListener {
	private static final long serialVersionUID = 3L;
	QuackPanel gp;
	
	GameWindow() {
		super();
		
		gp = new QuackPanel();
		add(gp, BorderLayout.CENTER);
		
		pack();
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		addWindowListener(this);
	}
	
	public void windowActivated(WindowEvent e)
	{ gp.resumeGame( ); }
	public void windowDeactivated(WindowEvent e)
	{ gp.pauseGame( ); }
	public void windowDeiconified(WindowEvent e)
	{ gp.resumeGame( ); }
	public void windowIconified(WindowEvent e)
	{ gp.pauseGame( ); }
	public void windowClosing(WindowEvent e)
	{ gp.stopGame( ); }
	public void windowOpened(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	
	public static void main(String args[]) {
		new GameWindow();
	}
}