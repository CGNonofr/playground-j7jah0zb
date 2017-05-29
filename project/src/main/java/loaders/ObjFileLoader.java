package loaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.TextureCoordinates;
import model.Vector;
import model.Point3D;
import model.objects.Face;
import model.objects.Object3D;
import model.objects.Vertex;

public class ObjFileLoader implements ObjLoader {
	private static final Pattern VERTEX_PATTERN=Pattern.compile("v\\s+(?<x>-?[0-9]*\\.?[0-9]+)\\s+(?<y>-?[0-9]*\\.?[0-9]+)\\s+(?<z>-?[0-9]*\\.?[0-9]+)\\s*");
	private static final Pattern TEXTURE_PATTERN=Pattern.compile("vt\\s+(?<x>-?[0-9]*\\.?[0-9]+)\\s+(?<y>-?[0-9]*\\.?[0-9]+).*");
	private static final Pattern NORMAL_PATTERN=Pattern.compile("vn\\s+(?<x>-?[0-9]*\\.?[0-9]+)\\s+(?<y>-?[0-9]*\\.?[0-9]+)\\s+(?<z>-?[0-9]*\\.?[0-9]+)\\s*");
	private static final Pattern FACE_PATTERN=Pattern.compile("f(?:\\s+(?:[0-9]+)?(?:/(?:[0-9]+)?(?:/(?:[0-9]+)?)?)?)+\\s*");
	private static final Pattern FACE_VERTEX_PATTERN=Pattern.compile("\\s+(?<v>[0-9]+)?(?:/(?<vt>[0-9]+)?(?:/(?<vn>[0-9]+)?)?)?");
	
	@Override
	public Object3D load(URL ressource) throws IOException {
		try (BufferedReader reader=new BufferedReader(new InputStreamReader(ressource.openStream()))) {
			
			String line;
			List<Point3D> vertexes=new ArrayList<Point3D>();
			List<Vector> normals=new ArrayList<Vector>();
			List<TextureCoordinates> textureCoordinates=new ArrayList<TextureCoordinates>();
			List<Face> faces=new ArrayList<Face>();
			while((line=reader.readLine())!=null) {
				Matcher m=VERTEX_PATTERN.matcher(line);
				if(m.matches()) {
					vertexes.add(new Point3D(Double.parseDouble(m.group("x")),Double.parseDouble(m.group("y")),Double.parseDouble(m.group("z"))));
					continue;
				}
				m=TEXTURE_PATTERN.matcher(line);
				if(m.matches()) {
					textureCoordinates.add(new TextureCoordinates(Math.max(0, Math.min(1, Double.parseDouble(m.group("x")))), Math.max(0, Math.min(1, Double.parseDouble(m.group("y"))))));
					continue;
				}
				m=NORMAL_PATTERN.matcher(line);
				if(m.matches()) {
					normals.add(new Vector(Double.parseDouble(m.group("x")),Double.parseDouble(m.group("y")),Double.parseDouble(m.group("z"))));
					continue;
				}
				m=FACE_PATTERN.matcher(line);
				if(m.matches()) {
					m=FACE_VERTEX_PATTERN.matcher(line);
					List<Vertex> fvertex=new ArrayList<Vertex>();
					while(m.find()) {
						Point3D v = null;
						Vector n = null;
						TextureCoordinates tc = null;
						if(m.group("v")!=null) {
							v=vertexes.get(Integer.parseInt(m.group("v"))-1);
						}
						if(m.group("vt")!=null) {
							tc=textureCoordinates.get(Integer.parseInt(m.group("vt"))-1);
						}
						if(m.group("vn")!=null) {
							n=normals.get(Integer.parseInt(m.group("vn"))-1);
						}
						fvertex.add(new Vertex(v, tc, n));
					}
					faces.add(new Face(fvertex.toArray(new Vertex[fvertex.size()])));
				}
			}
			System.out.println(faces.size());
			return new Object3D(faces.toArray(new Face[faces.size()]));
		}
	}

}
