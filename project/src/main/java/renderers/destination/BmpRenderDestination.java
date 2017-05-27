package renderers.destination;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;


import org.jmrtd.io.SplittableInputStream;


public class BmpRenderDestination implements RenderDestination {
	private final static int BMP_CODE = 19778;
	
	private final PipedOutputStream outputStream;
	private final SplittableInputStream inputStream;
	private int width, height;
	private int lineCounter = 0;
	public BmpRenderDestination(int width, int height) throws IOException {
		this.width = width;
		this.height = height;
		
		inputStream = new SplittableInputStream(new PipedInputStream(outputStream = new PipedOutputStream()), getTotalSize());
		generateHeader();
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	public InputStream getInputStream() {
		return inputStream;
//		return inputStream.getInputStream(0);
	}
	
	public int getTotalSize() {
		return 54 + 3 * width * height + getPadding(width)*height;
	}

	private void generateHeader() throws IOException {
		byte bytes[] = new byte[54];
		byte[]a=intToByteCouple(BMP_CODE);
		bytes[0]=a[1];
		bytes[1]=a[0];
		
		a=intToFourBytes(getTotalSize());
		bytes[5]=a[0];
		bytes[4]=a[1];
		bytes[3]=a[2];
		bytes[2]=a[3];
		
		//data offset
		bytes[10]=54;
		
		bytes[14]=40;
		
		a=intToFourBytes(height);
		bytes[22]=a[3];
		bytes[23]=a[2];
		bytes[24]=a[1];
		bytes[25]=a[0];
		
		a=intToFourBytes(width);
		bytes[18]=a[3];
		bytes[19]=a[2];
		bytes[20]=a[1];
		bytes[21]=a[0];
		
		bytes[26]=1;
		
		bytes[28]=24;
		
		outputStream.write(bytes);
	}
	
	public void writeLine(int[] rgbValues) throws IOException {
		for(int rgb : rgbValues){
			outputStream.write((byte) rgb);
			outputStream.write((byte) (rgb>>8));
			outputStream.write((byte) (rgb>>16));
		}
		for(int j=getPadding(width);j>0;j--) {
			outputStream.write(0);
		}
		lineCounter++;
	}
	
	public boolean isEnded() {
		return lineCounter >= height;
	}

	private byte[] intToByteCouple(int x){
		byte [] array = new byte[2];
		
		array[1]=(byte)  x;
		array[0]=(byte) (x>>8);
		
		return array;
	}
	
	private byte[] intToFourBytes(int x){
		byte [] array = new byte[4];
		
		array[3]=(byte)  x;
		array[2]=(byte) (x>>8);
		array[1]=(byte) (x>>16);
		array[0]=(byte) (x>>24);
		
		return array;
	}
	
	private int getPadding(int rowLength){
		
		int padding = (3*rowLength)%4;
		if(padding!=0)
			padding=4-padding;
		
		
		return padding;
	}
	
}