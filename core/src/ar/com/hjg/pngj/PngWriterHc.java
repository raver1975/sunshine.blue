package ar.com.hjg.pngj;

import java.io.File;
import java.io.OutputStream;

import ar.com.hjg.pngj.pixels.PixelsWriter;
import ar.com.hjg.pngj.pixels.PixelsWriterMultiple;
import com.badlogic.gdx.files.FileHandle;

/** Pngwriter with High compression EXPERIMENTAL */
public class PngWriterHc extends PngWriter {

	public PngWriterHc(FileHandle file, ImageInfo imgInfo, boolean allowoverwrite) {
		super(file, imgInfo, allowoverwrite);
		setFilterType(FilterType.FILTER_SUPER_ADAPTIVE);
	}

	public PngWriterHc(FileHandle file, ImageInfo imgInfo) {
		super(file, imgInfo);
	}

	public PngWriterHc(OutputStream outputStream, ImageInfo imgInfo) {
		super(outputStream, imgInfo);
	}

	@Override
	protected PixelsWriter createPixelsWriter(ImageInfo imginfo) {
		PixelsWriterMultiple pw = new PixelsWriterMultiple(imginfo);
		return pw;
	}

	public PixelsWriterMultiple getPixelWriterMultiple() {
		return (PixelsWriterMultiple) pixelsWriter;
	}

}
