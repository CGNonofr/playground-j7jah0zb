package renderers.destination;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Arrays;

import org.jmrtd.io.SplittableInputStream;


public class BmpRenderDestination implements RenderDestination {
	private final static int BMP_CODE = 19778;
	
	private final PipedOutputStream outputStream;
	private final SplittableInputStream inputStream;
	private int width, height;
	private int lineCounter = 0;
	private byte[] availableData;
	private int availableDataPos;
	public BmpRenderDestination(int width, int height) throws IOException {
		this.width = width;
		this.height = height;
		availableDataPos = 0;
		availableData = new byte[getTotalSize()];
		Arrays.fill(availableData, (byte) 0xff);
		
		inputStream = new SplittableInputStream(new PipedInputStream(outputStream = new PipedOutputStream(), getTotalSize()), getTotalSize());
		generateHeader();
		

	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	public InputStream getInputStream() {
		return inputStream.getInputStream(0);
	}
	
	public InputStream getFilledInputStream() throws IOException {
		return new ByteArrayInputStream(availableData);
	}
	
	public int getTotalSize() {
		return 54 + 3 * (width + getPadding(width)) * height;
	}

	private void generateHeader() throws IOException {
		byte bytes[] = availableData;
		Arrays.fill(bytes, 0, 54, (byte) 0x00);
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
		
		outputStream.write(bytes, 0, availableDataPos = 54);
	}
	
	public void writeLine(int[] rgbValues) throws IOException {
		for(int rgb : rgbValues){
			outputStream.write(availableData[availableDataPos++] = (byte) rgb);
			outputStream.write(availableData[availableDataPos++] = (byte) (rgb>>8));
			outputStream.write(availableData[availableDataPos++] = (byte) (rgb>>16));
		}
		for(int j=getPadding(width);j>0;j--) {
			outputStream.write(availableData[availableDataPos++] = 0);
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