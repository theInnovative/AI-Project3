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
	
	public enum Type { NORMAL, HIGHWAY, HARD, BLOCKED }

	public Cell(int x, int y, Type type){
		self = new Point(x,y);
		this.type = type;
		data = new ArrayList<Double>();
		data.add(.125);
	}
	
	public Cell(int x, int y){
		this(x,y,Type.NORMAL);
	}

	public Cell copy(){
		return new Cell(self.x,self.y,type);
	}

	@Override
	public boolean equals(Object o){
		if(!(o instanceof Cell))
			return false;
		Cell c = (Cell) o;
		return c.self.equals(self);
	}
	
	public String toString(){
		NumberFormat formatter = new DecimalFormat(".####");
		String s = "";
		
		if(type == Type.BLOCKED)
			return "";
			
		switch(type){
		case BLOCKED:	s += "Blocked   ";	break;
		case NORMAL:	s += "Normal   ";	break;
		case HIGHWAY:	s += "Highway   ";	break;
		default: s+= "Hard   ";
		}
		
		if(data.get(data.size()-1) >= .0001)
			s += "P() = " + formatter.format(data.get(data.size()-1));
		else
			s += "P() < .0001";
			
		return s;
	}

}
