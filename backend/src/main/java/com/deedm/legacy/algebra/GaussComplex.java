package com.deedm.legacy.algebra;

public class GaussComplex {
	int real = 0;
	int image = 0;

	public GaussComplex(int a, int b) {
		real = a;
		image = b;
	}
	
	public int getReal() {
		return real;
	}
	
	public int getImage() {
		return image;
	}
	
	public boolean isZero() {
		return (real==0 && image == 0);
	}
	
	public boolean isUnit() {
		return (real==1 && image == 0);
	}
	
	public GaussComplex add(GaussComplex other) {
		return new GaussComplex(real+other.real, image+other.image);
	}
	
	public GaussComplex minus(GaussComplex other) {
		return new GaussComplex(real-other.real, image-other.image);
	}

	public GaussComplex times(GaussComplex other) {
		return new GaussComplex(real*other.real-image*other.image, image*other.real+real*other.image);
	}
	
	public int normal() {
		return real*real + image*image;
	}
	
	public GaussComplex div(GaussComplex other) {
		int norm = other.normal();
		
		int treal = real*other.real+image*other.image;
		int timage = image*other.real-real*other.image;
		double dreal = ((double)treal)/norm;
		double dimage = ((double)timage)/norm;
		
		int qreal = (int)Math.round(dreal);
		int qimage = (int)Math.round(dimage);
		
//		System.out.println("In div: this = " + this + ", other = " + other + ", normal = " + norm + ", treal = " + treal + ", timage = " + timage);
//		System.out.println("dreal = " + dreal + ", dimage = " + dimage + ", qreal = " + qreal + ", qimage = " + qimage);
		return new GaussComplex(qreal, qimage);
	}
	
	public GaussComplex mod(GaussComplex other) {
		int norm = other.normal();
		double dreal = ((double)(real*other.real+image*other.image))/norm;
		double dimae = ((double)(image*other.real-real*other.image))/norm;
		
		int qreal = (int)Math.round(dreal);
		int qimage = (int)Math.round(dimae);
		
		int rreal = qreal*other.real-qimage*other.image;
		int rimage = qimage*other.real+qreal*other.image;
		
		return new GaussComplex(real-rreal, image-rimage);
	}
	
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof GaussComplex)) return false;
		GaussComplex other = (GaussComplex)obj;
		
		return (real == other.real && image == other.image);
	}
	
	public int compare(GaussComplex other) {
		if (real == other.real && image == other.image) return 0;
		
		if (real == 0 && image == 0) return -1;
		if (other.real == 0 && other.image == 0) return 1;
		
		if (real == 1 && image == 0) return -1;
		if (other.real == 1 && other.image == 0) return 1;
		
		if (image == 0 && other.image == 0) {
			return real-other.real;
		}
		if (image == 0 && other.image != 0) return -1;
		if (other.image == 0 && image !=0) return 1;
		
		int normDiff = normal() - other.normal();
		if (normDiff < 0) return -1;
		else if (normDiff > 0) return 1;
		else if (real == other.real) return image-other.image;
		else return real-other.real;
	}
	
	public String toString() {
		if (image == 0) return real+"";
		if (real == 0) {
			if (image == 1) return "i";
			else if (image == -1) return "-i";
			else return image+"i";
		}
		if (image == 1) return real+"+i";
		if (image == -1) return real+"-i";
		if (image < 0) return real+""+image+"i";
		else return real+"+"+image+"i";
	}
	
	public String toLaTexString() {
		if (image == 0) return real+"";
		if (real == 0) {
			if (image == 1) return "\\textrm{i}";
			else if (image == -1) return "-\\textrm{i}";
			else return image+"\\textrm{i}";
		}
		if (image == 1) return real+"+\\textrm{i}";
		if (image == -1) return real+"-\\textrm{i}";
		if (image < 0) return real+""+image+"\\textrm{i}";
		else return real+"+"+image+"\\textrm{i}";
	}

}
