package view;

import java.awt.Color;
import java.awt.Point;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import view.Cell.Type;

public class Controller implements Initializable {
	
	@FXML
	public RadioButton up;
	@FXML
	public RadioButton down;
	@FXML
	public RadioButton left;
	@FXML
	public RadioButton right;
	@FXML
	public RadioButton normal;
	@FXML
	public RadioButton highway;
	@FXML
	public RadioButton hard;
	@FXML
	public ToggleGroup direction;
	@FXML
	public ToggleGroup type;
	@FXML
	public Button reset;
	@FXML
	public Button undo;
	@FXML
	public Button gtd;

	private SimGUI grid;
	private Cell gridVals[][];
	private int count;
	private ArrayList<MoveObs> moveObs = new ArrayList<MoveObs>();
	
	private final static String path = "Trial Grids\\Grid-";
	
	public enum Direction { UP, DOWN, LEFT, RIGHT }
	
	public class MoveObs{
		Direction dir;
		Cell.Type obs;
		public MoveObs(Direction dir, Cell.Type obs){
			this.dir = dir;
			this.obs = obs;
		}
	}
	
	public class BestPath{
		String s;
		double prob;
		public BestPath(String s, double prob){
			this.s = s;
			this.prob = prob;
		}
		
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		gridVals = new Cell[3][3];
		
		gridVals[0][0] = new Cell(0,0,Type.HIGHWAY);
		gridVals[0][1] = new Cell(0,1,Type.HIGHWAY);
		gridVals[0][2] = new Cell(0,2,Type.HARD);
		gridVals[1][0] = new Cell(1,0);
		gridVals[1][1] = new Cell(1,1);
		gridVals[1][2] = new Cell(1,2);
		gridVals[2][0] = new Cell(2,0);
		gridVals[2][1] = new Cell(2,1,Type.BLOCKED);
		gridVals[2][2] = new Cell(2,2,Type.HIGHWAY);
		
		count = 1;
	}
	
	public void generateTruthData(){
		gtd.setVisible(false);
		for(int X = 0; X < 10; X++){
			Cell gV[][] = new Cell[20][20];
			String moves = "", obs = "";
			Cell.Type o;
			ArrayList<Point> points;
			
			int totalLeft = 20 * 20;
			int nCells = (int)(.5 * totalLeft);
			int hCells = (int)(.2 * totalLeft);
			int tCells = (int)(.2 * totalLeft);
			
			for(int i = 0; i < gV.length; i++){
				for(int j = 0; j < gV[0].length; j++){
					int x = (int)(Math.random() * totalLeft);
					totalLeft--;
					if(x < nCells){
						nCells--;
						gV[i][j] = new Cell(i,j);
					}else if(x < nCells + hCells){
						hCells--;
						gV[i][j] = new Cell(i,j, Cell.Type.HIGHWAY);
					}else if(x < nCells + hCells + tCells){
						tCells--;
						gV[i][j] = new Cell(i,j, Cell.Type.HARD);
					}else{
						gV[i][j] = new Cell(i,j, Cell.Type.BLOCKED);
					}				
				}
			}
			printGrid(X, gV);
			
			for(int Y = 0; Y < 10; Y++){
				Point start;
				points = new ArrayList<Point>();
				do{
					start = new Point((int)(Math.random()*20), (int)(Math.random()*20));
				}while(!validPos(start.x,start.y));
				points.add(start);
				
				Point tmp = start, next;
				for(int i = 0; i < 100; i++){
					switch((int)(Math.random() * 4)){
					case 0:		moves += 'U';	next = new Point(tmp.x-1, tmp.y);	break;
					case 1:		moves += 'D';	next = new Point(tmp.x+1, tmp.y);	break;
					case 2:		moves += 'L';	next = new Point(tmp.x, tmp.y-1);	break;
					default:	moves += 'R';	next = new Point(tmp.x, tmp.y+1);
					}
					
					if(Math.random() >= .9 || !validPos(next.x, next.y))
						next = tmp;
					else
						tmp = next;
					points.add(next);
					
					o = gV[next.x][next.y].type;
					double x = Math.random();
					if(x < .05)
						switch(o){
						case NORMAL: 	obs += 'H';	break;
						case HIGHWAY: 	obs += 'T';	break;
						default: 		obs += 'N';	break;
						}
					else if(x < .1){
						switch(o){
						case NORMAL: 	obs += 'T';	break;
						case HIGHWAY: 	obs += 'N';	break;
						default: 		obs += 'H';	break;
						}
					}else{
						switch(o){
						case NORMAL: 	obs += 'N';	break;
						case HIGHWAY: 	obs += 'H';	break;
						default: 		obs += 'T';	break;
						}
					}
					
				}
				printData(X,Y,moves, obs, points);
			}
		}
		gtd.setVisible(true);
	}
	
	private void printGrid(int x, Cell[][] gV){
		String name = x + "\\Grid.txt";
		FileWriter file;
		
		try {
			file = new FileWriter(path + name, false);
			for(int i = 0; i < 20; i++){
				for(int j = 0; j < 20; j++){
					switch(gV[i][j].type){
					case NORMAL: 	file.write('N');	break;
					case HARD:		file.write('T');	break;
					case HIGHWAY:	file.write('H');	break;
					default:		file.write('B');
					}
				}
				file.write(System.getProperty("line.separator"));
			}

			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void printData(int x, int y, String m, String o, ArrayList<Point> p){
		String name = x + "\\" + "GTD-" + y + ".txt";
		FileWriter file;
		
		try {
			file = new FileWriter(path + name, false);
			for(int i = 0; i < 101; i++){
				file.write(i + ": " + printP(p.remove(0)));
				file.write(System.getProperty("line.separator"));
			}
			
			for(int i = 0; i < 100; i++){
				file.write((i + 1) + ": " + m.charAt(i));
				file.write(System.getProperty("line.separator"));
			}
			
			for(int i = 0; i < 100; i++){
				file.write((i + 1) + ": " + o.charAt(i));
				file.write(System.getProperty("line.separator"));
			}

			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void launchSmallGrid(){
		if(grid == null)
			grid = new SimGUI(3, 3, this);
		updateCells();
		//setColors();
	}
	
	public void addNext(){
		double d[][] = new double[3][3];
		double total = 0;
		Type typeEntered;
		Direction directionEntered;
		
		if(grid == null)
			return;
		
		if((RadioButton)type.getSelectedToggle() == normal)
			typeEntered = Cell.Type.NORMAL;
		else if((RadioButton)type.getSelectedToggle() == highway)
			typeEntered = Cell.Type.HIGHWAY;
		else
			typeEntered = Cell.Type.HARD;
		
		if((RadioButton)direction.getSelectedToggle() == up)
			directionEntered = Direction.UP;
		else if((RadioButton)direction.getSelectedToggle() == down)
			directionEntered = Direction.DOWN;
		else if((RadioButton)direction.getSelectedToggle() == left)
			directionEntered = Direction.LEFT;
		else
			directionEntered = Direction.RIGHT;
		
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 3; j++){
				if(gridVals[i][j].type == Cell.Type.BLOCKED)
					continue;
				int i2 = i, j2 = j;
				d[i][j] += .1 * gridVals[i][j].data.get(count-1);
				
				switch(directionEntered){
				case UP:	i2--;	break;
				case DOWN:	i2++;	break;
				case LEFT:	j2--;	break;
				default:	j2++;
				}
				
				if(validPos(i2,j2))
					d[i2][j2] 	+= .9 * gridVals[i][j].data.get(count-1);
				else
					d[i][j] 	+= .9 * gridVals[i][j].data.get(count-1);
			}
		
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 3; j++){
				if(gridVals[i][j].type == Cell.Type.BLOCKED)
					continue;
				if(typeEntered == gridVals[i][j].type)
					d[i][j] = d[i][j] * .9;
				else
					d[i][j] = d[i][j] * .05;
				total += d[i][j];
			}
		
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 3; j++){
				if(gridVals[i][j].type == Cell.Type.BLOCKED)
					continue;
				double probability = d[i][j]/total;//(int)(d[i][j]/total * 1000000)/1000000.0;
				gridVals[i][j].data.add(probability);
			}
		
		moveObs.add(new MoveObs(directionEntered, typeEntered));
		count++;
		reset.setDisable(false);
		undo.setDisable(false);
		updateCells();
	}
	
	private boolean validPos(int x, int y){
		if(x >= 0 && y >= 0 && x < 3 && y < 3)
			return gridVals[x][y].type != Cell.Type.BLOCKED;
		else
			return false;
	}
	
	public void reset(){
		if(grid == null)
			return;
		
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 3; j++){
				gridVals[i][j].data = new ArrayList<Double>();
				gridVals[i][j].data.add(.125);
			}
		
		moveObs = new ArrayList<MoveObs>();
		count = 1;
		reset.setDisable(true);
		undo.setDisable(true);
		updateCells();
	}
	
	public void undo(){
		if(grid == null)
			return;
		
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 3; j++)
				if(gridVals[i][j].type != Cell.Type.BLOCKED)
					gridVals[i][j].data.remove(count-1);
		
		count--;
		moveObs.remove(count-1);
		if(count == 1){
			reset.setDisable(true);
			undo.setDisable(true);			
		}
		updateCells();
	}
	
	private void setBestPosition(){
		double val = 0.0;
		ArrayList<Point> best = new ArrayList<Point>();
		String s = "Most Likely Cell(s): ";
		boolean multiple = false;
		
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 3; j++){
				if(gridVals[i][j].type == Cell.Type.BLOCKED)
					continue;
				if(gridVals[i][j].data.get(count-1) > val){
					best = new ArrayList<Point>();
					best.add(new Point(i,j));
					val = gridVals[i][j].data.get(count-1);
				}else if(gridVals[i][j].data.get(count-1) == val)
					best.add(new Point(i,j));
			}
		
		Point p = null;
		while(best.size() > 0){
			p = best.remove(0);
			if(multiple)
				s += ", (" + p.x + "," + p.y + ")";
			else
				s += "(" + p.x + "," + p.y + ")";
			multiple = true;
		}
		
		grid.setLabel1("Probability: " + val + "   " + s);
		setBestPath(p);
	}
	
	private void setBestPath(Point p){
		grid.setLabel2("Best Path to "+ printP(p) +": " + viterbi(p, count).s);
	}
	
	private BestPath viterbi(Point p, int x){
		if(!validPos(p.x, p.y))
			return new BestPath("", 0.0);
		
		if(x < 2){
			double d = gridVals[p.x][p.y].data.get(0);
			return new BestPath("[" + printP(p) + " = "+ Double.toString(d) +"]; ", d);
		}
		
		Point parent;
		double prob;
		BestPath bp1, bp2;
		String s;
				
		switch(moveObs.get(x-2).dir){
		case UP:	parent = new Point(p.x + 1, p.y);	break;
		case DOWN:	parent = new Point(p.x - 1, p.y);	break;
		case LEFT:	parent = new Point(p.x, p.y + 1);	break;
		default:	parent = new Point(p.x, p.y - 1);
		}
		
		bp1 = viterbi(p,		x - 1);
		bp2 = viterbi(parent,	x - 1);
		
		if(bp1.prob * .1 > bp2.prob *.9){
			prob = bp1.prob * .1;
			parent =  p;
			s = bp1.s;
		}else{
			prob = bp2.prob * .9;
			s = bp2.s;
		}
		
		if(moveObs.get(x-2).obs == gridVals[p.x][p.y].type)
			prob *= .9;
		else
			prob *= .05;
		
		return new BestPath(s + "[" + printP(p) + " = "
				+ Double.toString(prob) +"]; " ,prob);
	}
	
	private String printP(Point p){
		return "(" + p.x + "," + p.y + ")";
	}
	
	private void updateCells(){
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 3; j++){
				grid.setText(i, j, gridVals[i][j].toString());
				if(gridVals[i][j].type == Cell.Type.BLOCKED){
					grid.setCell(i,j, Color.BLACK);
				}else
					grid.setGradientColor(i,j, 
							gridVals[i][j].data.get(count-1));
			}
		setBestPosition();
	}
	
}
