package it.marteEngine.slideshow;


import java.awt.Font;
import java.io.File;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.Sound;
import org.newdawn.slick.TrueTypeFont;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Слайдшоу, загружаемое из XML-файла
 * @author Сова
 * @see core.Slide
 **/
public class Slideshow {
	
	//внутренний счётчик
	private int counter = 0;
	String name;
	//время жизни слайдшоу
	int lifetime = 0;
	//текущий слайд
	Slide current_slide = null;
	Music current_track = null;
	//коллекция слайдов
	private HashMap<Integer, Slide> slides = new HashMap<Integer, Slide>();
	private HashMap<Integer, Music> music = new HashMap<Integer, Music>();
	//Флаг статуса слайд-шоу
	private boolean playing = false;
	GameContainer container;
    //костыль, который загружает массив русских символов в поддерживаемые кодировки
	private static Font font = new Font("Courier New", Font.PLAIN, 16);
    private static TrueTypeFont slicFont = new TrueTypeFont(font, true,("йцукенгшщзхъфывапролджэячсмитьбюё".toUpperCase()+"йцукенгшщзхъфывапролджэячсмитьбюё•").toCharArray());
	
	
	/**
	 * Инициализирует слайдшоу, но не запускает его
	 * @param path - путь к XML-файлу со сценарием
	 * @see core.Slideshow.start();
	 * */
	public Slideshow(String path, GameContainer container) throws Exception {
		this.container = container;

		parse(path);
	}
	
	
	/**
	 * Запускает слайд-шоу
	 */
	public void start() {
		playing = true;
	}
	
	/**
	 * Рисует слайды по-очереди, пока слайдшоу не закончится
	 * @param g - набор графических инструментов
	 * */
	public void draw(Graphics g) {
		setCharset_Russian(g);
		//слайдшоу живёт, пока счётчик меньше времени жизни, а затем почти полностью стирается из памяти
		if (counter < lifetime) {
			//то, что оно живёт - не значит, что оно работает
			if(playing) {
				update();
				//слайды рисуются, пока могут
				if (current_slide != null) {
				current_slide.draw(g);
				}
			}
		}
	}

	/**контролирует жизненный цикл слайдшоу*/
	private void update() {
		slides.forEach((time, slide) -> {
			if(counter == time) { 
				current_slide = slide;
			}
		});		
		music.forEach((time, track)->{
			if(counter==time) {
				if(current_track != null && current_track.playing())current_track.stop();
				current_track = track;
				track.play();
			}
		});
		counter++;
	}
	
	
	/**
	 * парсинг слайдшоу из сценария
	 * @param path путь к сценарию
	 * */
	private void parse(String path) throws Exception{
			//Создаем логический файл
			File inputFile = new File(path);
			//Создаём фаабрику парсеров XML-документов
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			//Парсим документ
			Document doc = dBuilder.parse(inputFile);
			//втф??
			doc.getDocumentElement().normalize();
			//получаем корневой элемент
			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			name = doc.getDocumentElement().getAttribute("name");
			//Получаем корневой атрибут элемента, обозначающий имя слайдшоу
			System.out.println("Slideshow name" + name);
			//Получаем список слайдов
			NodeList slidesList = doc.getElementsByTagName("slide");
			System.out.println("--- Начинается слайдшоу ---");
			System.out.println("размер слайдшоу: "+slidesList.getLength());
			//перебираем слайды по одному, и парсим
			for (int i = 0; i < slidesList.getLength(); i++) {
				System.out.println("--- Слайд ---");
				Slide slide;
				//отдельный узел
				Node nNode = slidesList.item(i);
				System.out.println("\nCurrent Element :" + nNode.getNodeName());
				//если тип уровня - узел..
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					//то переобразуем его в соответствующий тип 
					Element eElement = (Element) nNode;
					//Парсим путь к картинке слайда
					String pic = eElement.getElementsByTagName("picture").item(0).getTextContent();
					System.out.println("Картинка слайда: "+pic);
					//парсим время жизни слайда
					int lifetime = Integer.parseInt(eElement.getAttribute("lifetime"));
					System.out.println("Время жизни слайда: "+lifetime);
					if(pic == null && lifetime == 0)
						throw new Exception("Слайд "+nNode.getAttributes().getNamedItem("name")+" повреждён"); 
					else slide = new Slide(new Image(pic), lifetime, container);
					//коллекция субтитров
					NodeList subtitles = eElement.getElementsByTagName("text");
					System.out.println("размер коллекции субтитров: "+subtitles.getLength());
					//коллекция озвучки
					NodeList voiceover = eElement.getElementsByTagName("voice");
					System.out.println("размер коллекции озвучки: "+voiceover.getLength());
					slide.slide_lifetime = lifetime;
					
					for (int j = 0; j < subtitles.getLength(); j++) {
						// узел субтитров
						Node subtitle = subtitles.item(j);
						// текст
						String subtitleText = subtitle.getTextContent();
						System.out.println("Текст: "+subtitleText);
						if(subtitleText.length()>container.getWidth()/10)
						{
							String[] data = subtitleText.split(" ");
							subtitleText = "";
							data[data.length/2] += "\n";
							for(String word: data) {
								subtitleText += word+" ";
							}
						}
						// тайминг появления на экране
						int timing = Integer.parseInt(subtitle.getAttributes().item(0).getTextContent());
						System.out.println("Время отображения на экране: "+timing);
						// добавляем к слайду
						slide.addText(timing, subtitleText);
					}
					
					//та же история
					for (int j = 0; j < voiceover.getLength(); j++) {
						Node voice = voiceover.item(j);
						String replica = voice.getTextContent();
						System.out.println("Узел озвучки: "+replica);
						int timing = Integer.parseInt(voice.getAttributes().item(0).getTextContent());
						System.out.println("Время начала: "+timing);
						Sound sound = new Sound(replica);
						slide.addVoiceover(timing, sound);
					}
					//добавляем слайд..
					slides.put(this.lifetime, slide);
					//..и продлеваем время сущесвования всего слайдшоу
					this.lifetime += lifetime;
			}
		}

		NodeList playlist = doc.getElementsByTagName("music");
		for (int i = 0; i < playlist.getLength(); i++) {
			Node track = playlist.item(i);
			String trackpath = track.getTextContent();
			System.out.println("Саундтрек сменяется на: "+trackpath);
			int timing = Integer.parseInt(track.getAttributes().item(0).getTextContent());
			Music tracrepresent = new Music(trackpath);
			music.put(timing, tracrepresent);
		}
		System.out.println("it.marteEngine.slideshow lifetime: " + this.lifetime);
	}
	
	/**
	 * Позволяет установить русскую кодировку, и добавить переносы строк
	 **/
	private void setCharset_Russian(Graphics g) {
		if(!g.getFont().equals(slicFont)) {
			g.setFont(slicFont);
			//TODO: Придумать, как не вызывать смену шрифта каждый тик
			//TODO: Найти отладчик с проверкой объектов в хипе, на случай утечек памяти
		}
	}
	
	public int getLifetime() {
		return lifetime;
	}
	
	public int getCurrentSlideshowTic() {
		return counter;
	}
	
	public Slide getCurrentSlide() {
		return current_slide;
	}		
}
