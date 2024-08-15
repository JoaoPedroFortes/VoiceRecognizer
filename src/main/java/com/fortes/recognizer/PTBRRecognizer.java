package com.fortes.recognizer;

import com.fortes.pojo.Result;
import com.fortes.util.ReconignerUtils;
import com.google.gson.Gson;
import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.AudioSystem;
import java.io.IOException;
import java.io.InputStream;

public class PTBRRecognizer implements IRecognizer {

	private static PTBRRecognizer instance;

	public static PTBRRecognizer getInstance() {
		if (instance == null) {
			instance = new PTBRRecognizer();
		}
		return instance;
	}

	public static final String MODEL_PATH = "/models/vosk-model-pt-fb-v0.1.1-pruned";

	Model model;
	Recognizer recognizer;

	public PTBRRecognizer() {
		try {
			LibVosk.setLogLevel(LogLevel.WARNINGS);
			model = ReconignerUtils.loadModel(MODEL_PATH);
			recognizer = new Recognizer(model, 16000);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Result execute(String audioPath) {
		try {
			System.out.println("Reconhecendo texto...");

			var bufferedInputStream = ReconignerUtils.getAudioData(audioPath);

			try (InputStream audioStream = AudioSystem.getAudioInputStream(bufferedInputStream);) {
				if (audioStream == null) {
					throw new IllegalArgumentException("Arquivo de áudio não encontrado: " + audioPath);
				}

				int bufferSize = 4096;
				byte[] buffer = new byte[bufferSize];
				int bytesRead;

				while ((bytesRead = audioStream.read(buffer)) >= 0) {
					recognizer.acceptWaveForm(buffer, bytesRead);
				}

				// Imprimir o resultado final
				return getResult(recognizer.getFinalResult());

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Result getResult(String result) {
		return new Gson().fromJson(result, Result.class);
	}

	@Override
	public void close() {
		recognizer.close();
		model.close();
	}


}
