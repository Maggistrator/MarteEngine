package it.marteEngine.slideshow;


import java.awt.Dimension;
import java.util.HashMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;

/** 
 * Slide �������� ��������� ��� Slideshow 
 * @author ����
 * @see core.Slideshow
 * */
public class Slide {

	//�����, ������� ����� �������������� �� ������, � ����� FPS
	int slide_lifetime;
	//���������� �������, ����������� �������, ����������� ������
	private int counter = 0;
	Image image;
    //������� �������������� �����, � ��� ������� �� ������
	String current_text = "";
	Dimension textPosition = new Dimension(50, 550);
	//��������� ������� ������� � ���������� 
	private HashMap<Integer, Sound> voiceover = new HashMap<Integer, Sound>();
	//��������� ��������� � ����������
	private HashMap<Integer, String> replicas = new HashMap<Integer, String>();
	//���� ���������� ��������� ������
	public boolean isFinished = false;
	GameContainer container;
	Color opaque_black = new Color(0,0,0,0.7f);
	
	private Slide() {}
	
	/**
	 * �������������� �����
	 * @param image - ����������� ������
	 * @param slide_lifetime - ����� "�����" ������
	 * */
	public Slide(Image image, int slide_lifetime, GameContainer container) {
		this.image = image;
		this.slide_lifetime = slide_lifetime;
		this.container = container;

		System.out.println("container "+container);
	}
	
	/**
	 * �������� �� ��������� ������, ���������, � ������������� ��������������� �����
	 * @param g - ����� ����������� ������������ ��� ���������
	 * */
	public void draw(Graphics g) {
		if (counter < slide_lifetime) {
			update();
			g.drawImage(image, 0, 0);

			g.setColor(opaque_black);
			g.fillRect(0, container.getHeight()-80,container.getWidth(), 80);
			g.setColor(Color.white);
			g.drawString(current_text, container.getWidth()/10, container.getHeight()-70);
		}else isFinished = true;
	}
	
	//������� �������� ��������� �� ������� ���������� � ������� �����
	private void update() {
		//�������
		for (int time : voiceover.keySet()) {
			if(counter == time) voiceover.get(time).play();
		}
		//�������
		for (int time : replicas.keySet()) {
			if(counter == time) current_text = replicas.get(time);
		}
		counter++;
	}

	/**
	 * ��������� �������� �� ����� ������� �������
	 * @param time - ����� ������ ������� � �����
	 * @param sound - ������ Sound, ���������� - ������� �� ������
	 * */
	public void addVoiceover(int time, Sound sound) {
		voiceover.put(time, sound);	
	}
	
	/**
	 * ��������� �������� �� ����� ������� ���������
	 * @param time - ����� ������ ��������� ������ � ����� 
	 * <br>����� ������������, ���� ����� �� ��������, ��� ����� ��������� ������ �� �������
	 * @param text - ������������� �����, ������� ������� ����������
	 * */
	public void addText(int time, String text) {
		replicas.put(time, text);
	}
	
	/**
	 * ��������� �������� �� ����� ������� ���������
	 * @param time - ����� ������ ��������� ������ � ����� 
	 * <br>����� ������������, ���� ����� �� ��������, ��� ����� ��������� ������ �� �������
	 * @param text - ������������� �����, ������� ������� ����������
	 * @param position - ����� ������ �� ������
	 * */
	public void addText(int time, String text, Dimension position) {
		replicas.put(time, text);
		textPosition = position;
	}
}
