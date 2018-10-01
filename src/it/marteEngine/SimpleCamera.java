package it.marteEngine;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import it.marteEngine.entity.Entity;

public class SimpleCamera {
	// ��������, �� ������� ������� ������
	Entity toFollow;
	// ������� ������
	Rectangle bounds;
	// ������� ��������, �� ������� ������� ������
	Rectangle hitBox2Follow = new Rectangle(0,0,20,20);
	//������ ���������� �����
	private Vector2f OutOfBounds = new Vector2f();
	
	public SimpleCamera(Entity toFollow, Rectangle bounds, GameContainer container) {
		// �������������� ���������� 
		this.toFollow = toFollow;
		this.bounds = bounds;
		hitBox2Follow = new Rectangle(toFollow.x,toFollow.y,toFollow.hitboxWidth,toFollow.hitboxHeight);
		/*
		 * ���� �������, �� ������� ����� ������� �� ������� 
		 * �� �����, �� ��� ����������� �� [������� ������� - (������� ����������+�������)/2]
		 * ��������, ��� ��������� �������, � ������� ������ ������ �� �������
		 * �� � ���������, ������ ������������
		 * */
		if(bounds.getWidth()>container.getWidth()){
			bounds.setWidth(bounds.getWidth()-(container.getWidth()+hitBox2Follow.getWidth())/2);
			bounds.setX(bounds.getX()+(container.getWidth()+hitBox2Follow.getWidth())/2);
		}
		// ������ ��� ���� ��������
		if(bounds.getHeight()>container.getHeight()){
			bounds.setHeight(bounds.getHeight()-(container.getHeight()+hitBox2Follow.getHeight())/2);
			bounds.setY(bounds.getY()+(container.getHeight()+hitBox2Follow.getHeight())/2);
		}
		
	}
	public void draw(GameContainer container, Graphics g) throws SlickException{
		/*
		 * ������� ����������� �������� ������������ ��� �������� ������ ����������
		 * � ������ �������. ���� �������� �� ����� �� ��� �������, ������ ������
		 * �� ���
		 * */
		if(toFollow.x>bounds.getX()&&toFollow.x<bounds.getWidth())
			OutOfBounds.x = toFollow.x;
		if(toFollow.y>bounds.getY()&&toFollow.y<bounds.getHeight())
			OutOfBounds.y = toFollow.y;
		/*
		 * ��� ������ �����������, ���������� ���� ����������� ��������
		 * �� ����������, ��������������� ����������� ��������,
		 * �� ��� ���, ���� �� ������� ������������� ������� �� ���������
		 * ����������, ������ �������� ����������
		 * (�������� ������ � (���������+�������)/2, ��� �������������)
		 * */
		g.translate(-(OutOfBounds.x-(container.getWidth()-hitBox2Follow.getWidth())/2),
				-(OutOfBounds.y-(container.getHeight()-hitBox2Follow.getHeight())/2));
	}
	
	@Override
	public String toString() {
		//� ��� toString(), ���
		String result = "SimpleCamera:";
		result+=bounds.getX();
		result+="x"+bounds.getY();
		result+="x"+bounds.getWidth();
		result+="x"+bounds.getHeight();
		return result;
	}
	
}
