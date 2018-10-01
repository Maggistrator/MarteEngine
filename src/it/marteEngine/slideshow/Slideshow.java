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
 * ��������, ����������� �� XML-�����
 * @author ����
 * @see core.Slide
 **/
public class Slideshow {
	
	//���������� �������
	private int counter = 0;
	String name;
	//����� ����� ��������
	int lifetime = 0;
	//������� �����
	Slide current_slide = null;
	Music current_track = null;
	//��������� �������
	private HashMap<Integer, Slide> slides = new HashMap<Integer, Slide>();
	private HashMap<Integer, Music> music = new HashMap<Integer, Music>();
	//���� ������� �����-���
	private boolean playing = false;
	GameContainer container;
    //�������, ������� ��������� ������ ������� �������� � �������������� ���������
	private static Font font = new Font("Courier New", Font.PLAIN, 16);
    private static TrueTypeFont slicFont = new TrueTypeFont(font, true,("���������������������������������".toUpperCase()+"����������������������������������").toCharArray());
	
	
	/**
	 * �������������� ��������, �� �� ��������� ���
	 * @param path - ���� � XML-����� �� ���������
	 * @see core.Slideshow.start();
	 * */
	public Slideshow(String path, GameContainer container) throws Exception {
		this.container = container;

		parse(path);
	}
	
	
	/**
	 * ��������� �����-���
	 */
	public void start() {
		playing = true;
	}
	
	/**
	 * ������ ������ ��-�������, ���� �������� �� ����������
	 * @param g - ����� ����������� ������������
	 * */
	public void draw(Graphics g) {
		setCharset_Russian(g);
		//�������� ����, ���� ������� ������ ������� �����, � ����� ����� ��������� ��������� �� ������
		if (counter < lifetime) {
			//��, ��� ��� ���� - �� ������, ��� ��� ��������
			if(playing) {
				update();
				//������ ��������, ���� �����
				if (current_slide != null) {
				current_slide.draw(g);
				}
			}
		}
	}

	/**������������ ��������� ���� ��������*/
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
	 * ������� �������� �� ��������
	 * @param path ���� � ��������
	 * */
	private void parse(String path) throws Exception{
			//������� ���������� ����
			File inputFile = new File(path);
			//������ �������� �������� XML-����������
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			//������ ��������
			Document doc = dBuilder.parse(inputFile);
			//���??
			doc.getDocumentElement().normalize();
			//�������� �������� �������
			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			name = doc.getDocumentElement().getAttribute("name");
			//�������� �������� ������� ��������, ������������ ��� ��������
			System.out.println("Slideshow name" + name);
			//�������� ������ �������
			NodeList slidesList = doc.getElementsByTagName("slide");
			System.out.println("--- ���������� �������� ---");
			System.out.println("������ ��������: "+slidesList.getLength());
			//���������� ������ �� ������, � ������
			for (int i = 0; i < slidesList.getLength(); i++) {
				System.out.println("--- ����� ---");
				Slide slide;
				//��������� ����
				Node nNode = slidesList.item(i);
				System.out.println("\nCurrent Element :" + nNode.getNodeName());
				//���� ��� ������ - ����..
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					//�� ������������ ��� � ��������������� ��� 
					Element eElement = (Element) nNode;
					//������ ���� � �������� ������
					String pic = eElement.getElementsByTagName("picture").item(0).getTextContent();
					System.out.println("�������� ������: "+pic);
					//������ ����� ����� ������
					int lifetime = Integer.parseInt(eElement.getAttribute("lifetime"));
					System.out.println("����� ����� ������: "+lifetime);
					if(pic == null && lifetime == 0)
						throw new Exception("����� "+nNode.getAttributes().getNamedItem("name")+" ��������"); 
					else slide = new Slide(new Image(pic), lifetime, container);
					//��������� ���������
					NodeList subtitles = eElement.getElementsByTagName("text");
					System.out.println("������ ��������� ���������: "+subtitles.getLength());
					//��������� �������
					NodeList voiceover = eElement.getElementsByTagName("voice");
					System.out.println("������ ��������� �������: "+voiceover.getLength());
					slide.slide_lifetime = lifetime;
					
					for (int j = 0; j < subtitles.getLength(); j++) {
						// ���� ���������
						Node subtitle = subtitles.item(j);
						// �����
						String subtitleText = subtitle.getTextContent();
						System.out.println("�����: "+subtitleText);
						if(subtitleText.length()>container.getWidth()/10)
						{
							String[] data = subtitleText.split(" ");
							subtitleText = "";
							data[data.length/2] += "\n";
							for(String word: data) {
								subtitleText += word+" ";
							}
						}
						// ������� ��������� �� ������
						int timing = Integer.parseInt(subtitle.getAttributes().item(0).getTextContent());
						System.out.println("����� ����������� �� ������: "+timing);
						// ��������� � ������
						slide.addText(timing, subtitleText);
					}
					
					//�� �� �������
					for (int j = 0; j < voiceover.getLength(); j++) {
						Node voice = voiceover.item(j);
						String replica = voice.getTextContent();
						System.out.println("���� �������: "+replica);
						int timing = Integer.parseInt(voice.getAttributes().item(0).getTextContent());
						System.out.println("����� ������: "+timing);
						Sound sound = new Sound(replica);
						slide.addVoiceover(timing, sound);
					}
					//��������� �����..
					slides.put(this.lifetime, slide);
					//..� ���������� ����� ������������ ����� ��������
					this.lifetime += lifetime;
			}
		}

		NodeList playlist = doc.getElementsByTagName("music");
		for (int i = 0; i < playlist.getLength(); i++) {
			Node track = playlist.item(i);
			String trackpath = track.getTextContent();
			System.out.println("��������� ��������� ��: "+trackpath);
			int timing = Integer.parseInt(track.getAttributes().item(0).getTextContent());
			Music tracrepresent = new Music(trackpath);
			music.put(timing, tracrepresent);
		}
		System.out.println("it.marteEngine.slideshow lifetime: " + this.lifetime);
	}
	
	/**
	 * ��������� ���������� ������� ���������, � �������� �������� �����
	 **/
	private void setCharset_Russian(Graphics g) {
		if(!g.getFont().equals(slicFont)) {
			g.setFont(slicFont);
			//TODO: ���������, ��� �� �������� ����� ������ ������ ���
			//TODO: ����� �������� � ��������� �������� � ����, �� ������ ������ ������
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
