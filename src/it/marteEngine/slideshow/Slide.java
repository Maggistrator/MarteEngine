package it.marteEngine.slideshow;


import java.awt.Dimension;
import java.util.HashMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;

/** 
 * Slide является элементом для Slideshow 
 * @author Сова
 * @see core.Slideshow
 * */
public class Slide {

	//время, которое слайд отрисовывается на экране, в тиках FPS
	int slide_lifetime;
	//внутренний счётчик, управляющий слайдом, изображение слайда
	private int counter = 0;
	Image image;
    //текущий отрисовываемый текст, и его позиция на экране
	String current_text = "";
	Dimension textPosition = new Dimension(50, 550);
	//коллекция записей озвучки с таймингами 
	private HashMap<Integer, Sound> voiceover = new HashMap<Integer, Sound>();
	//коллекция субтитров с таймингами
	private HashMap<Integer, String> replicas = new HashMap<Integer, String>();
	//флаг завершения отрисовки слайда
	public boolean isFinished = false;
	GameContainer container;
	Color opaque_black = new Color(0,0,0,0.7f);
	
	private Slide() {}
	
	/**
	 * Инициализирует слайд
	 * @param image - изображение слайда
	 * @param slide_lifetime - время "жизни" слайда
	 * */
	public Slide(Image image, int slide_lifetime, GameContainer container) {
		this.image = image;
		this.slide_lifetime = slide_lifetime;
		this.container = container;

		System.out.println("container "+container);
	}
	
	/**
	 * Отвечает за отрисовку слайда, субтитров, и своевременное воспроизведение звука
	 * @param g - набор графических инструментов для отрисовки
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
	
	//перебор колекций таймингов на предмет совпадений с текущим тиком
	private void update() {
		//озвучка
		for (int time : voiceover.keySet()) {
			if(counter == time) voiceover.get(time).play();
		}
		//субитры
		for (int time : replicas.keySet()) {
			if(counter == time) current_text = replicas.get(time);
		}
		counter++;
	}

	/**
	 * Позволяет добавить на слайд реплику озвучки
	 * @param time - время начала дорожки в тиках
	 * @param sound - объект Sound, собственно - дорожка со звуком
	 * */
	public void addVoiceover(int time, Sound sound) {
		voiceover.put(time, sound);	
	}
	
	/**
	 * Позволяет добавить на слайд реплику субтитров
	 * @param time - время начала отрисовки текста в тиках 
	 * <br>Будет показываться, пока текст не сменится, или время отрисовки слайда не истечёт
	 * @param text - многострочный текст, который следует отобразить
	 * */
	public void addText(int time, String text) {
		replicas.put(time, text);
	}
	
	/**
	 * Позволяет добавить на слайд реплику субтитров
	 * @param time - время начала отрисовки текста в тиках 
	 * <br>Будет показываться, пока текст не сменится, или время отрисовки слайда не истечёт
	 * @param text - многострочный текст, который следует отобразить
	 * @param position - место текста на экране
	 * */
	public void addText(int time, String text, Dimension position) {
		replicas.put(time, text);
		textPosition = position;
	}
}
