package com.klemstinegroup.sunshinelab.engine.util;

import java.util.zip.ZipFile;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

public class ArchiveFileHandleResolver implements FileHandleResolver {

  private final ZipFile archive;
	
	public ArchiveFileHandleResolver (ZipFile archive) {
		this.archive = archive;
	}
	
	@Override
	public FileHandle resolve (String fileName) {
		return new ArchiveFileHandle(archive, fileName);
	}

}