package com.klemstinegroup.sunshineblue.engine.util;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ByteArray;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MemoryFileHandle extends FileHandle {
   public ByteArray ba = new ByteArray();

//	public MemoryFileHandle(ZipFile archive, File file) {
//		super(file, FileType.Classpath);
//		this.archive = archive;
//		archiveEntry = this.archive.getEntry(file.getPath());
//	}

    public MemoryFileHandle(byte[] b){
        ba.addAll(b);
    }

    public MemoryFileHandle(String fileName) {
        super(fileName.replace('\\', '/'), FileType.Classpath);
//		this.archive = archive;
//		this.archiveEntry = archive.getEntry(fileName.replace('\\', '/'));
    }

    public MemoryFileHandle() {
        super(UUID.randomUUID().toString().replaceAll("-", ""), FileType.Classpath);
    }

//	@Override
//	public FileHandle child (String name) {
//		name = name.replace('\\', '/');
//		if (file.getPath().length() == 0) return new MemoryFileHandle(archive, new File(name));
//		return new MemoryFileHandle(archive, new File(file, name));
//	}

//	@Override
//	public FileHandle sibling (String name) {
//		name = name.replace('\\', '/');
//		if (file.getPath().length() == 0) throw new GdxRuntimeException("Cannot get the sibling of the root.");
//		return new MemoryFileHandle(archive, new File(file.getParent(), name));
//	}

//	@Override
//	public FileHandle parent () {
//		File parent = file.getParentFile();
//		if (parent == null) {
//			if (type == FileType.Absolute)
//				parent = new File("/");
//			else
//				parent = new File("");
//		}
//		return new MemoryFileHandle(archive, parent);
//	}


    @Override
    public OutputStream write(boolean append) {
//        System.out.println("writing");
        if (!append) {
            ba.clear();
        }
        return new OutputStream() {
            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                ba.addAll(b,off,len);
            }

            @Override
            public void write(int b) throws IOException {
                ba.add((byte) b);
            }
        };
    }

    @Override
    public InputStream read() {
//        System.out.println("reading");
        return new InputStream() {
            int cnt = 0;

            @Override
            public int read() throws IOException {
                if (cnt >= ba.size) {
                    return -1;
                }
                return ba.get(cnt++) & 0xff;
            }
        };
    }

    public void write(InputStream input, boolean append) {
        OutputStream output = null;
        try {
            output = write(append);
            StreamUtils.copyStream(input, output);
        } catch (Exception ex) {
            throw new GdxRuntimeException("Error stream writing to file: " + file + " (" + type + ")", ex);
        } finally {
            StreamUtils.closeQuietly(input);
            StreamUtils.closeQuietly(output);
        }

    }

//	@Override
//	public boolean exists() {
//		return archiveEntry != null;
//	}

    @Override
    public long length() {
        return ba.size;
    }

//	@Override
//	public long lastModified () {
//		return archiveEntry.getTime();
//	}

    @Override
    public byte[] readBytes() {
        return ba.toArray();
    }

    @Override
    public void writeBytes(byte[] bytes, boolean append) {
        if (!append)ba.clear();
        ba.addAll(bytes);
    }

    @Override
    public String toString() {
        return "Memory file:"+ba.size;
    }
}