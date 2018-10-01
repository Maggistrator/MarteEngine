package it.marteEngine;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import it.marteEngine.entity.Entity;

public class SimpleCamera {
	// сущность, за которой следует камера
	Entity toFollow;
	// границы камеры
	Rectangle bounds;
	// хитбокс сущности, за которой следует камера
	Rectangle hitBox2Follow = new Rectangle(0,0,20,20);
	//важная непонятная херня
	private Vector2f OutOfBounds = new Vector2f();
	
	public SimpleCamera(Entity toFollow, Rectangle bounds, GameContainer container) {
		// инициализируем переменные 
		this.toFollow = toFollow;
		this.bounds = bounds;
		hitBox2Follow = new Rectangle(toFollow.x,toFollow.y,toFollow.hitboxWidth,toFollow.hitboxHeight);
		/*
		 * если область, за которой нужно следить не влезает 
		 * на экран, то она уменьшается до [размеры области - (размеры контейнера+хитбокс)/2]
		 * вероятно, это свободная область, в которой камера следит за игроком
		 * за её пределами, камера открепляется
		 * */
		if(bounds.getWidth()>container.getWidth()){
			bounds.setWidth(bounds.getWidth()-(container.getWidth()+hitBox2Follow.getWidth())/2);
			bounds.setX(bounds.getX()+(container.getWidth()+hitBox2Follow.getWidth())/2);
		}
		// высоты это тоже касается
		if(bounds.getHeight()>container.getHeight()){
			bounds.setHeight(bounds.getHeight()-(container.getHeight()+hitBox2Follow.getHeight())/2);
			bounds.setY(bounds.getY()+(container.getHeight()+hitBox2Follow.getHeight())/2);
		}
		
	}
	public void draw(GameContainer container, Graphics g) throws SlickException{
		/*
		 * свобода перемещения сущности определяется как половина высоты контейнера
		 * в каждую сторону. Пока сущность не вышла за эти границы, камера следит
		 * за ней
		 * */
		if(toFollow.x>bounds.getX()&&toFollow.x<bounds.getWidth())
			OutOfBounds.x = toFollow.x;
		if(toFollow.y>bounds.getY()&&toFollow.y<bounds.getHeight())
			OutOfBounds.y = toFollow.y;
		/*
		 * сия ибучая конструкция, перемещает весь графический контекст
		 * на координату, противоположную координатам сущности,
		 * до тех пор, пока до границы отслеживаемой области не останется
		 * расстояние, равное половине контейнера
		 * (учитывая допуск в (контейнер+хитбокс)/2, для центрирования)
		 * */
		g.translate(-(OutOfBounds.x-(container.getWidth()-hitBox2Follow.getWidth())/2),
				-(OutOfBounds.y-(container.getHeight()-hitBox2Follow.getHeight())/2));
	}
	
	@Override
	public String toString() {
		//а это toString(), лол
		String result = "SimpleCamera:";
		result+=bounds.getX();
		result+="x"+bounds.getY();
		result+="x"+bounds.getWidth();
		result+="x"+bounds.getHeight();
		return result;
	}
	
}
