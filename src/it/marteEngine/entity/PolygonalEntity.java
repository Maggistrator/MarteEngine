package it.marteEngine.entity;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Polygon;

import it.marteEngine.entity.Entity;

public abstract class PolygonalEntity extends Entity {
	public Polygon hitbox = new Polygon();
	public boolean debug;
	
	public PolygonalEntity(float x, float y) {
		super(x, y);
		collidable = false;
	}
	
	public PolygonalEntity(float x, float y, Image image) {
		super(x, y, image);
		collidable = false;
	}

	public void setHitbox(Polygon hitbox) {
		this.hitbox = hitbox;
		collidable  = true;
	}
	
	public void setHitbox(float... points) {
		hitbox = new Polygon(points);
	}
	
	/**задает хитбокс при помощи массива точек (в относительной системе координат)*/
	public void setHitbox(Point... points) {
		for (Point point : points) {
			hitbox.addPoint(point.getX()+x, point.getY()+y);
		}
		collidable  = true;
	}

	// Collide не вызывает collision response! это разница -
	// мы не сталкиваем сущности в их логическом виде, а просто провер€ем факт
	// столкновени€
	// TODO: оптимизировать алогритм, чтобы он не провер€л все сущности в мире
	@Override
	public Entity collide(String type, float x, float y) {
		if (type == null || type.isEmpty()) {
			if (debug)
				System.err.println("PoligonTestError#1: target type is NULL or not defined");
			return null;
		}
		for (Entity current : world.getEntities()) {
			PolygonalEntity entity = (PolygonalEntity) current;
			if (debug)
				System.out.println("entity: " + entity);
			if (debug)
				if (!entity.collidable)
					System.err.println("PoligonTestError#2: target isn't collidable");
			if (debug)
				if (!entity.isType(type))
					System.out.println("PoligonTest Notice #1: target has type " + entity.getCollisionTypes()[0]
							+ " instead of " + type);
			if (debug)
				if (!this.collidable)
					System.err.println("PoligonTestError#3: current entity isn't collidable");
			if (entity.collidable && entity.isType(type) && this.collidable) {
				if (entity.equals(this))
					System.err.println("PoligonTestError#4: entity collides itself");
				if (!entity.equals(this)) {
					float old_x = hitbox.getX();
					float old_y = hitbox.getY();
					hitbox.setX(x);
					hitbox.setY(y);
					if (hitbox.intersects(entity.getHitbox())) {
						if (debug)
							System.out.println("PoligonTest Notice #4: Success! Target is: " + entity);
						return entity;
					} else if (debug)
						System.out.println("PoligonTest Notice #2: collision is not found");
					hitbox.setX(old_x);
					hitbox.setY(old_y);
				}
			}
		}
		if (debug)
			System.out.println("PoligonTest Notice #3: none of conditions are satisfied");
		return null;
	}
	
	
	@Override
	public Entity collideWith(Entity other, float x, float y) {
		if (other.collidable && this.collidable) {
			if (!other.equals(this)) {
				this.collisionResponse(other);
				other.collisionResponse(this);
				//TODO: ¬ќ“ “”“ ƒќЋ∆Ќј Ѕџ“№ Ќќ¬јя –≈јЋ»«ј÷»я
				return other;
				}
			}
		return null;
	}

	@Override
	public List<Entity> collideInto(String type, float x, float y) {
		if (type == null || type.isEmpty())
			return null;
		ArrayList<Entity> collidingEntities = null;
		for (Entity entity : world.getEntities()) {
			if (entity.collidable && entity.isType(type)) {
				if (!entity.equals(this)){
					//TODO: ¬ќ“ “”“ ƒќЋ∆Ќј Ѕџ“№ Ќќ¬јя –≈јЋ»«ј÷»я
					this.collisionResponse(entity);
					entity.collisionResponse(this);
					if (collidingEntities == null)
						collidingEntities = new ArrayList<Entity>();
					collidingEntities.add(entity);
				}
			}
		}
		return collidingEntities;
	}

	/**
	 * Checks if this Entity contains the specified point. The
	 * {@link #collisionResponse(Entity)} is called to notify this entity of the
	 * collision.
	 * 
	 * @param x
	 *            The x coordinate of the point to check
	 * @param y
	 *            The y coordinate of the point to check
	 * @return If this entity contains the specified point
 	 */
	@Override
	public boolean collidePoint(float x, float y) {
		if (true) {
			//TODO: ¬ќ“ “”“ ƒќЋ∆Ќј Ѕџ“№ Ќќ¬јя –≈јЋ»«ј÷»я
			this.collisionResponse(null);
			return true;
		}
		return false;
	}
	
	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		super.render(container, g);
		if(debug) debug(g);
	}
	
	public void debug(Graphics g) {
		g.drawString("x="+x, x-50, y-50);
		g.drawString("y="+y, x-50, y-30);
		
		String str = "";
		float[] arr = hitbox.getPoints();
		for(int i = 0; i < arr.length; i++) {
			str += arr[i]+"|";
			if(i % 2 == 0) {
				g.drawString("point: "+str, x + width + 50, y + 30 * i);
				str = "";
			}
		}
		g.draw(hitbox);
	}
	
	@Deprecated
	@Override
	public void setHitBox(float xOffset, float yOffset, int width, int height) {
		super.setHitBox(xOffset, yOffset, width, height);
	}
	
	public Polygon getHitbox() {
		return hitbox;
	}
	
	/**создает точку с относительными координатами*/
	public void addPoint2Hitbox(int x, int y) {
		hitbox.addPoint(this.x+x, this.y+y);
	}

}
