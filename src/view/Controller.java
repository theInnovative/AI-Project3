package view;

import java.awt.Color;
import java.awt.Point;
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

	private SimGUI grid;
	private Cell gridVals[][];
	private int count;
	
	public enum Direction { UP, DOWN, LEFT, RIGHT }

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
		
		while(best.size() > 0){
			Point p = best.remove(0);
			if(multiple)
				s += ", (" + p.x + "," + p.y + ")";
			else
				s += "(" + p.x + "," + p.y + ")";
			multiple = true;
		}
		
		grid.setLabel1("Probability: " + val + "   " + s);
	}
	
	private void setBestPath(){
		grid.setLabel2("Best Path: ");
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
		setBestPath();
	}
	
}
