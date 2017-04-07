package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SimGUI extends JFrame {

	private static final long serialVersionUID = 2L;
	private static final int delay = 5 * 100000;
	public JButton[][] buttons;
	private JPanel gamePanel;
	private JPanel panel;
	private JPanel panel2;
	private JTextField label1;
	private JTextField label2;

	/**
	 * Constructs a visual representation of the tissue
	 *
	 * @param size the length of one dimension of a square tissue sample.
	 * @param delay how long a cell waits before changing colors (in milliseconds)
	 */
	public SimGUI(int x, int y) {
		super("AI - Project 3 GUI");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);		
		setSize(1200, 700);

		gamePanel = new JPanel(new GridLayout(y, x));
		panel = new JPanel(new BorderLayout());
		panel2 = new JPanel(new BorderLayout());
		buttons = new JButton[y][x];
		label1 = new JTextField("");
		label2 = new JTextField("");

		for (int row = 0; row < y; row++){
			 for (int col = 0; col < x; col++) {
				JButton b = new JButton("");
				b.setFont(new Font("Arial", Font.PLAIN, 10));
				b.setMargin(new Insets(1,1,1,1));
				b.setBackground(Color.WHITE);
				buttons[row][col] = b;
				gamePanel.add(b);
			}
		}
		panel.add(gamePanel, BorderLayout.CENTER);
		panel.add(panel2, BorderLayout.PAGE_END);
		panel2.add(label1, BorderLayout.PAGE_START);
		panel2.add(label2, BorderLayout.PAGE_END);
		this.add(panel);
		this.setVisible(true);
	}

	/**
	* Sets the color of the cell located at a particular position
	*
	* @param row the row position of target cell
	* @param col the col position of target cell
	* @param c the color you wish to change the cell to
	**/
	public void setCell(int row, int col, Color c){
		if(getColor(row, col) == c)
			return;
		try{
		Thread.sleep(0, delay);}catch(Exception e){}
		buttons[row][col].setBackground(c);
		buttons[row][col].repaint();
	}
	
	public void setText(int row, int col, String s){
		buttons[row][col].setText(s);
	}

	public Color getColor(int row, int col){
		return buttons[row][col].getBackground();
	}
	
	public void setGradientColor(int row, int col, double value){
		int x;
		String s;
		
		if(value < .4){
			x = (int)(value/ .4 * (255-50)) + 50;
			s = Integer.toHexString(x);
			
			buttons[row][col].setBackground(Color.decode("0x"+"ff"+s+s));
		}else{
			x = 255 - (int)((value - .4)/ .6 * 255);
			s = Integer.toHexString(x);
			
			buttons[row][col].setBackground(Color.decode("0x"+s+s+"ff"));
		}
		
		buttons[row][col].repaint();		
	}
	
	public void setLabel1(String s){
		label1.setText(s);
	}
	
	public void setLabel2(String s){
		label2.setText(s);
	}
	
	public String getLabel2(){
		return label2.getText();
	}
}

