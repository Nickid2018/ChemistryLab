package com.github.nickid2018.chemistrylab.util;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.zip.*;
import org.apache.commons.io.*;

public class ZipFileLocation implements ResourceLocation {

	private String file;
	private ZipFile zipFile;
	private Map<String, File> extracted = new HashMap<>();
	private Map<String, ByteArrayOutputStream> out = new HashMap<>();

	public ZipFileLocation(String file) throws IOException {
		this.file = file;
		zipFile = new ZipFile("resources/" + file + ".zip");
	}

	@Override
	public InputStream getResourceAsStream(String ref) {
		String at = formatPath(ref);
		InputStream is;
		try {
			is = zipFile.getInputStream(zipFile.getEntry(at));
		} catch (Exception e) {
			is = null;
		}
		return is;
	}

	@Override
	public URL getResource(String ref) {
		String at = formatPath(ref);
		try {
			if (extracted.containsKey(at))
				return extracted.get(at).toURI().toURL();
			File tmp = File.createTempFile("", "");
			tmp.deleteOnExit();
			ZipEntry entry = zipFile.getEntry(at);
			FileOutputStream fos = new FileOutputStream(tmp);
			InputStream is = zipFile.getInputStream(entry);
			IOUtils.copy(is, fos);
			is.close();
			fos.close();
			extracted.put(at, tmp);
			return tmp.toURI().toURL();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public OutputStream getOutputStream(String ref) {
		String at = formatPath(ref);
		if (out.containsKey(at))
			return out.get(at);
		ByteArrayOutputStream baos;
		out.put(at, baos = new ByteArrayOutputStream());
		return baos;
	}

	public void flushFile() throws IOException {
		if (out.isEmpty())
			return;
		ByteArrayOutputStream outside = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(outside);
		ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile.getName()));
		ZipEntry ze;
		while ((ze = zis.getNextEntry()) != null) {
			ZipEntry zew = new ZipEntry(ze.getName());
			zos.putNextEntry(zew);
			if (ze.isDirectory())
				continue;
			if (out.containsKey(ze.getName())) {
				out.get(ze.getName()).writeTo(zos);
			} else {
				InputStream is = zipFile.getInputStream(ze);
				IOUtils.copy(is, zos);
			}
			zis.closeEntry();
		}
		zos.flush();
		zos.close();
		zis.close();
		FileOutputStream fos = new FileOutputStream(zipFile.getName());
		outside.writeTo(fos);
		fos.close();
	}

	private String formatPath(String ref) {
		String at;
		if (!ResourceManager.getCanFuzzy()) {
			String[] sps = ref.split(":");
			if (sps.length != 2)
				return null;
			if (!sps[0].equals(file))
				return null;
			at = sps[1].replace('\\', '/');
		} else
			at = ref;
		return at;
	}
}
