package view;

/**
 * Class representation of Cell node.
 *
 * @author Eric Cajuste
 * @author Thurgood Kilper
 */

import java.awt.Point;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class Cell {

	Point self = null;
	ArrayList<Double> data;
	Type type;
	Controller.BestPath[] bPs = new Controller.BestPath[101];
	
	public enum Type { NORMAL, HIGHWAY, HARD, BLOCKED }

	public Cell(int x, int y, Type type, double d){
		self = new Point(x,y);
		this.type = type;
		data = new ArrayList<Double>();
		data.add(d);
	}
	
	public Cell(int x, int y, double d){
		this(x,y,Type.NORMAL, d);
	}

	@Override
	public boolean equals(Object o){
		if(!(o instanceof Cell))
			return false;
		Cell c = (Cell) o;
		return c.self.equals(self);
	}
	
	public String toString(){
		NumberFormat formatter = new DecimalFormat(".###");
		String s = "";
		
		if(type == Type.BLOCKED)
			return "";
			
		switch(type){
		case BLOCKED:	s += "B   ";	break;
		case NORMAL:	s += "N   ";	break;
		case HIGHWAY:	s += "H   ";	break;
		default: s+= "T   ";
		}
		
		if(data.get(data.size()-1) >= .001)
			s += ", " + formatter.format(data.get(data.size()-1));
		else
			s += ", <.001";
			
		return s;
	}

}
