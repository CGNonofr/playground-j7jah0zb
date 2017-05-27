package renderers.destination;

import java.io.IOException;

public interface RenderDestination {
	public void writeLine(int[] line) throws IOException;
}
