package com.fortes.util;

import org.vosk.Model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;


public class ReconignerUtils {

	public static final Random random = new Random();

	/**
	 * Carrega um modelo nos resources
	 *
	 *
	 * @param modelPath
	 * @return
	 * @throws Exception
	 */
	public static Model loadModel(String modelPath) throws Exception {
		return new Model(getPathFromResource(modelPath));
	}

	/**
	 * Carrega um caminho de arquivo nos resources
	 *
	 * @param modelPath
	 * @return
	 * @throws Exception
	 */
	public static String getPathFromResource(String path) throws Exception {
		File file = new File(ReconignerUtils.class.getResource(path).toURI());
		return file.getAbsolutePath();
	}

	public static BufferedInputStream getAudioData(String audioPath) throws Exception {
		return new BufferedInputStream(new FileInputStream(audioPath));
	}


	public static File createFile(String filePath) {
		File outputFile = new File(filePath);

		File parentDir = outputFile.getParentFile();
		if (parentDir != null && !parentDir.exists()) {
			parentDir.mkdirs();
		}

		if(!outputFile.exists()){
			try {
				outputFile.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return outputFile;
	}

	public static String createRandomFileName() {
		return String.format("file-%s", random.nextInt(1000));
	}
}
