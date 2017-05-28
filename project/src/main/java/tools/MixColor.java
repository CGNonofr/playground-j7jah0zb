package tools;


import java.awt.Color;

public class MixColor{

	public static Integer mixColor(int c1, int c2, double p) {
		if( (p >= 0) && (p <= 1) ) {
			
			float[] f1=Color.RGBtoHSB((c1>>16) & 0xff, (c1>>8) & 0xff, (c1) & 0xff, null);
			float[] f2=Color.RGBtoHSB((c2>>16) & 0xff, (c2>>8) & 0xff, (c2) & 0xff, null);
			
			double alpha1=f1[0]*2*Math.PI;
			double r1=f1[1]*f1[2];
			double alpha2=f2[0]*2*Math.PI;
			double r2=f2[1]*f2[2];
			
			f1[0]=(float)(r1*Math.cos(alpha1));
			f1[1]=(float)(r1*Math.sin(alpha1));
			f2[0]=(float)(r2*Math.cos(alpha2));
			f2[1]=(float)(r2*Math.sin(alpha2));
			
			for(int i=0;i<3;i++) {
				f2[i]=(float)(p*f1[i]+(1d-p)*f2[i]);
			}
			
			f1[2]=f2[2];
			if((r1==0)&&(r2==0)) {
				f1[0]=0;
				f2[0]=0;
			}else {
				f1[1]=(float)(Math.sqrt(f2[0]*f2[0]+f2[1]*f2[1]));
				f1[0]=(float)(Math.acos(f2[0]/f1[1]));
				f1[1]/=f1[2];
				if(f2[1]<0) f1[0]=(float)(-f1[0]+2*Math.PI);
				f1[0]/=(float)(2*Math.PI);
			}
			return Color.HSBtoRGB(f1[0], f1[1], f1[2]);
		}
		return null;
	}
}