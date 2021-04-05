package ar.com.hjg.pngj;

import com.badlogic.gdx.files.FileHandle;

import java.io.File;
import java.io.InputStream;

/**
 * Trivial extension of {@link PngReader} that uses {@link ImageLineByte}
 * <p>
 * The factory is set at construction time. Remember that this could still be
 * changed at runtime.
 */
public class PngReaderByte extends PngReader {

	public PngReaderByte(FileHandle file) {
		super(file);
		setLineSetFactory(ImageLineSetDefault.getFactoryByte());
	}

	public PngReaderByte(InputStream inputStream) {
		super(inputStream);
		setLineSetFactory(ImageLineSetDefault.getFactoryByte());
	}

	/**
	 * Utility method that casts {@link #readRow()} return to
	 * {@link ImageLineByte}.
	 */
	public ImageLineByte readRowByte() {
		return (ImageLineByte) readRow();
	}

}
